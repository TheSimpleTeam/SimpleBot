package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.TimeUnit;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        this.name = "purge";
        this.aliases = new String[]{"p", "cl", "clear", "pu", "pur", "purg", "cle", "clea"};
        this.guildOnly = true;
        this.arguments = "<nombre de messages>";
        this.help = "help.purge";
        this.category = CommandCategories.STAFF.category;
        this.cooldown = 5;
        this.example = "6";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        if(!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.purge", event));
            return;
        }
        int clearMessages = 1;
        try {
            if(Integer.parseInt(args[0]) < 0) {
                event.replyWarning(MessageHelper.translateMessage("warning.purge.numberTooSmall", event));
            } else if (Integer.parseInt(args[0]) > 100) {
                event.replyWarning(MessageHelper.translateMessage("warning.purge.numberTooLarge", event));
                clearMessages = 100;
            } else {
                clearMessages = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException ignore) {
            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.purge", event));
            return;
        }
        try {
            event.getTextChannel().getHistory().retrievePast(clearMessages).queue(messages -> event.getMessage().delete().queue(unused -> event.getTextChannel().purgeMessages(messages)));
        } catch (IllegalArgumentException ex){
            event.getChannel().sendMessage(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.purge", event)).queueAfter(10, TimeUnit.SECONDS);
        }
        event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.purge", event), clearMessages), messageSuccess -> messageSuccess.delete().queueAfter(10, TimeUnit.SECONDS));
    }
}