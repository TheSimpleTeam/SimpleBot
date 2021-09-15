package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

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
        if (args.length != 1 && args.length != 2) {
            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.mute", event));
            return;
        }
        if(!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user ->
            event.getGuild().retrieveMember(user).queue(member -> {
                if (!event.getMember().canInteract(member)) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userCantInteractTarget", event));
                    return;
                }
                if (!event.getSelfMember().canInteract(member)) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.botCantInteractTarget", event));
                    return;
                }
                String reason;
                if(args[1] == null || args[1].isEmpty()) reason = MessageHelper.translateMessage("text.reasonNull", event);
                else reason = MessageHelper.translateMessage("text.reason", event) + args[1];
                isMutedRoleHere(event);
                mute(event, member, reason, event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event)));
            }, memberNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))), userNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason, Role mutedRole) {
        if (targetMember.getRoles().contains(mutedRole)) { // Unmute
            event.getGuild().removeRoleFromMember(targetMember, mutedRole).queue();
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.unmute", event), targetMember.getEffectiveName(), reason));
        } else { // Mute
            event.getGuild().addRoleToMember(targetMember, mutedRole).queue();
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.mute", event), targetMember.getEffectiveName(), reason));
        }
    }

    public static boolean isMutedRoleHere(CommandEvent event){
        if(Main.getServerConfig().mutedRole.get(event.getGuild().getId()) == null || event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event)) == null){
            event.getGuild().createRole()
                    .setName("Muted Role")
                    .setColor(0x010101)
                    .queue(mutedRole -> {
                        Main.getServerConfig().mutedRole.put(event.getGuild().getId(), mutedRole.getId());
                        for (GuildChannel guildChannel : event.getGuild().getChannels()) {
                            guildChannel.putPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_WRITE).queue();
                        }
                    });
            event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.mute", event));
            return false;
        }
        return true;
    }
}
