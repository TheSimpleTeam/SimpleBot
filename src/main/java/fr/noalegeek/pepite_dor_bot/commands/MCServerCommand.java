package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RequestHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;

public class MCServerCommand extends Command {

    public MCServerCommand() {
        this.name = "minecraftserver";
        this.cooldown = 5;
        this.help = "help.mcServer";
        this.example = "hypixel.net";
        this.aliases = new String[]{"minecrafts","minecraftse","minecraftser","minecraftserv","minecraftserve","minecs","minecse","minecser","minecserv","minecserve","minecserver","mcrafts","mcraftse","mcraftser","mcraftserv","mcraftserve","mcraftserver","mcs","mcse","mcser","mcserv","mcserve","mcserver"};
        this.arguments = "arguments.mcServer";
        this.category = CommandCategories.FUN.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        Main.LOGGER.info(args[0]);
        try {
            JsonObject serverInformations = Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class);
            if (!serverInformations.get("online").getAsBoolean()) {
                EmbedBuilder errorServerOfflineEmbed = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTimestamp(Instant.now())
                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                        .setTitle("\u274C " + MessageHelper.translateMessage("error.mcServer.offlineServer", event));
                event.reply(new MessageBuilder(errorServerOfflineEmbed.build()).build());
                return;
            }
            //We get the informations like https://github.com/Minemobs/McStatusJava/blob/master/src/main/java/fr/minemobs/test/Main.java
            EmbedBuilder successEmbed = new EmbedBuilder()
                    .setTimestamp(Instant.now())
                    .addField(MessageHelper.translateMessage("success.mcServer.ipAdress", event), serverInformations.get("ip").getAsString(), false)
                    .addField(MessageHelper.translateMessage("success.mcServer.port", event), serverInformations.get("port").getAsString(), false)
                    .addField(MessageHelper.translateMessage("success.mcServer.version", event), serverInformations.get("version").getAsString(), false)
                    .addField(MessageHelper.translateMessage("success.mcServer.connectedPlayers", event), String.valueOf(serverInformations.get("players").getAsJsonObject().get("online").getAsInt()), false)
                    .setColor(Color.GREEN);
            event.reply(new MessageBuilder(successEmbed.build()).build());
        } catch (IOException exception) {
            MessageHelper.sendError(exception, event);
        }
    }
}
