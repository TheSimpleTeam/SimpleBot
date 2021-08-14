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
        this.aliases = new String[]{"p", "cl", "clear", "pu"};
        this.guildOnly = true;
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.arguments = "<nombre de messages>";
        this.help = "Supprime les messages datant de moins de 2 semaines selon un nombre donné en-dessous de 101.";
        this.category = CommandCategories.STAFF.category;
        this.cooldown = 5;
        this.example = "6";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" \\s+");
        if(event.getArgs().isEmpty()) {
            event.replyError(MessageHelper.syntaxError(event, this) + "Le nombre de messages à spécifier doit se situer entre 1 et 100.");
            return;
        }
        int clearMessages = 1;
        try {
            if(Integer.parseInt(args[0]) < 0) {
                event.replyWarning("La valeur minimum est de 1.\n:warning: La valeur a donc été défini à 1 !");
            } else if (Integer.parseInt(args[0]) > 100) {
                event.replyWarning("La valeur maximale est de 100.\n:warning: La valeur a donc été défini à 100 !");
                clearMessages = 100;
            } else {
                clearMessages = Integer.parseInt(args[0]);
            }
        } catch (NumberFormatException ignore) {
            event.replyError("Le nombre à spécifier doit être un nombre de **1** à **100**.");
            return;
        }
        try {
            event.getTextChannel().getHistory().retrievePast(clearMessages).queue(messages -> event.getMessage().delete().queue(unused -> event.getTextChannel().purgeMessages(messages)));
        } catch (IllegalArgumentException ex){
            event.getChannel().sendMessage(MessageHelper.formattedMention(event.getAuthor()) + "Il y a des messages datant de plus de 2 semaines donc je ne peux pas les supprimer !").queueAfter(10, TimeUnit.SECONDS);
        }
        event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + clearMessages + " messages ont bien été supprimés.", messageSuccess -> messageSuccess.delete().queueAfter(10, TimeUnit.SECONDS));
    }
}