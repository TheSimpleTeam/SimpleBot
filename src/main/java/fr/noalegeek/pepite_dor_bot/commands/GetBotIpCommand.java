package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.helpers.RequestHelper;

import java.io.IOException;

public class GetBotIpCommand extends Command {

    public GetBotIpCommand() {
        this.name = "getbotipcommand";
        this.aliases = new String[]{"gbic", "gbi"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String ipify = "https://api.ipify.org/";
        try {
            event.replyInDm("L'ip du bot est : " + RequestHelper.getResponseAsString(RequestHelper.sendRequest(ipify)));
        } catch (IOException ex) {
            MessageHelper.sendError(ex, event);
        }
    }
}
