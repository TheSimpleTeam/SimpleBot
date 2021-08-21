package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.listener.Listener;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.RoleAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MuteCommand extends Command {
    public MuteCommand() {
        this.category = CommandCategories.STAFF.category;
        this.aliases = new String[]{"m", "mu", "mut"};
        this.name = "mute";
        this.arguments = "<identifiant/mention du membre> [raison]";
        this.help = "Mute définitivement un membre avec une raison ou non. Unmute si la personne est déjà mute.";
        this.cooldown = 5;
        this.example = "285829396009451522 spam";
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (args.length > 2) {
            event.replyError(MessageHelper.syntaxError(event, this) + "Mettre une raison n'est pas obligatoire.");
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user ->
            event.getGuild().retrieveMember(user).queue(member -> {
                if (!event.getSelfMember().canInteract(member)) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le bot n'a pas les permissions de faire cela.");
                    return;
                }
                if (!event.getMember().canInteract(member)) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous n'avez pas la permission de mute ce membre.");
                    return;
                }
                String mutedRoleId = Main.getServerConfig().mutedRole.get(event.getGuild().getId());
                if(args[1] == null || args[1].isEmpty()) args[1] = "Aucune raison";
                if (mutedRoleId == null || event.getGuild().getRoleById(mutedRoleId) == null) {
                    event.getGuild().createRole()
                            .setName("Muted Role")
                            .setColor(0x010101)
                            .queue(mutedRole -> {
                                Main.getServerConfig().mutedRole.put(event.getGuild().getId(), mutedRole.getId());
                                for (GuildChannel guildChannel : event.getGuild().getChannels()) {
                                    guildChannel.putPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_WRITE).queue();
                                }
                                mute(event, member, args[1], mutedRole);
                            });
                    event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + ":warning: Le rôle configuré par défaut n'est pas présent donc j'ai créé un nouveau rôle nommé \"Muted Role\".");
                } else {
                    mute(event, member, args[1], event.getGuild().getRoleById(mutedRoleId));
                }
            }, memberNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié une personne présente sur le serveur.")), userNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié une personne existante."));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason, Role mutedRole) {
        if (targetMember.getRoles().contains(mutedRole)) { // Unmute the target
            event.getGuild().removeRoleFromMember(targetMember, mutedRole).queue();
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été démuter pour la raison " + reason + ".");
        } else { // Mute the target
            event.getGuild().addRoleToMember(targetMember, mutedRole).queue();
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muter pour la raison " + reason + ".");
        }
    }
}
