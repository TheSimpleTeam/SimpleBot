package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class UnmuteCommand extends Command {
    public UnmuteCommand() {
        this.name = "unmute";
        this.aliases = new String[]{"um","umute","unm"};
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "<identifiant/mention du membre> <raison>";
        this.example = "363811352688721930";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.category = CommandCategories.STAFF.category;
        this.help = "Démute un membre seulement si la personne est déjà mute.";
    }
    @Override
    protected void execute(CommandEvent event) {
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
        Member targetMember = event.getGuild().getMemberById(args[0].replace("<@", "").replace(">", ""));
        if(targetMember.getRoles().contains(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())))) {
            event.getGuild().unban(target).queue();
            if(args[1] == null){
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "L'utilisateur " + target.getName() + " à bien été démuter.");
            } else {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "L'utilisateur " + target.getName() + " à bien été démuter pour la raison " + args[1] + ".");
            }
            return;
        }
        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous ne pouvez pas démuter " + target.getName() + " car il n'est pas mute");
    }
}
