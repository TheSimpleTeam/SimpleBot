package net.thesimpleteam.simplebot.plugins;

import net.thesimpleteam.pluginapi.command.CommandEvent;
import net.thesimpleteam.pluginapi.command.CommandInfo;
import net.thesimpleteam.pluginapi.event.Event;
import net.thesimpleteam.pluginapi.exceptions.NotImplementedException;
import net.thesimpleteam.pluginapi.message.Message;
import net.thesimpleteam.pluginapi.plugins.Author;
import net.thesimpleteam.pluginapi.plugins.Plugin;
import net.thesimpleteam.pluginapi.socket.MessageType;
import net.thesimpleteam.pluginapi.socket.SocketMessage;
import net.thesimpleteam.pluginapi.utils.SerializationUtils;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.plugins.utils.JdaToPluginObject;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static net.thesimpleteam.simplebot.SimpleBot.LOGGER;

public class PluginService {

    private PluginService() {}

    private static ObjectOutputStream outputStream;

    private static final List<Plugin> plugins = new ArrayList<>();
    private static final List<CommandInfo> commands = new ArrayList<>();
    private static final boolean alreadyLoaded = false;
    private static Process p;

    public static void startPluginLoader() {
        if(alreadyLoaded) return;
        LOGGER.info("Starting plugin loader...");
        Path javaPath = Path.of(ProcessHandle.current()
                .info()
                .command()
                .orElseThrow());
        //ProcessBuilder builder = new ProcessBuilder(javaPath.toString(), "-jar", "plugins/PluginLoader.jar");
        //builder.inheritIO();
        try {
            //p = builder.start();
            p = null;
            startSocketServer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void startSocketServer() {
        SimpleBot.getExecutorService().execute(() -> {
            LOGGER.info("Starting socket server...");
            try(ServerSocket server = new ServerSocket(10909);
                Socket socket = server.accept();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                oos.flush();
                LOGGER.info("Socket server started!");
                outputStream = oos;
                oos.writeObject(new SocketMessage(MessageType.GET_PLUGINS));
                oos.flush();
                while(true) {
                    SocketMessage message = SerializationUtils.deserialize((byte[]) ois.readObject(), SocketMessage.class);
                    switch (Objects.requireNonNull(message).getMessageType()) {
                        case GET_USER -> {
                            LOGGER.info("Received user request");
                            //FIXME: This line completely blocks the thread
                            Author object = JdaToPluginObject.toAuthor(Objects.requireNonNull(SimpleBot.getJda().getUserById(message.getObject(String.class))));
                            SocketMessage obj = new SocketMessage(MessageType.SEND_USER, object).setId(message.getId());
                            //Write this object to the socket using an array of bytes
                            oos.writeObject(obj);
                            oos.flush();
                            LOGGER.info("Sent user");
                        }
                        case SEND_PLUGINS -> {
                            plugins.addAll(List.of(message.getObject(Plugin[].class)));
                            LOGGER.info("Loaded " + plugins.size() + " plugins!");
                        }
                        case SEND_REGISTERED_COMMANDS -> {
                            commands.addAll(List.of(message.getObject(CommandInfo[].class)));
                            LOGGER.info("Loaded " + commands.size() + " commands!");
                        }
                        case REPLY -> {
                            Message msg = message.getObject(Message.class);
                            var channel = SimpleBot.getJda().getTextChannelById(msg.getChannelID());
                            if(channel == null) {
                                LOGGER.warning(msg.getFromPlugin() + " tried to send a message to a channel that doesn't exist!");
                                return;
                            }
                            channel.sendMessage(msg.getMessageContent()).queue();
                        }
                        case PONG -> LOGGER.info("Pong received!");
                        default -> LOGGER.warning("Unknown message type received: " + message.getMessageType());
                    }
                }
            }catch (EOFException ignored) {} catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void stop() throws IOException {
        LOGGER.info("Shutting down...");
        /*try {
            p.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            p.destroyForcibly();
            throw new RuntimeException(e);
        }*/
    }

    public static void callEvent(Event event) {
        try {
            outputStream.writeObject(new SocketMessage(MessageType.TRIGGER_EVENTS, event));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void executeCommand(CommandInfo command, Message message) {
        try {
            outputStream.writeObject(new SocketMessage(MessageType.EXECUTE_COMMAND, new CommandEvent(command, message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<CommandInfo> getCommands() {
        return commands;
    }

    public static Optional<CommandInfo> findCommand(String nameOrAlias) {
        return commands.stream().filter(commandInfo -> Stream.concat(Stream.of(commandInfo.name()), Arrays.stream(commandInfo.aliases())).anyMatch(nameOrAlias::equalsIgnoreCase)).findFirst();
    }

    public static List<Plugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }
}