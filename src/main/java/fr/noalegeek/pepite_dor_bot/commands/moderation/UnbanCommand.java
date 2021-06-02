package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.name = "unban";
        this.arguments = "<mention>";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getArgs().length() == 1) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Syntaxe de la commande !ban : ``!ban <user ou userID> <temps en jours> [raison]``.");
            return;
        }
        User target = event.getMessage().getMentionedUsers().get(0);
        if (target == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous devez spécifié un membre existant.");
            return;
        }
        event.getGuild().unban(target).queue(unused ->
                event.replySuccess("L'utilisateur " + target.getName() + " à bien été unban."));

    }
}
