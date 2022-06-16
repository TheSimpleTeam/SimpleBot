package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.thesimpleteam.simplebot.utils.RequestHelper;

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
            event.replyInDm(new MessageBuilder(MessageHelper.getEmbed(event, "success.getBotIp", null, null, null, RequestHelper.getResponseAsString(RequestHelper.sendRequest("https://api.ipify.org/")))).build());
        } catch (IOException ex) {
            MessageHelper.sendError(ex, event, this);
        }
    }
}
