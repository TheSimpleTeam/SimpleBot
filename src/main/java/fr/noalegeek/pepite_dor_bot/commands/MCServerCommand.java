package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
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
        this.aliases = new String[]{"minecraftserv","minecrafts","mcserver","mcserv","mcs","mserver","mserv","ms"};
        this.arguments = "arguments.mcServer";
        this.category = CommandCategories.INFO.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 1) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        try {
            if (!Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class).get("online").getAsBoolean()) {
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.mcServer.offlineServer", null, null, null, (Object[]) null).build()).build());
                return;
            }
            //We get the informations like https://github.com/Minemobs/McStatusJava/blob/master/src/main/java/fr/minemobs/test/Main.java
            EmbedBuilder successEmbed = new EmbedBuilder()
                    .setTimestamp(Instant.now())
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.ipAdress"), Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class).get("ip").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.port"), Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class).get("port").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.version"), Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class).get("version").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.connectedPlayers"), String.valueOf(Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class).get("players").getAsJsonObject().get("online").getAsInt()), false)
                    .setColor(Color.GREEN);
            event.reply(new MessageBuilder(successEmbed.build()).build());
        } catch (IOException exception) {
            MessageHelper.sendError(exception, event, this);
        }
    }
}
