package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
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
        this.help = "Supprime le nombre de messages spécifié.";
        this.category = CommandCategories.STAFF.category;
        this.example = "69";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" \\s+");
        if(event.getArgs().isEmpty()) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this)+"Le nombre de messages à spécifier doit se situer entre 1 et 100.");
            return;
        }
        final AuditableRestAction<Void> deleteMessage = event.getMessage().delete();
        boolean isMessageOld = false;
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
        try {
            event.getTextChannel().getHistory().retrievePast(clearMessages).queue(messages -> deleteMessage.queue(unused -> event.getTextChannel().purgeMessages(messages)),
                    unused -> Main.LOGGER.info("Le message n'a pas pu être supprimé."));
        } catch (IllegalArgumentException ex){
            isMessageOld = true;
        }
        event.replySuccess(clearMessages + " messages ont bien été supprimés.", messageSuccess -> messageSuccess.delete().queueAfter(10, TimeUnit.SECONDS));
        if(isMessageOld){
            event.getChannel().sendMessage(":warning: Les messages datant de plus 2 semaines n'ont pas pu être supprimé !").queueAfter(10, TimeUnit.SECONDS);
        }
    }
}