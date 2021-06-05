package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.util.concurrent.TimeUnit;

public class PurgeCommand extends Command {

    public PurgeCommand() {
        this.name = "purge";
        this.aliases = new String[]{"p", "cl", "clear"};
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.arguments = "<number of messages>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        final AuditableRestAction<Void> deleteMessage = event.getMessage().delete();
        if(event.getArgs().isEmpty()) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }
        int clearMessages = 1;
        try {
            if(Integer.parseInt(args[0]) < 0) {
                event.replyWarning("La valeur minimum est de 1.");
            }else if(Integer.parseInt(args[0]) > 100) {
                event.replyWarning("La valeur maximale est de 100.");
                clearMessages = 100;
            } else {
                clearMessages = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException ignore) {
            event.replyError("Le premier argument doit être un nombre de **1** à **100**.");
            return;
        }
        int clearMessages1 = clearMessages;
        event.getTextChannel().getHistory().retrievePast(clearMessages + 1).queue(messages -> deleteMessage.queue(unused -> {
            event.getTextChannel().purgeMessages(messages);
            event.replySuccess(clearMessages1 + " ont bien été supprimés", message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
        }));
    }
}
