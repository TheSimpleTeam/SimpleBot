package net.thesimpleteam.simplebot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.simpleteam.simplebot.api.Server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.thesimpleteam.simplebot.cli.CLI;
import net.thesimpleteam.simplebot.cli.CLIBuilder;
import net.thesimpleteam.simplebot.cli.commands.HelpCommand;
import net.thesimpleteam.simplebot.cli.commands.SendMessageCommand;
import net.thesimpleteam.simplebot.cli.commands.TestCommand;
import net.thesimpleteam.simplebot.commands.annotations.RequireConfig;
import net.thesimpleteam.simplebot.config.Infos;
import net.thesimpleteam.simplebot.config.ServerConfig;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.gson.RecordTypeAdapterFactory;
import net.thesimpleteam.simplebot.listeners.Listener;
import net.thesimpleteam.simplebot.utils.Eval;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import okhttp3.OkHttpClient;
import org.fusesource.jansi.internal.CLibrary;
import org.python.core.PrePy;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SimpleBot {

    private static JDA jda;
    private static CommandClient client;
    private static Infos infos;
    private static ServerConfig serverConfig;
    private static final EventWaiter waiter = new EventWaiter();
    public static final Gson gson = new GsonBuilder().registerTypeAdapterFactory(new RecordTypeAdapterFactory()).setPrettyPrinting().create();
    public static final Logger LOGGER = Logger.getLogger(SimpleBot.class.getName());
    public static final OkHttpClient httpClient = new OkHttpClient.Builder().build();
    public static final Eval eval = new Eval();
    private static Map<String, JsonObject> localizations;
    private static String[] langs;
    private static ScheduledExecutorService executorService;

    private record Bot(List<Command> commands, String ownerID, String serverInvite) {
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Is in dev mode :" + isInDevMode());
        executorService = Executors.newScheduledThreadPool(3);
        try {
            String arg = "";
            try {
                arg = args[0];
            } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {}
            setupLogs();
            infos = readConfig(arg);
            LOGGER.info("Bot config loaded");
            serverConfig = setupServerConfig();
            LOGGER.info("Servers config loaded");
            jda = JDABuilder.createDefault(infos.token()).setActivity(Activity.playing(infos.prefix() + "help")).enableIntents(EnumSet.allOf(GatewayIntent.class))
                    .enableCache(CacheFlag.ONLINE_STATUS).build();
            setupLocalizations();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        } catch (LoginException e) {
            LOGGER.log(Level.SEVERE, "The token is invalid !");
            return;
        }
        Bot b = new Bot(new ArrayList<>(), "285829396009451522", "https://discord.gg/jw3kn4gNZW");
        CommandClientBuilder clientBuilder = new CommandClientBuilder()
                .setOwnerId(b.ownerID)
                .setCoOwnerIds("363811352688721930")
                .setPrefix(infos.prefix())
                .useHelpBuilder(true)
                .setServerInvite(b.serverInvite)
                .setPrefixFunction(SimpleBot::getPrefix)
                .setStatus(OnlineStatus.ONLINE);
        setupCommands(clientBuilder, b);
        client = clientBuilder.setHelpConsumer(e -> getHelpConsumer(e, b)).build();
        jda.addEventListener(new Listener(), waiter, client);
        jda.awaitReady();
        executorService.scheduleAtFixedRate(() ->
                        jda.getPresence().setActivity(Activity.playing(getInfos().activities()[new Random().nextInt(getInfos().activities().length)])),
                0, getInfos().timeBetweenStatusChange(), TimeUnit.SECONDS);
        executorService.scheduleAtFixedRate(() -> {
            try {
                Listener.saveConfigs();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage());
            }
        }, getInfos().autoSaveDelay(), getInfos().autoSaveDelay(), TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(() -> serverConfig.tempBan().entrySet().stream()
                .map(tempBanMap -> new AbstractMap.SimpleImmutableEntry<>(tempBanMap.getKey(), LocalDateTime.parse(tempBanMap.getValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))))
                .filter(tempBanMap -> tempBanMap.getValue().isEqual(LocalDateTime.now()) || tempBanMap.getValue().isBefore(LocalDateTime.now())).forEach(tempBanMapNeedsUnban -> {
                    serverConfig.tempBan().remove(tempBanMapNeedsUnban.getKey());
                    try {
                        Listener.saveConfigs();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    jda.getGuildById(tempBanMapNeedsUnban.getKey().split("-")[1]).retrieveBanList().queue(banList -> {
                        if (banList.stream().anyMatch(ban -> ban.getUser().getId().equals(tempBanMapNeedsUnban.getKey().split("-")[0]))) {
                            jda.getGuildById(tempBanMapNeedsUnban.getKey().split("-")[1]).unban(tempBanMapNeedsUnban.getKey().split("-")[0]).queue(unused ->
                                            jda.getTextChannelById(serverConfig.channelMemberJoin().get(tempBanMapNeedsUnban.getKey().split("-")[1]))
                                                    .sendMessage(jda.getUserById(tempBanMapNeedsUnban.getKey().split("-")[0]).getName()).queue());
                        }
                    });
                }), 0, 1, TimeUnit.SECONDS);
        executorService.schedule(() -> new Server(jda, gson).server(), 3, TimeUnit.SECONDS);
        if (CLibrary.isatty(CLibrary.STDIN_FILENO) == 1 || isInDevMode()) {
            executorService.schedule(() -> {
                try {
                    CLI cli = new CLIBuilder(jda).addCommand(new TestCommand(), new SendMessageCommand(), new HelpCommand()).build();
                    cli.commandsListener();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }, 5, TimeUnit.SECONDS);
        } else {
            LOGGER.warning("Console is not interactive. CLI Commands will be disabled!");
        }
    }

    private static void getHelpConsumer(CommandEvent event, Bot bot) {
        StringBuilder helpBuilder = new StringBuilder(String.format(MessageHelper.translateMessage(event, "help.commands"), event.getSelfUser().getName()));
        for(CommandCategories category : CommandCategories.values()) {
            if(!bot.commands.stream().filter(command -> category.category == command.getCategory()).toList().isEmpty()) {
                helpBuilder.append("\n\n__").append(MessageHelper.translateMessage(event, category.category.getName())).append("__ :\n");
                for (Command command : bot.commands.stream().filter(command -> category.category == command.getCategory()).toList()) {
                    if (!command.isHidden() && (!command.isOwnerCommand() || event.isOwner())) {
                        if (MessageHelper.translateMessage(event, command.getHelp()).contains("²")) {
                            for (int index = 0; index < MessageHelper.translateMessage(event, command.getHelp()).split("²").length - 1; index++) {
                                helpBuilder.append("\n`").append(getPrefix(event.getGuild())).append(command.getName()).append(" ").append(MessageHelper.translateMessage(event, command.getArguments()).split("²")[index]).append("`").append(" -> *").append(MessageHelper.translateMessage(event, command.getHelp()).split("²")[index]).append("*");
                            }
                        } else {
                            helpBuilder.append("\n`").append(getPrefix(event.getGuild())).append(command.getName()).append(" ").append(command.getArguments() != null ?
                                    command.getArguments().startsWith("arguments.") ? MessageHelper.translateMessage(event, command.getArguments()) : command.getArguments() : "").append("`")
                                    .append(" \u27A1 *").append(MessageHelper.translateMessage(event, command.getHelp())).append("*");
                        }
                    }
                }
            }
        }
        if (event.getJDA().getUserById(bot.ownerID) != null) {
            helpBuilder.append("\n\n").append(MessageHelper.translateMessage(event, "help.contact")).append(" **").append(MessageHelper.getTag(event.getJDA().getUserById(bot.ownerID))).append("**");
            if (event.getClient().getServerInvite() != null)
                helpBuilder.append(' ').append(MessageHelper.translateMessage(event, "help.discord")).append(' ').append(bot.serverInvite);
        }
        event.replyInDm(helpBuilder.toString(), unused -> {
        }, t -> event.reply(MessageHelper.translateMessage(event, "help.DMBlocked")));
    }

    private static void setupLocalizations() throws IOException {
        Map<String, JsonObject> objects = new HashMap<>();
        List<String> langS = new ArrayList<>();
        File f = new File("lang");
        if (!f.exists()) {
            LOGGER.severe("PLEASE DOWNLOAD THE LANG FOLDER FROM OUR REPOSITORY!");
            System.exit(-1);
        }
        File[] langsFile = f.listFiles();
        if (langsFile == null) return;
        for (File lang : langsFile) {
            langS.add(lang.getName().replaceAll(".json", ""));
            objects.put(lang.getName().replaceAll(".json", ""), gson.fromJson(Files.newBufferedReader(lang.toPath(), StandardCharsets.UTF_8), JsonObject.class));
        }
        localizations = objects;
        langs = langS.toArray(new String[0]);
    }

    /**
     * <p>Instantiates all classes from the package {@link net.thesimpleteam.simplebot.commands}</p>
     */
    private static void setupCommands(CommandClientBuilder clientBuilder, Bot b) {
        Reflections reflections = new Reflections("net.thesimpleteam.simplebot.commands");
        Set<Class<? extends Command>> commands = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> command : commands) {
            if (hasConfig(command)) {
                try {
                    addCommand(command, b, clientBuilder);
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.info(command.getName() + " need " + command.getAnnotation(RequireConfig.class).value() + " key in config.json");
            }
        }
    }

    private static boolean hasConfig(Class<? extends Command> clazz) {
        if (clazz.getAnnotation(RequireConfig.class) == null) return true;
        RequireConfig c = clazz.getAnnotation(RequireConfig.class);
        try {
            return Infos.class.getMethod(c.value()).invoke(infos) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static void addCommand(Class<? extends Command> clazz, Bot b, CommandClientBuilder builder)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Command instance = clazz.getDeclaredConstructor().newInstance();
        builder.addCommands(instance);
        b.commands.add(instance);
    }

    private static void setupLogs() throws IOException {
        File logFolder = new File("logs");
        if (!Files.exists(logFolder.toPath())) {
            logFolder.mkdir();
        }
        FileHandler fh = new FileHandler("logs/log-" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replace(":", "-") + ".log");
        SimpleFormatter sf = new SimpleFormatter();
        fh.setEncoding("UTF-8");
        fh.setFormatter(sf);
        LOGGER.addHandler(fh);
    }

    private static Infos readConfig(String arg) throws IOException {
        File configDir = new File(Paths.get("config").toUri());
        File config = new File(configDir, "config.json");
        File configTemplate = new File(configDir, "config-template.json");
        if (!configDir.exists()) configDir.mkdir();
        if (!config.exists()) {
            config.createNewFile();
            Map<String, Object> map = new LinkedHashMap<>();
            if (arg.equalsIgnoreCase("--nosetup") || !PrePy.isInteractive()) {
                map.put("token", "YOUR-TOKEN-HERE");
                map.put("prefix", "sb!");
                map.put("timeBetweenStatusChange", 15);
                map.put("autoSaveDelay", 15);
                map.put("activities", new String[]{"sb!help"});
                map.put("botGithubToken", "YOUR-GITHUB-TOKEN");
            } else {
                Console console = System.console();
                System.out.println("I see that it's the first time that you install the bot.");
                System.out.println("The configuration will begin. What will be the bot's prefix?");
                map.put("prefix", console.readLine().isEmpty() ? "!" : console.readLine());
                System.out.println("How long will it take between each status change ?");
                map.put("timeBetweenStatusChange", console.readLine().isEmpty() ? 15 : console.readLine());
                System.out.println("What will be the delay between each automatic save ?");
                map.put("autoSaveDelay", console.readLine().isEmpty() ? 15 : console.readLine());
                System.out.println("What are gonna be the bot's activities?\n(Separate them with ;). For example:\nexample;ban everyone;check my mentions");
                map.put("activities", console.readLine().isEmpty() ? new String[]{"sb!help"} : console.readLine().split(";"));
                System.out.println("What is your bot token?");
                map.put("botGithubToken", "YOUR-GITHUB-TOKEN");
                System.out.println("The configuration is finished. Your bot will be ready to start !");
            }
            Writer writer = Files.newBufferedWriter(config.toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            gson.toJson(map, writer);
            writer.close();
            Files.copy(config.toPath(), configTemplate.toPath(), StandardCopyOption.REPLACE_EXISTING);
            configTemplate.setWritable(false);
        }
        Reader reader = Files.newBufferedReader(config.toPath(), StandardCharsets.UTF_8);
        Infos infos = gson.fromJson(reader, Infos.class);
        reader.close();
        try(RandomAccessFile access = new RandomAccessFile(config, "rw"); FileChannel channel = access.getChannel()) {
            channel.tryLock();
        } catch (OverlappingFileLockException e) {
            LOGGER.log(Level.SEVERE, "The configuration file is already locked by another process. Please close the other instance of the bot and try again.");
        }
        return infos;
    }

    public static ServerConfig setupServerConfig() throws IOException {
        File serverConfigFile = new File("config/server-config.json");
        if (!serverConfigFile.exists()) {
            serverConfigFile.createNewFile();
            Map<String, Object> map = new LinkedHashMap<>();
            Map<String, String> defaultGuildJoinRole = new HashMap<>();
            Map<String, String> defaultChannelMemberJoin = new HashMap<>();
            Map<String, String> defaultChannelMemberLeave = new HashMap<>();
            Map<String, String> defaultMutedRole = new HashMap<>();
            Map<String, String[]> defaultProhibitWords = new HashMap<>();
            Map<String, String> languages = new HashMap<>();
            defaultGuildJoinRole.put("657966618353074206", "660083059089080321");
            defaultChannelMemberJoin.put("657966618353074206", "848965362971574282");
            defaultChannelMemberLeave.put("657966618353074206", "660110008507432970");
            defaultMutedRole.put("657966618353074206", "660114547646005280");
            defaultProhibitWords.put("657966618353074206", new String[]{"prout", "pute"});
            languages.put("846048803554852904", "en");
            map.put("guildJoinRole", defaultGuildJoinRole);
            map.put("channelMemberJoin", defaultChannelMemberJoin);
            map.put("channelMemberLeave", defaultChannelMemberLeave);
            map.put("mutedRole", defaultMutedRole);
            map.put("prohibitWords", defaultProhibitWords);
            map.put("language", languages);
            map.put("prefix", new HashMap<String, String>());
            map.put("tempBan", new HashMap<String, String>());
            Writer writer = Files.newBufferedWriter(serverConfigFile.toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            gson.toJson(map, writer);
            writer.close();
        }
        Reader reader = Files.newBufferedReader(serverConfigFile.toPath(), StandardCharsets.UTF_8);
        ServerConfig config = gson.fromJson(reader, ServerConfig.class);
        reader.close();
        return config;
    }

    public static boolean doesFileExist(String path) {
        if(isInDevMode()) {
            return new File(path).exists();
        }
        try(AutoCloseable o = SimpleBot.class.getResourceAsStream(path)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInDevMode() {
        String property = System.getProperty("simplebot.dev");
        return property != null && property.equals("true");
    }

    public static InputStream getFile(String path) {
        if (!doesFileExist(path)) {
            return null;
        }
        if(isInDevMode()) {
            try {
                return Files.newInputStream(Path.of(path));
            } catch (IOException ignored) {
                return null;
            }
        }
        return SimpleBot.class.getResourceAsStream(path);
    }

    public static String getPrefix(MessageReceivedEvent event) {
        return getPrefix(event.isFromGuild() ? event.getGuild() : null);
    }

    public static String getPrefix(Guild guild) {
        return guild != null && serverConfig.prefix().containsKey(guild.getId()) ? serverConfig.prefix().get(guild.getId()) : infos.prefix();
    }

    public static JDA getJda() {
        return jda;
    }

    public static CommandClient getClient() {
        return client;
    }

    public static Infos getInfos() {
        return infos;
    }

    public static ServerConfig getServerConfig() { return serverConfig; }

    public static EventWaiter getEventWaiter() {
        return waiter;
    }

    public static Map<String, JsonObject> getLocalizations() {
        return localizations;
    }

    public static String[] getLangs() {
        return langs;
    }

    public static boolean isTTY() {
        return PrePy.isInteractive();
    }

    public static ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}
