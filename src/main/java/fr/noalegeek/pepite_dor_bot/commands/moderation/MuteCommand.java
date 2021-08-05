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
        this.example = "@NoaLeGeek spam";
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        User author = event.getAuthor();
        if (author.isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (args.length > 2) {
            event.replyError(MessageHelper.syntaxError(author, this) + "Mettre une raison n'est pas obligatoire.");
            return;
        }
        Guild guild = event.getGuild();
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user ->
                guild.retrieveMember(user).queue(member -> {
            if (!event.getSelfMember().canInteract(member)) {
                event.replyError(MessageHelper.formattedMention(author) + "Le bot n'a pas les permissions de faire cela.");
                return;
            }
            if (!event.getMember().canInteract(member)) {
                event.replyError(MessageHelper.formattedMention(author) + "Vous n'avez pas la permission de mute ce membre.");
                return;
            }
            String mutedRoleId = Main.getServerConfig().mutedRole.get(event.getGuild().getId());
            if (mutedRoleId == null || event.getGuild().getRoleById(mutedRoleId) == null) {
                RestAction.allOf(
                                guild.createRole().setName("Muted Role").setColor(0x010101).map(mutedRole -> Main.getServerConfig().mutedRole.put(guild.getId(), mutedRole.getId())))
                        .queue(unused -> {
                            List<RestAction<?>> restActionList = new ArrayList<>();
                            for (GuildChannel guildChannel : guild.getChannels()) {
                                restActionList.add(guildChannel.putPermissionOverride(guild.getRoleById(mutedRoleId)).setDeny(Permission.MESSAGE_WRITE));
                            }
                            RestAction.allOf(restActionList).queue(unused1 -> {
                                        if (args.length == 1) {
                                            mute(event, member, null);
                                        } else {
                                            mute(event, member, args[1]);
                                        }
                                    }
                            );
                        });
                event.replyWarning(MessageHelper.formattedMention(author) + ":warning: Le rôle configuré par défaut n'est pas présent donc j'ai créé un nouveau rôle nommé \"Muted Role\".");
            } else {
                if (args.length == 1) {
                    mute(event, member, null);
                } else {
                    mute(event, member, args[1]);
                }
            }
        }, memberNull -> event.replyError(MessageHelper.formattedMention(author) + "Vous devez spécifié une personne présente sur le serveur.")), userNull -> event.replyError(MessageHelper.formattedMention(author) + "Vous devez spécifié une personne existante."));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason) {
        Guild guild = event.getGuild();
        User author = event.getAuthor();
        String mutedRoleId = Main.getServerConfig().mutedRole.get(event.getGuild().getId());
        if (targetMember.getRoles().contains(guild.getRoleById(mutedRoleId))) { // Unmute the target
            targetMember.getRoles().remove(guild.getRoleById(mutedRoleId));
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(author) + targetMember.getEffectiveName() + " a bien été démuter pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(author) + targetMember.getEffectiveName() + " a bien été démuter.");
        } else { // Mute the target
            guild.addRoleToMember(targetMember, guild.getRoleById(mutedRoleId)).queue();
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(author) + targetMember.getEffectiveName() + " a bien été muter pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(author) + targetMember.getEffectiveName() + " a bien été muter.");
        }
    }
}
