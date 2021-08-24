package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.Arrays;

public class MuteCommand extends Command {
    public MuteCommand() {
        this.category = CommandCategories.STAFF.category;
        this.aliases = new String[]{"m", "mu", "mut"};
        this.name = "mute";
        this.arguments = "arguments.mute";
        this.help = "help.mute";
        this.cooldown = 5;
        this.example = "285829396009451522 spam";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        event.reply(Arrays.toString(args));
        if (args.length != 2 && args.length != 3) {
            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.mute", event.getGuild().getId()));
            return;
        }
        if(!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event.getGuild().getId()), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event.getGuild().getId()), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user ->
            event.getGuild().retrieveMember(user).queue(member -> {
                if (!event.getMember().canInteract(member)) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userCantInteractTarget", event.getGuild().getId()));
                    return;
                }
                if (!event.getSelfMember().canInteract(member)) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.botCantInteractTarget", event.getGuild().getId()));
                    return;
                }
                String reason;
                if(args[1] == null || args[1].isEmpty()) reason = MessageHelper.translateMessage("text.reasonNull", event.getGuild().getId());
                else reason = MessageHelper.translateMessage("text.reason", event.getGuild().getId()) + args[1];
                if (Main.getServerConfig().mutedRole.get(event.getGuild().getId()) == null || event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())) == null) {
                    event.getGuild().createRole()
                            .setName("Muted Role")
                            .setColor(0x010101)
                            .queue(mutedRole -> {
                                Main.getServerConfig().mutedRole.put(event.getGuild().getId(), mutedRole.getId());
                                for (GuildChannel guildChannel : event.getGuild().getChannels()) {
                                    guildChannel.putPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_WRITE).queue();
                                }
                                mute(event, member, reason, mutedRole);
                            });
                    event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.mute", event.getGuild().getId()));
                } else {
                    mute(event, member, reason, event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())));
                }
            }, memberNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event.getGuild().getId()))), userNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event.getGuild().getId())));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason, Role mutedRole) {
        if (targetMember.getRoles().contains(mutedRole)) { // Unmute
            event.getGuild().removeRoleFromMember(targetMember, mutedRole).queue();
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.unmute", event.getGuild().getId()), targetMember.getEffectiveName(), reason));
        } else { // Mute
            event.getGuild().addRoleToMember(targetMember, mutedRole).queue();
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.mute", event.getGuild().getId()), targetMember.getEffectiveName(), reason));
        }
    }
}
