/*
 * MIT License
 *
 * Copyright (c) 2022 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.thesimpleteam.loader;

import fr.thesimpleteam.pluginapi.BasePlugin;
import fr.thesimpleteam.pluginapi.IPluginLoader;
import fr.thesimpleteam.pluginapi.event.Event;
import fr.thesimpleteam.pluginapi.event.EventHandler;
import fr.thesimpleteam.pluginapi.event.Listener;
import fr.thesimpleteam.pluginapi.socket.MessageType;
import fr.thesimpleteam.pluginapi.socket.SocketMessage;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class PluginLoader implements IPluginLoader {

    private record Plugin(BasePlugin plugin, Path jarPath, List<Class<?>> pluginClasses, List<Listener> listeners) { }

    private final Path pluginFolder;
    private final List<Plugin> plugins = new ArrayList<>();
    private final boolean debug;
    private ObjectOutputStream os;
    public static final Logger LOGGER = Logger.getLogger("SBPL");

    PluginLoader(Path pluginFolder) {
        this.pluginFolder = pluginFolder;
        this.debug = false;
    }

    PluginLoader(Path pluginFolder, boolean debug) {
        this.pluginFolder = pluginFolder;
        this.debug = debug;
    }


    @Override
    public void callEvent(Event event) {
        plugins.stream().filter(plugin -> !plugin.listeners.isEmpty()).map(Plugin::listeners).forEach(listeners -> {
            //TODO: optimize because it's unreadable
            var methods = listeners.stream().filter(listener -> Arrays.stream(listener.getClass().getMethods())
                    .anyMatch(m -> m.getParameterCount() == 1 &&
                            m.getParameterTypes()[0].isAssignableFrom(event.getClass()) &&
                            m.isAnnotationPresent(EventHandler.class))).toList();
            methods.forEach(listener -> Arrays.stream(listener.getClass().getMethods()).filter(m -> m.getParameterCount() == 1 &&
                    m.getParameterTypes()[0].isAssignableFrom(event.getClass()) &&
                    m.isAnnotationPresent(EventHandler.class)).findAny().ifPresent(listenerMethod -> {
                        try {
                            Field plugin = Event.class.getDeclaredField("plugin");
                            plugin.trySetAccessible();
                            plugin.set(event, plugins.stream().filter(pl -> pl.listeners.stream().anyMatch(l -> l == listener))
                                    .findFirst().get().plugin);
                            listenerMethod.invoke(listener, event);
                        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
            }));
        });
    }

    @Override
    public void addListener(BasePlugin plugin, Listener... listeners) {
        plugins.stream().filter(pl -> pl.plugin == plugin).findFirst().ifPresent(pl -> pl.listeners.addAll(Arrays.asList(listeners)));
    }

    @Override
    public void reply(String message) {
        try {
            os.writeObject(new SocketMessage(message, MessageType.LOG_MESSAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void start() {
        try(Stream<Path> paths = Files.walk(pluginFolder)) {
            paths.filter(p -> p.getFileName().toString().endsWith(".jar") && !p.getFileName().toString().equals("PluginLoader.jar")).forEach(this::loadPlugin);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setupPacketSystem();
    }

    private void setupPacketSystem() {
        Socket socket;
        try(ServerSocket listener = new ServerSocket(4444)) {
            LOGGER.info("Waiting for PluginService to connect...");
            socket = listener.accept();
            LOGGER.info("PluginService connected!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            try(ObjectInputStream is = new ObjectInputStream(socket.getInputStream())) {
                os = new ObjectOutputStream(socket.getOutputStream());
                if(is.readObject() instanceof String s && s.equals("ping")) {
                    System.out.println("Pong!");
                }
                plugins.stream().map(Plugin::plugin).forEach(p -> {
                    try {
                        os.writeObject(p.getAnnotation());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                while(true) {
                    try {
                        Object o = is.readObject();
                        if(o instanceof SocketMessage sm) {
                            if (sm.getMessageType() == MessageType.TRIGGER_EVENT) {
                                callEvent((Event) sm.getObject());
                            }
                        }
                    } catch (EOFException e) {
                        break;
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                os.close();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void loadPlugin(Path jarFile) {
        try {
            var classes = loadClasses(jarFile);
            @SuppressWarnings("unchecked")
            Optional<? extends Class<? extends BasePlugin>> pluginClass = classes.stream()
                    .filter(BasePlugin.class::isAssignableFrom).map(clazz -> ((Class<? extends BasePlugin>)clazz)).findFirst();
            if(pluginClass.isEmpty() || (pluginClass.get().getConstructors().length != 0 &&
                    Arrays.stream(pluginClass.get().getConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findAny().isEmpty())) return;
            try {
                BasePlugin pl;
                Constructor<? extends BasePlugin> constructor = pluginClass.get().getConstructor();
                constructor.trySetAccessible();
                if(pluginClass.get().getAnnotation(fr.thesimpleteam.pluginapi.Plugin.class) == null) {
                    throw new IllegalStateException("The class " + pluginClass.get().getName() + " is not annotated with @Plugin");
                }
                pl = constructor.newInstance();
                plugins.add(new Plugin(pl, jarFile, classes, new ArrayList<>()));
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

    private List<Class<?>> loadClasses(Path jarFile) throws ClassNotFoundException {
        var classesNames = getClasseNames(jarFile.toString()).stream().map(this::removeExtension).toList();
        List<Class<?>> classes = new ArrayList<>();
        try(URLClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toUri().toURL()})) {
            for (String classesName : classesNames) {
                classes.add(loader.loadClass(classesName));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }

    private List<String> getClasseNames(String jarName) {
        ArrayList<String> classes = new ArrayList<>();

        if (debug) LOGGER.info(() -> "Found " + jarName + " in plugin folder");
        try(JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName))) {
            AtomicReference<JarEntry> jarEntry = new AtomicReference<>();

            while (true) {
                jarEntry.set(jarFile.getNextJarEntry());
                if (jarEntry.get() == null) {
                    break;
                }

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
}
