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
        this.arguments = "<nombre de messages>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" \\s+");
        final AuditableRestAction<Void> deleteMessage = event.getMessage().delete();
        boolean isMessageOld = false;
        if(event.getArgs().isEmpty()) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this)+"Le nombre de messages à spécifier doit se situer entre 1 et 100.");
            return;
        }
        int clearMessages = 1;
        try {
            if(Integer.parseInt(args[0]) < 0) {
                event.replyWarning("La valeur minimum est de 1.");
            }else if(Integer.parseInt(args[0]) > 100) {
                event.replyWarning("La valeur maximale est de 100.\n");
                clearMessages = 100;
            } else {
                clearMessages = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException ignore) {
            event.replyError("Le nombre à spécifier doit être un nombre de **1** à **100**.");
            return;
        }
        int finalClearMessages = clearMessages;
        try {
            for(int i = 1;i < finalClearMessages;i+=0){
                event.getTextChannel().getHistory().retrievePast(i).queue(messages -> deleteMessage.queue(unused -> {
                    event.getTextChannel().purgeMessages(messages);

                }));
            }
            event.replySuccess(finalClearMessages + " messages ont bien été supprimés.", messageSucces -> messageSucces.delete().queueAfter(5000, TimeUnit.SECONDS));
            if(isMessageOld){
                event.getChannel().sendMessage(":warning: Les messages datant de plus 2 semaines n'ont pas pu être supprimé !").queue();
            }
        } catch (IllegalArgumentException ex){
            isMessageOld = true;
        }
    }
}
