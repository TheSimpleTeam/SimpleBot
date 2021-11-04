package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

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
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 1 && args.length != 2) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.mute", event));
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user ->
            event.getGuild().retrieveMember(user).queue(member -> {
                if (!event.getMember().canInteract(member)) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userCantInteractTarget", event));
                    return;
                }
                if (!event.getSelfMember().canInteract(member)) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.botCantInteractTarget", event));
                    return;
                }
                isMutedRoleHere(event);
                mute(event, member, MessageHelper.setReason(args[1], event), event.getGuild().getRoleById(Main.getServerConfig().mutedRole().get(event.getGuild().getId())));
            }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))),
                userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason, Role mutedRole) {
        if (targetMember.getRoles().contains(mutedRole)) { // Unmute
            event.getGuild().removeRoleFromMember(targetMember, mutedRole).queue();
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.unmute", event),
                    targetMember.getEffectiveName(), reason));
        } else { // Mute
            event.getGuild().addRoleToMember(targetMember, mutedRole).queue();
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.mute", event),
                    targetMember.getEffectiveName(), reason));
        }
    }

    public static boolean isMutedRoleHere(CommandEvent event){
        if(Main.getServerConfig().mutedRole().get(event.getGuild().getId()) == null || event.getGuild().getRoleById(Main.getServerConfig().mutedRole().get(event.getGuild().getId())) == null){
            event.getGuild().createRole()
                    .setName("Muted Role")
                    .setColor(0x010101)
                    .queue(mutedRole -> {
                        Main.getServerConfig().mutedRole().put(event.getGuild().getId(), mutedRole.getId());
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
