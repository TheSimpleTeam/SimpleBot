package net.thesimpleteam.simplebot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.TimeUnit;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        this.name = "purge";
        this.aliases = new String[]{"clear"};
        this.guildOnly = true;
        this.arguments = "arguments.purge";
        this.help = "help.purge";
        this.category = CommandCategories.STAFF.category;
        this.cooldown = 5;
        this.example = "42";
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 1) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage(event, "syntax.purge"));
            return;
        }
        int clearMessages = 1;
        try {
            if(Integer.parseInt(args[0]) < 0) event.replyWarning(MessageHelper.translateMessage(event, "warning.purge.numberTooSmall"));
            else if (Integer.parseInt(args[0]) > 100) {
                event.replyWarning(MessageHelper.translateMessage(event, "warning.purge.numberTooLarge"));
                clearMessages = 100;
            } else clearMessages = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignore) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage(event, "syntax.purge"));
            return;
        }
        event.getTextChannel().getHistory().retrievePast(clearMessages).queue(messages -> event.getTextChannel().purgeMessages(messages));
        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage(event, "success.purge"), clearMessages), messageSuccess -> messageSuccess.delete().queueAfter(10, TimeUnit.SECONDS));
    }
}