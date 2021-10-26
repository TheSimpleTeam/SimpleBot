package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RequestHelper;

import java.io.IOException;
import java.util.ArrayList;

public class GetBotIpCommand extends Command {

    public GetBotIpCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "help.getBotIp";
        this.cooldown = 5;
        this.name = "getbotip";
        this.aliases = new String[]{"getboti", "getbip", "getbi", "getboip", "getboi", "gbotip", "gboti", "gbip", "gbi", "gboip", "gboi", "gebotip", "geboti", "gebip", "gebi", "geboip", "geboi"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            event.replyInDm(String.format(MessageHelper.translateMessage("success.getBotIp", event), RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.ipify.org/"))));
        } catch (IOException ex) {
            MessageHelper.sendError(ex, event, this);
        }
    }
}
