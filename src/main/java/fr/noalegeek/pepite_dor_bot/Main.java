package fr.noalegeek.pepite_dor_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.noalegeek.pepite_dor_bot.commands.PerfectNumber;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static JDA jda;
    private static CommandClient client;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        String token = null;
        try {
            token = readConfig();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getCause().getMessage());
        }
        EventWaiter waiter = new EventWaiter();
        try {
            jda = JDABuilder.createDefault(token).enableIntents(EnumSet.allOf(GatewayIntent.class)).build();
        } catch (LoginException e) {
            LOGGER.log(Level.SEVERE,"Le token est invalide");
        }
        Random randomActivity = new Random();
        client = new CommandClientBuilder()
                .setOwnerId("285829396009451522")
                .setPrefix("!")
                .addCommands(new PerfectNumber())
                .setActivity(Activity.playing("se créer de lui-même..."))
                .setStatus(OnlineStatus.ONLINE)
                .build();
        jda.addEventListener(new Events(), waiter, client);
    }

    private static String readConfig() throws IOException {
        File config = new File(Paths.get("config.json").toUri());
        if(!config.exists()) {
            config.createNewFile();
            Map<String, String> map = new HashMap<>();
            map.put("token", "YOUR-TOKEN-HERE");
            Writer writer = new FileWriter(config);
            gson.toJson(map, writer);
            writer.close();
        }
        Reader reader = Files.newBufferedReader(Paths.get("config.json"));
        Map<String, String> map = gson.fromJson(reader, Map.class);
        reader.close();
        return map.get("token");
    }
}
