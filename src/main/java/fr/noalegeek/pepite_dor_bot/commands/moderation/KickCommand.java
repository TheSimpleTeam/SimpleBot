package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class KickCommand extends Command {
    public KickCommand() {
        this.name = "kick";
        this.aliases = new String[]{"k"};
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "<identifiant/mention du membre> <raison>";
        this.example = "363811352688721930";
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.category = CommandCategories.STAFF.category;
        this.help = "Kick un membre du serveur, la personne pourra rejoindre après.";
    }
    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (event.getArgs().split(" ").length == 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }
        User target = event.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", "")).getUser();
        if (target == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié un membre existant.");
            return;
        }
        Member targetMember = event.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", ""));
        if(event.getGuild().retrieveBanList().complete().contains(target)) {
            event.getGuild().kick(targetMember).queue();
            if (args[1] == null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "L'utilisateur " + target.getName() + " à bien été kick du serveur.");
            } else {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "L'utilisateur " + target.getName() + " à bien été kick du serveur pour la raison " + args[1] + ".");
            }
        }
        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous ne pouvez pas débannir " + target.getName() + " car il n'est pas banni du serveur.");
    }
}
