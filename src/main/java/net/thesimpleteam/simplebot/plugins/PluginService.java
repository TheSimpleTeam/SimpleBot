package net.thesimpleteam.simplebot.plugins;

import fr.thesimpleteam.pluginapi.Plugin;
import fr.thesimpleteam.pluginapi.event.Event;
import fr.thesimpleteam.pluginapi.socket.MessageType;
import fr.thesimpleteam.pluginapi.socket.SocketMessage;
import net.thesimpleteam.simplebot.SimpleBot;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class PluginService {

    private PluginService() {}

    private static final boolean alreadyLoaded = false;
    private static ObjectOutputStream os;
    private static Process p;

    public static void startPluginLoader() {
        if(alreadyLoaded) return;
        SimpleBot.LOGGER.info("Starting plugin loader...");
        Path javaPath = Path.of(ProcessHandle.current()
                .info()
                .command()
                .orElseThrow());
        ProcessBuilder builder = new ProcessBuilder(javaPath.toString(), "-jar", "plugins/PluginLoader.jar");
        builder.inheritIO();
        try {
            p = builder.start();
            new Thread(() -> {
                Socket socket = null;
                while(socket == null) {
                    try {
                        socket = new Socket("localhost", 4444);
                    } catch (IOException e) {
                        if(!(e instanceof ConnectException)) throw new RuntimeException(e);
                    }
                }
                try {
                    os = new ObjectOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try(ObjectInputStream is = new ObjectInputStream(socket.getInputStream())) {
                    os.writeObject("ping");
                    while(true) {
                        Object o;
                        try {
                            o = is.readObject();
                        } catch (EOFException | SocketException e) {
                            break;
                        }

                        if(o instanceof String s) {
                            SimpleBot.LOGGER.info(s);
                        } else if(o instanceof Plugin pl) {
                            SimpleBot.LOGGER.info(pl.name() + " loaded");
                        } else if(o instanceof SocketMessage msg) {
                            if(msg.getMessageType() == MessageType.REPLY) {
                                String channelId = msg.getMessage().substring(msg.getMessage().lastIndexOf(':') + 1);
                                String content = msg.getMessage().substring(0, msg.getMessage().lastIndexOf(':'));
                                SimpleBot.getJda().getTextChannelById(channelId).sendMessage(content).queue();
                            }
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    os.close();
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                p.destroy();
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void callEvent(Event event) {
        if(os == null) return;
        try {
            os.writeObject(new SocketMessage(event, MessageType.TRIGGER_EVENT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() throws IOException {
        os.writeObject(MessageType.SHUTDOWN);
        SimpleBot.LOGGER.info("Shutting down...");
        try {
            p.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            p.destroyForcibly();
            throw new RuntimeException(e);
        }
    }

    public static void blockThread() throws InterruptedException {
        p.waitFor();
    }
}
