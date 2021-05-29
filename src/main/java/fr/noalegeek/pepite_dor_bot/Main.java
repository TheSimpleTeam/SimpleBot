package fr.noalegeek.pepite_dor_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static JDA jda;
    private static CommandClient client;
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        Infos infos = null;

        try {
            infos = readConfig();
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
                .setPrefix(infos.prefix)
                .useHelpBuilder(false)
                .setActivity(Activity.playing(infos.activities[randomActivity.nextInt(infos.activities.length)]))
                .setStatus(OnlineStatus.ONLINE);
        setupCommands(clientBuilder);
        client = clientBuilder.build();
        jda.addEventListener(new Events(), waiter, client);
    }

    /**
     * <p>Instantiates all classes from the package {@link fr.noalegeek.pepite_dor_bot.commands}</p>
     */
    private static void setupCommands(CommandClientBuilder clientBuilder) {
        Reflections reflections = new Reflections("fr.noalegeek.pepite_dor_bot.commands");
        Set<Class<? extends Command>> commands = reflections.getSubTypesOf(Command.class);
        for (Class<? extends Command> command : commands) {
            try {
                clientBuilder.addCommands(command.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static Infos readConfig() throws IOException {
        File config = new File(Paths.get("config.json").toUri());
        File configTemplate = new File(Paths.get("config-template.json").toUri());
        if (!config.exists()) {
            config.createNewFile();
            Map<String, Object> map = new HashMap<>();
            map.put("token", "YOUR-TOKEN-HERE");
            map.put("prefix", "!");
            map.put("activities", new String[]{map.get("prefix") + "help", "Se créer de lui-meme..."});
            Writer writer = Files.newBufferedWriter(config.toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE);
            gson.toJson(map, writer);
            writer.close();
            Files.copy(config.toPath(), configTemplate.toPath(), StandardCopyOption.REPLACE_EXISTING);
            configTemplate.setWritable(false);
        }
        Reader reader = Files.newBufferedReader(Paths.get("config.json"), StandardCharsets.UTF_8);
        Infos infos = gson.fromJson(reader, Infos.class);
        reader.close();
        return infos;
    }

    public static JDA getJda() {
        return jda;
    }

    public static CommandClient getClient() {
        return client;
    }
}
