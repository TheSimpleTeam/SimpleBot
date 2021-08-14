package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.name = "unban";
        this.arguments = "<identifiant/mention du membre> [raison]";
        this.aliases = new String[]{"ub","unb","uban","pa","pardon"};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.category = CommandCategories.STAFF.category;
        this.help = "Débanni un membre seulement la personne est déjà banni du serveur.";
        this.guildOnly = true;
        this.example = "285829396009451522";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (event.getArgs().split(" ").length == 1) {
            event.replyError(MessageHelper.syntaxError(event, this));
            return;
        }
        User target = event.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", "")).getUser();
        if (target == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié un membre existant.");
            return;
        }
        if(event.getGuild().retrieveBanList().complete().contains(target)) {
            event.getGuild().unban(target).queue();
            if(args[1] == null){
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "L'utilisateur " + target.getName() + " à bien été débanni.");
            } else {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "L'utilisateur " + target.getName() + " à bien été débanni pour la raison " + args[1] + ".");
            }
            return;
        }
        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous ne pouvez pas débannir " + target.getName() + " car il n'est pas banni du serveur.");
    }
}
