package net.thesimpleteam.loader;

import net.thesimpleteam.pluginapi.command.Command;
import net.thesimpleteam.pluginapi.command.CommandEvent;
import net.thesimpleteam.pluginapi.command.CommandInfo;
import net.thesimpleteam.pluginapi.event.Event;
import net.thesimpleteam.pluginapi.event.EventHandler;
import net.thesimpleteam.pluginapi.event.Listener;
import net.thesimpleteam.pluginapi.message.Message;
import net.thesimpleteam.pluginapi.plugins.BasePlugin;
import net.thesimpleteam.pluginapi.plugins.IPluginLoader;
import net.thesimpleteam.pluginapi.plugins.Author;
import net.thesimpleteam.pluginapi.socket.MessageType;
import net.thesimpleteam.pluginapi.socket.SocketMessage;
import net.thesimpleteam.pluginapi.socket.SocketMessageListener;
import net.thesimpleteam.pluginapi.utils.BlockingHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class PluginLoader implements IPluginLoader, SocketMessageListener {

    private record Plugin(BasePlugin plugin, Path jarPath, List<Class<?>> pluginClasses, List<Listener> listeners, List<Command> commands) { }

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    public static final BlockingHashMap<UUID, Future<SocketMessage>> SOCKET_MESSAGES = new BlockingHashMap<>();
    public static final Logger LOGGER = Logger.getLogger("SBPL");
    private final List<Plugin> plugins = new ArrayList<>();
    private final Path pluginFolder;
    private final boolean debug;
    private SocketClient client;
    PluginLoader(Path pluginFolder) {
        this.pluginFolder = pluginFolder;
        this.debug = false;
    }

    PluginLoader(Path pluginFolder, boolean debug) {
        this.pluginFolder = pluginFolder;
        this.debug = debug;
    }

    void start() {
        try(Stream<Path> paths = Files.walk(pluginFolder)) {
            paths.filter(p -> p.getFileName().toString().endsWith(".jar") && !p.getFileName().toString().equals("PluginLoader.jar")).forEach(this::loadPlugin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        startSocketServer();
    }

    private void startSocketServer() {
        EXECUTOR_SERVICE.execute(() -> {
            LOGGER.info("Connecting to socket server...");
            this.client = new SocketClient(this);
            LOGGER.info("Connected to socket server!");
            this.client.sendMessage(MessageType.SEND_REGISTERED_COMMANDS, plugins.stream().map(Plugin::commands)
                    .flatMap(commands -> commands.stream().map(Command::getInfo)).toArray(CommandInfo[]::new));
        });
    }

    @Override
    public void onMessage(SocketMessage message) {
        LOGGER.info("Received message: " + message.getMessageType());
        switch (message.getMessageType()) {
            case GET_PLUGINS -> client.sendMessage(new SocketMessage(MessageType.SEND_PLUGINS, getPluginsInfos()));
            case TRIGGER_EVENTS -> {
                Event event = message.getObject(Event.class);
                callEvent(event);
            }
            case SHUTDOWN -> stop();
            case PING -> client.sendMessage(MessageType.PONG);
            case EXECUTE_COMMAND -> {
                CommandEvent event = message.getObject(CommandEvent.class);
                callCommandEvent(event);
            }
            case SEND_USER -> {
                SOCKET_MESSAGES.put(message.getId(), EXECUTOR_SERVICE.submit(() -> message));
                LOGGER.info(() -> "Added " + message.getId() + " to the blocking map : " + SOCKET_MESSAGES.containsKey(message.getId()));
            }
            default -> {}
        }
    }

    private void callCommandEvent(CommandEvent event) {
        try {
            Field command = CommandEvent.class.getDeclaredField("command");
            command.setAccessible(true);
            CommandInfo info = event.getCommandInfo();
            command.set(event, getCommand(info));
            if(command.get(event) == null) {
                LOGGER.warning("Command not found: " + info.name());
                return;
            }
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.SEVERE, "Error while calling command event", e);
            return;
        }
        event.getCommand().execute(event);
    }

    private Command getCommand(CommandInfo info) {
        return plugins.stream().map(Plugin::commands).flatMap(Collection::stream).filter(command -> command.getInfo().equals(info)).findFirst().orElse(null);
    }

    private void stop() {
        plugins.forEach(pl -> pl.plugin.onDisable());
        System.exit(0);
    }

    @Override
    public void addCommand(BasePlugin plugin, Command... command) {
        plugins.stream().filter(p -> p.plugin.equals(plugin)).findFirst().ifPresent(p -> p.commands.addAll(Arrays.asList(command)));
    }

    @Override
    public Author getUser(String id) {
        Optional<SocketMessage> socketMessage = client.sendMessage(MessageType.GET_USER, id);
        if(socketMessage.isEmpty()) return null;
        return socketMessage.get().getObject(Author.class);
    }

    @Override
    public void addListener(BasePlugin plugin, Listener... listeners) {
        plugins.stream().filter(pl -> pl.plugin == plugin).findFirst().ifPresent(pl -> pl.listeners.addAll(Arrays.asList(listeners)));
    }

    @Override
    public void reply(String message, String channelId) {
        var msg = new Message(message, channelId, null);
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        plugins.stream().map(Plugin::plugin).filter(pl -> Arrays.stream(trace).map(this::toClass).filter(Objects::nonNull)
                .anyMatch(clazz -> pl.getClass() == clazz)).findFirst().ifPresent(basePlugin -> {
            try {
                Field fromPlugin = Message.class.getDeclaredField("fromPlugin");
                fromPlugin.setAccessible(true);
                fromPlugin.set(msg, basePlugin);
                fromPlugin.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.log(Level.SEVERE, "Could not set fromPlugin field", e);
                throw new RuntimeException(e);
            }
        });
        client.sendMessage(new SocketMessage(MessageType.REPLY, msg));
    }

    private Class<?> toClass(StackTraceElement element) {
        try {
            return Class.forName(element.getClassName());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void callEvent(Event event) {
        plugins.stream().filter(plugin -> !plugin.listeners.isEmpty()).map(Plugin::listeners).forEach(listeners -> {
            listeners.forEach(listener -> Arrays.stream(listener.getClass().getMethods())
                    .filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].isAssignableFrom(event.getClass()) &&
                    m.isAnnotationPresent(EventHandler.class)).findAny()
                    .ifPresent(listenerMethod -> setPluginFieldInListener(event, listener, listenerMethod)));
        });
    }

    private void setPluginFieldInListener(Event event, Listener listener, Method listenerMethod) {
        plugins.stream().filter(pl -> pl.listeners.stream().anyMatch(Predicate.isEqual(listener))).findFirst().ifPresent(pl -> {
            try {
                Field plugin = Event.class.getDeclaredField("plugin");
                plugin.trySetAccessible();
                plugin.set(event, pl.plugin);
                listenerMethod.invoke(listener, event);
            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.log(Level.SEVERE, "Error while calling event", e);
                throw new RuntimeException(e);
            }
        });
    }

    private void loadPlugin(Path jarFile) {
        try {
            var classes = loadClasses(jarFile);
            Optional<? extends Class<? extends BasePlugin>> pluginClass = classes.stream().map(this::toBasePlugin)
                    .filter(Objects::nonNull).findFirst();
            if(pluginClass.isEmpty() || (pluginClass.get().getConstructors().length != 0 &&
                    Arrays.stream(pluginClass.get().getConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findAny().isEmpty())) return;
            try {
                BasePlugin pl;
                Constructor<? extends BasePlugin> constructor = pluginClass.get().getConstructor();
                constructor.trySetAccessible();
                if(pluginClass.get().getAnnotation(net.thesimpleteam.pluginapi.plugins.Plugin.class) == null) {
                    throw new IllegalStateException("The class " + pluginClass.get().getName() + " is not annotated with @Plugin");
                }
                pl = constructor.newInstance();
                plugins.add(new Plugin(pl, jarFile, classes, new ArrayList<>(), new ArrayList<>()));
                Field loader = BasePlugin.class.getDeclaredField("loader");
                loader.trySetAccessible();
                loader.set(pl, this);
                pl.onEnable();
                System.out.println("Loaded plugin: " + pl.getName());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private Class<? extends BasePlugin> toBasePlugin(Class<?> clazz) {
        return BasePlugin.class.isAssignableFrom(clazz) && !clazz.isInterface() ? (Class<? extends BasePlugin>) clazz : null;
    }

    private List<Class<?>> loadClasses(Path jarFile) throws ClassNotFoundException {
        var classesNames = getClassesNames(jarFile.toString()).stream().map(this::removeExtension).toList();
        List<Class<?>> classes = new ArrayList<>();
        try(URLClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toUri().toURL()})) {
            for (String classesName : classesNames) {
                classes.add(loader.loadClass(classesName));
            }
        } catch (IOException | NoClassDefFoundError e) {
            LOGGER.log(Level.SEVERE, "Could not load classes from " + removeExtension(jarFile.toString()), e);
        }
        return classes;
    }

    private List<String> getClassesNames(String jarName) {
        ArrayList<String> classes = new ArrayList<>();

        if (debug) LOGGER.info(() -> "Found " + jarName + " in plugin folder");
        try(JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName))) {
            AtomicReference<JarEntry> jarEntry = new AtomicReference<>();

            while (true) {
                jarEntry.set(jarFile.getNextJarEntry());
                if (jarEntry.get() == null) break;
                if (jarEntry.get().getName().endsWith(".class")) {
                    if (debug) LOGGER.info(() -> "Found " + jarEntry.get().getName().replace("/", "."));
                    classes.add(jarEntry.get().getName().replace("/", "."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private String removeExtension(String filePath) {
        File f = new File(filePath);
        if (f.isDirectory()) return filePath;
        String name = f.getName();

        final int lastPeriodPos = name.lastIndexOf('.');
        if (lastPeriodPos <= 0) {
            return filePath;
        } else {
            File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
            return renamed.getPath();
        }
    }

    public net.thesimpleteam.pluginapi.plugins.Plugin[] getPluginsInfos() {
        return plugins.stream().map(Plugin::plugin)
                .map(BasePlugin::getAnnotation)
                .toArray(net.thesimpleteam.pluginapi.plugins.Plugin[]::new);
    }
}