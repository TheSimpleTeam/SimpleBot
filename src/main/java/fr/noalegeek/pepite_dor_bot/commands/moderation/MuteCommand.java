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
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (args.length > 2) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this) + "Mettre une raison n'est pas obligatoire.");
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
            if (Main.getServerConfig().mutedRole.containsValue(event.getGuild().getId()) && event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())) != null) {
                if (args.length == 1) {
                    mute(event, member, null);
                } else {
                    mute(event, member, args[1]);
                }
            } else {
                RestAction.allOf(
                                event.getGuild().createRole().setName("Muted Role").setColor(0x010101).map(mutedRole -> Main.getServerConfig().mutedRole.put(event.getGuild().getId(), mutedRole.getId())))
                        .queue(unused -> {
                            List<RestAction<?>> restActionList = new ArrayList<>();
                            for (GuildChannel guildChannel : event.getGuild().getChannels()) {
                                restActionList.add(guildChannel.putPermissionOverride(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId()))).setDeny(Permission.MESSAGE_WRITE));
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
                try {
                    Listener.saveConfigs();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + ":warning: Le rôle configuré par défaut n'est pas présent donc j'ai créé un nouveau rôle nommé \"Muted Role\".");
            }
        }, memberNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié une personne présente sur le serveur.")), userNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié une personne existante."));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason) {
        if (targetMember.getRoles().contains(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())))) { // Unmute the target
            targetMember.getRoles().remove(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())));
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été démuter pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été démuter.");
        } else { // Mute the target
            event.getGuild().addRoleToMember(targetMember, event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId()))).queue();
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muter pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muter.");
        }
    }
}
