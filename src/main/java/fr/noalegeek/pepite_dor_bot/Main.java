package fr.noalegeek.pepite_dor_bot;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import fr.noalegeek.pepite_dor_bot.commands.PingCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;
import java.util.Random;

public class Main {
    private static JDA jda;
    private static CommandClient client;
    public static void main(String[] args) throws LoginException {
        EventWaiter waiter = new EventWaiter();
        jda = JDABuilder.createDefault("ODQ2MDM1MTU3OTQ0NjMxMzI3.YKpppA.e3yAFfLV308s0ZvBumwTNfIZZHM").enableIntents(EnumSet.allOf(GatewayIntent.class)).build();
        Random randomActivity = new Random();
        client = new CommandClientBuilder()
                .setOwnerId("285829396009451522")
                .setPrefix("!")
                .addCommands(new PingCommand())
                .setActivity(Activity.playing("se créer de lui-même..."))
                .setStatus(OnlineStatus.ONLINE)
                .build();
        jda.addEventListener(new Events(),waiter,client);
    }
}
