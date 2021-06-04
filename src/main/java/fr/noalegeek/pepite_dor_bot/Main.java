package fr.noalegeek.pepite_dor_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.examples.command.ShutdownCommand;
import fr.noalegeek.pepite_dor_bot.commands.BotCommand;
import fr.noalegeek.pepite_dor_bot.config.Infos;
import fr.noalegeek.pepite_dor_bot.config.ServerConfig;
import fr.noalegeek.pepite_dor_bot.listener.EventsEmbeds;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    private static JDA jda;
    private static CommandClient client;
    private static Infos infos;
    public static ServerConfig serverConfig;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            String arg = "";
            try {
                arg = args[0];
            }catch (NullPointerException | ArrayIndexOutOfBoundsException ignore) {}
            infos = readConfig(arg);
            LOGGER.info("Bot config loaded");
            serverConfig = setupServerConfig();
            LOGGER.info("Servers config loaded");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getCause().getMessage());
        }

        EventWaiter waiter = new EventWaiter();

        try {
            jda = JDABuilder.createDefault(infos.token).enableIntents(EnumSet.allOf(GatewayIntent.class)).build();
        } catch (LoginException e) {
            LOGGER.log(Level.SEVERE,"Le token est invalide");
        }
        Random randomActivity = new Random();
        CommandClientBuilder clientBuilder = new CommandClientBuilder()
                .setOwnerId("285829396009451522")
                .setCoOwnerIds("363811352688721930")
                .addCommands(new ShutdownCommand())
                .setPrefix(infos.prefix)
                .useHelpBuilder(false)
                .setActivity(Activity.playing(infos.activities[randomActivity.nextInt(infos.activities.length)]))
                .setStatus(OnlineStatus.ONLINE);
        setupCommands(clientBuilder);
        client = clientBuilder.build();
        jda.addEventListener(new EventsEmbeds(), waiter, client);
        try {
            setupLogs();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }

    /**
     * <p>Instantiates all classes from the package {@link fr.noalegeek.pepite_dor_bot.commands}</p>
     */
    private static void setupCommands(CommandClientBuilder clientBuilder) {
        Reflections reflections = new Reflections("fr.noalegeek.pepite_dor_bot.commands");
        Set<Class<? extends BotCommand>> commands = reflections.getSubTypesOf(BotCommand.class);
        for (Class<? extends BotCommand> command : commands) {
            try {
                clientBuilder.addCommands(command.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void setupLogs() throws IOException {
        File logFolder = new File("logs/");
        if (!Files.exists(logFolder.toPath())) {
            logFolder.mkdir();
        }
        FileHandler fh = new FileHandler("logs/log-" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .replaceAll(":", "-") + ".log");
        SimpleFormatter sf = new SimpleFormatter();
        fh.setEncoding("UTF-8");
        fh.setFormatter(sf);
        LOGGER.addHandler(fh);
    }

    private static Infos readConfig(String arg) throws IOException {
        File config = new File(Paths.get("config/config.json").toUri());
        File configTemplate = new File(Paths.get("config/config-template.json").toUri());
        if (!config.exists()) {
            config.createNewFile();
            Map<String, Object> map = new LinkedHashMap<>();
            if(arg.equalsIgnoreCase("--nosetup")) {
                map.put("token", "YOUR-TOKEN-HERE");
                map.put("prefix", "!");
                map.put("defaultRoleID", "YOUR-ROLE-ID");
                map.put("timeBetweenStatusChange", 15);
                map.put("autoSaveDelay", 15);
                map.put("activities", new String[]{map.get("prefix") + "help", "Se créer de lui-meme..."});
            } else {
                Console console = System.console();
                if (console == null) {
                    System.out.println("No console: non-interactive mode!");
                    System.exit(0);
                }
                System.out.println("I see that it's the first time that you install the bot.");
                System.out.println("The configuration will begin");
                System.out.println("What is your bot token ?");
                map.put("token", console.readLine());
                System.out.println("What will be the bot's prefix ?");
                map.put("prefix", console.readLine().isEmpty() ? "!" : console.readLine());
                //flemme
                map.put("defaultRoleID", "846715377760731156");
                map.put("timeBetweenStatusChange", 15);
                map.put("autoSaveDelay", 15);
                System.out.println("What are gonna be the bot's activities? \n [Separate them with ;]. For example: \n" +
                        "!help;ban everyone;check my mentions");
                map.put("activities", console.readLine().isEmpty() ? new String[]{map.get("prefix") + "help"} : console.readLine().split(";"));
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
        return infos;
    }

    private static ServerConfig setupServerConfig() throws IOException {
        File serverConfigFile = new File("config/server-config.json");
        if(!serverConfigFile.exists()) {
            serverConfigFile.createNewFile();
            Map<String, Object> map = new LinkedHashMap<>();
            Map<String, String> defaultGuild = new HashMap<>();
            defaultGuild.put("846048803554852904", "846715377760731156");
            map.put("guildJoinRole", defaultGuild);
            Writer writer = Files.newBufferedWriter(serverConfigFile.toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            gson.toJson(map, writer);
            writer.close();
        }
        Reader reader = Files.newBufferedReader(serverConfigFile.toPath(), StandardCharsets.UTF_8);
        ServerConfig config = gson.fromJson(reader, ServerConfig.class);
        reader.close();
        return config;
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
}
