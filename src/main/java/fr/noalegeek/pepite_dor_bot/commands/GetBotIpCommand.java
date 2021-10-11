package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RequestHelper;

import java.io.IOException;

public class GetBotIpCommand extends Command {

    public GetBotIpCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "Envoie en message privé l'ip du bot.";
        this.cooldown = 5;
        this.name = "getbotip";
        this.aliases = new String[]{"gbi","getbi","getboti","getbip","gbotip","gboti","gbip"};
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
