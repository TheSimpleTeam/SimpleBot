package fr.noalegeek.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.simplebot.enums.CommandCategories;
import fr.noalegeek.simplebot.utils.MessageHelper;
import fr.noalegeek.simplebot.utils.RequestHelper;

import java.io.IOException;

public class GetBotIpCommand extends Command {

    public GetBotIpCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "help.getBotIp";
        this.cooldown = 5;
        this.name = "getbotip";
        this.aliases = new String[]{"gbi"};
        this.ownerCommand = true;
        this.hidden = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            event.replyInDm(String.format(MessageHelper.translateMessage(event, "success.getBotIp"), RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.ipify.org/"))));
        } catch (IOException ex) {
            MessageHelper.sendError(ex, event, this);
        }
    }
}
