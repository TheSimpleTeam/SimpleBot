/*
 * MIT License
 *
 * Copyright (c) 2021 minemobs
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

package fr.noalegeek.pepite_dor_bot.plugin;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.noalegeek.pepite_dor_bot.cli.CLI;
import net.thesimpleteam.simplebotplugin.BasePlugin;
import net.thesimpleteam.simplebotplugin.IPluginLoader;
import net.thesimpleteam.simplebotplugin.annotation.EventHandler;
import net.thesimpleteam.simplebotplugin.commands.CLICommand;
import net.thesimpleteam.simplebotplugin.commands.Command;
import net.thesimpleteam.simplebotplugin.event.Event;
import net.thesimpleteam.simplebotplugin.listener.Listener;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PluginLoader implements IPluginLoader {

    public static class Plugin {
        private final BasePlugin basePlugin;
        private final File file;
        private final ImmutableList<Class<?>> pluginClasses;
        private final List<Command> commands;
        private Listener[] listeners;

        private Plugin() {
            this.basePlugin = null;
            this.file = null;
            this.pluginClasses = null;
            this.listeners = null;
            this.commands = new ArrayList<>();
        }

        private Plugin(BasePlugin basePlugin, File file, ImmutableList<Class<?>> pluginClasses) {
            this.basePlugin = basePlugin;
            this.file = file;
            this.pluginClasses = pluginClasses;
            this.commands = new ArrayList<>();
        }

        public BasePlugin getBasePlugin() {
            return basePlugin;
        }

        public File getFile() {
            return file;
        }

        private ImmutableList<Class<?>> getPluginClasses() {
            return pluginClasses;
        }

        private Listener[] getListeners() {
            return listeners;
        }

        public List<Command> getCommands() {
            return commands;
        }
    }

    //TO FIX: Block reflection access to the Main class.

    private final File pluginFolder;
    private final List<Plugin> plugins = new ArrayList<>();
    public CLI cli;

    public PluginLoader(File pluginFolder) {
        this.pluginFolder = pluginFolder;
    }

    @Override
    public void callEvent(Event event) {
        plugins.stream().filter(plugin -> plugin.listeners != null && plugin.listeners.length > 0).map(Plugin::getListeners).forEach(listeners -> {
            var method = Arrays.stream(listeners).filter(listener -> Arrays.stream(listener.getClass().getMethods())
                    .anyMatch(m -> m.getParameterCount() == 1 &&
                            m.getParameterTypes()[0].isAssignableFrom(event.getClass()) &&
                            m.isAnnotationPresent(EventHandler.class))).toList();
            method.forEach(listener -> Arrays.stream(listener.getClass().getMethods()).filter(m -> m.getParameterCount() == 1 &&
                    m.getParameterTypes()[0].isAssignableFrom(event.getClass()) &&
                    m.isAnnotationPresent(EventHandler.class)).findAny().ifPresent(method1 -> {
                try {
                    //Modify the value of the private field "plugin" of the event using reflection.
                    FieldUtils.writeField(event, "plugin", plugins.stream().filter(plugin -> Arrays.stream(plugin.listeners).anyMatch(listener1 -> listener1 == listener))
                            .findFirst().get().basePlugin, true);
                    MethodUtils.getAccessibleMethod(method1).invoke(listener, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }));
        });
    }

    @Override
    public void addListener(BasePlugin plugin, Listener... listeners) {
        List<Listener> listenerList = new ArrayList<>(List.of(listeners));
        plugins.stream().filter(p -> p.getBasePlugin() == plugin).findAny().map(Plugin::getListeners).map(List::of).ifPresent(listenerList::addAll);
        plugins.stream().filter(p -> p.getBasePlugin() == plugin).findFirst().ifPresent(value -> value.listeners = listenerList.toArray(new Listener[0]));
    }

    @Override
    public void addCLICommand(CLICommand... commands) {
        if(cli == null) return;
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        if (!BasePlugin.class.isAssignableFrom(walker.getCallerClass())) return;
        Optional<Plugin> pluginOP = plugins.stream().filter(plugin -> plugin.getBasePlugin().getClass().getCanonicalName().equals(walker.getCallerClass().getCanonicalName())).findFirst();
        if (pluginOP.isEmpty()) return;
        System.out.println("Added " + commands.length + " commands from" + pluginOP.get().getBasePlugin().getName() + " to the CLI Command List.");
        cli.getPluginCommands().addAll(List.of(commands));
    }

    @Override
    public void addCommands(BasePlugin plugin, Command... commands) {
        System.out.println("Added " + commands.length + " commands from " + plugin.getName() + " to the Commands List.");
        if(plugins.stream().anyMatch(pl -> pl.getCommands().stream().map(Command::name).anyMatch(s -> Arrays.stream(commands).anyMatch(command -> command.name().equals(s))))) {
            System.out.println("A command with the same name as one of the commands you are trying to add already exists.");
            return;
        }
        plugins.stream().filter(pl -> pl.getBasePlugin() == plugin).findFirst().ifPresent(pl -> pl.commands.addAll(List.of(commands)));
    }

    public void loadPlugins() {
        for (File jarFile : FileUtils.listFiles(pluginFolder, new String[]{"jar"}, true)) {
            if(hashEquals(jarFile, getBlacklistedPlugins())) {
                System.out.println("Skipping " + jarFile.getName() + " because it is blacklisted.");
                return;
            }
            try {
                var classes = loadClasses(jarFile);
                @SuppressWarnings("unchecked")
                var plugin = classes.stream().filter(BasePlugin.class::isAssignableFrom)
                        .map(clazz -> ((Class<? extends BasePlugin>)clazz)).findFirst();
                if(plugin.isEmpty()) return;
                var pluginClass = plugin.get();
                if(pluginClass.getConstructors().length != 0 &&
                        Arrays.stream(pluginClass.getConstructors()).filter(constructor -> constructor.getParameterCount() == 0).findAny().isEmpty()) return;
                try {
                    BasePlugin basePlugin = pluginClass.getConstructor().newInstance();
                    plugins.add(new Plugin(basePlugin, jarFile, ImmutableList.copyOf(classes)));
                    FieldUtils.writeField(FieldUtils.getField(pluginClass, "loader", true), basePlugin, this, true);
                    basePlugin.onEnable();
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean hashEquals(File file, JsonObject hash) {
        return hash.has(getSha256(file));
    }

    private String getSha256(File file) {
        try {
            return DigestUtils.sha256Hex(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private JsonObject getBlacklistedPlugins() {
        File file = new File("blacklist.json");
        if(file.exists()) {
            try {
                return new Gson().fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), JsonObject.class);
            } catch (IOException ignored) {}
        }
        String blackListURL = "https://raw.githubusercontent.com/TheSimpleTeam/SimpleBot/main/blacklist.json";
        try {
            return new Gson().fromJson(new InputStreamReader(new URL(blackListURL).openStream()), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }

    private List<String> getClasseNames(String jarName) {
        ArrayList<String> classes = new ArrayList<>();

        try(JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName))) {
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (jarEntry.getName().endsWith(".class")) {
                    classes.add(jarEntry.getName().replaceAll("/", "\\."));
                }
            }
            jarFile.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    private List<Class<?>> loadClasses(File jarFile) throws MalformedURLException, ClassNotFoundException {
        var classesNames = getClasseNames(jarFile.getAbsolutePath()).stream()
                .map(FilenameUtils::getBaseName).toList();
        ClassLoader loader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()});
        List<Class<?>> classes = new ArrayList<>();
        for (String classesName : classesNames) {
            classes.add(loader.loadClass(classesName));
        }
        return classes;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
