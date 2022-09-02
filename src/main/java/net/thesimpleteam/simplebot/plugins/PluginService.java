package net.thesimpleteam.simplebot.plugins;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.thesimpleteam.pluginapi.event.MessageReceiveEvent;
import net.thesimpleteam.pluginapi.exceptions.NotImplementedException;
import net.thesimpleteam.pluginapi.message.Message;
import net.thesimpleteam.pluginapi.plugins.Plugin;
import net.thesimpleteam.pluginapi.socket.MessageType;
import net.thesimpleteam.pluginapi.socket.SocketMessage;
import net.thesimpleteam.pluginapi.utils.SerializationUtils;
import net.thesimpleteam.simplebot.SimpleBot;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.thesimpleteam.simplebot.SimpleBot.LOGGER;

public class PluginService {

    private PluginService() {}

    private static ObjectOutputStream outputStream;

    private static final List<Plugin> plugins = new ArrayList<>();
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
                        case SEND_PLUGINS -> {
                            plugins.addAll(List.of(message.getObject(Plugin[].class)));
                            LOGGER.info("Loaded " + plugins.size() + " plugins!");
                        }
                        case SEND_REGISTERED_COMMANDS -> {
                            throw new NotImplementedException();
                        }
                        case REPLY -> {
                            Message msg = message.getObject(Message.class);
                            JDA jda = SimpleBot.getJda();
                            if(jda.getGuildChannelById(msg.getChannelID()) == null || jda.getGuildChannelById(msg.getChannelID()).getType() != ChannelType.TEXT) {
                                LOGGER.warning(msg.getFromPlugin() + " tried to send a message to a channel that doesn't exist!");
                                return;
                            }
                            jda.getTextChannelById(msg.getChannelID()).sendMessage(msg.getMessageContent()).queue();
                        }
                        case PONG -> LOGGER.info("Pong received!");
                        default -> throw new NotImplementedException();
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

    public static void callEvent(MessageReceiveEvent messageReceiveEvent) {
        try {
            outputStream.writeObject(new SocketMessage(MessageType.TRIGGER_EVENTS, messageReceiveEvent));
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Plugin> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }
}
