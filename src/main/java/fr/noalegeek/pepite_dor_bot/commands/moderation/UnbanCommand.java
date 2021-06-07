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
        this.aliases = new String[]{"ub","unb","uban","pa","pardon"};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.help = "Débanni seulement les personnes déjà banni.";
        this.guildOnly = true;
        this.example = "285829396009451522";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getArgs().split(" ").length == 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }
        User target = event.getMessage().getMentionedUsers().get(0);
        if (target == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous devez spécifié un membre existant.");
            return;
        }
        event.getGuild().unban(target).queue(unused -> event.replySuccess("L'utilisateur " + target.getName() + " à bien été débanni."));
    }
}
