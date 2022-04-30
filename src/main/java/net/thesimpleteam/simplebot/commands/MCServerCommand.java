package net.thesimpleteam.simplebot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.thesimpleteam.simplebot.utils.RequestHelper;
import net.dv8tion.jda.api.MessageBuilder;

import java.io.IOException;

public class MCServerCommand extends Command {

    public MCServerCommand() {
        this.name = "minecraftserver";
        this.cooldown = 5;
        this.help = "help.mcServer";
        this.example = "hypixel.net";
        this.aliases = new String[]{"mcs"};
        this.arguments = "arguments.mcServer";
        this.category = CommandCategories.INFO.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args[0] != null && args[0].isEmpty()) {
            MessageHelper.syntaxError(event, this, "information.mcServer");
            return;
        }
        try {
            if (!SimpleBot.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest(new StringBuilder().append("https://api.mcsrvstat.us/2/").append(args[0]).toString())), JsonObject.class).get("online").getAsBoolean()) {
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.mcServer.offlineServer", null, null, null).build()).build());
                return;
            }
            JsonObject serverInformation = SimpleBot.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class);
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.mcServer.success", null, null, null)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.ipAdress"), serverInformation.get("ip").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.port"), serverInformation.get("port").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.version"), serverInformation.get("version").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.connectedPlayers"), String.valueOf(serverInformation.get("players").getAsJsonObject().get("online").getAsInt()), false).build()).build());
        } catch (IOException e) {
            MessageHelper.sendError(e, event, this);
        }
    }
}
