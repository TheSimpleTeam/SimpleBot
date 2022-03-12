package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.SimpleBot;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RequestHelper;
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
        if(args.length != 1) {
            MessageHelper.syntaxError(event, this, "information.mcServer");
            return;
        }
        try {
            if (!SimpleBot.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class).get("online").getAsBoolean()) {
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.mcServer.offlineServer", null, null, null, (Object[]) null).build()).build());
                return;
            }
            JsonObject serverInformation = SimpleBot.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.mcsrvstat.us/2/" + args[0])), JsonObject.class);
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.mcServer.success", null, null, null, (Object[]) null)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.ipAdress"), serverInformation.get("ip").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.port"), serverInformation.get("port").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.version"), serverInformation.get("version").getAsString(), false)
                    .addField(MessageHelper.translateMessage(event, "success.mcServer.connectedPlayers"), String.valueOf(serverInformation.get("players").getAsJsonObject().get("online").getAsInt()), false).build()).build());
        } catch (IOException exception) {
            MessageHelper.sendError(exception, event, this);
        }
    }
}
