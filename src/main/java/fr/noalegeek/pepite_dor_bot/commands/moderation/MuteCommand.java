package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.Nullable;

public class MuteCommand extends Command {
    public MuteCommand() {
        this.category = CommandCategories.STAFF.category;
        this.aliases = new String[]{"m", "mu", "mut"};
        this.name = "mute";
        this.arguments = "<identifiant/mention de l'utilisateur> [raison]";
        this.help = "Mute définitivement un utilisateur avec une raison ou non. Unmute si la personne est déjà mute.";
        this.cooldown = 5;
        this.example = "@NoaLeGeek spam";
    }
    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (!event.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous n'avez pas la permission de faire cette commande.");
            return;
        }
        if (args.length > 2) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this) + "Mettre une raison n'est pas obligatoire.");
            return;
        }
        try {
            User targetUser = event.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", "")).getUser();
            if (targetUser == null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié une personne existante.");
                return;
            }
            if (event.getGuild().getMember(targetUser) != null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "La personne doit être présente sur ce serveur.");
                return;
            }
            Member targetMember = event.getGuild().getMemberById(args[0].replace("<@", "").replace(">", ""));
            if (!event.getSelfMember().canInteract(targetMember)) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le bot n'a pas les permissions de faire cela.");
                return;
            }
            if (!event.getMember().canInteract(targetMember)) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous n'avez pas la permission de mute ce membre.");
                return;
            }
            if (!event.getGuild().getRoles().contains(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())))) { // If mutedRole is not here
                event.getGuild().createRole().setName("Muted Role").setColor(0x010101).queue(mutedRole -> Main.getServerConfig().mutedRole.put(event.getGuild().getId(), mutedRole.getId()));
                event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + ":warning: Le rôle configuré par défaut n'est pas présent donc j'ai créé un nouveau rôle nommé \"Muted Role\".");
            }
            mute(event, targetMember, args[1]);
        } catch (IndexOutOfBoundsException e) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this) + "Mettre une raison n'est pas obligatoire.");
        }
    }
    public static void mute(CommandEvent event, Member targetMember, @Nullable String reason) {
        if (targetMember.getRoles().contains(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())))) { // Unmute the target
            targetMember.getRoles().remove(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())));
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été unmuté pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été unmuté.");
        } else { // Mute the target
            targetMember.getRoles().add(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())));
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muté pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muté.");
        }
    }
}
