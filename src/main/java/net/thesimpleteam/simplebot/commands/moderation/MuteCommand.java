package net.thesimpleteam.simplebot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class MuteCommand extends Command {
    public MuteCommand() {
        this.category = CommandCategories.STAFF.category;
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
        if (args.length < 1) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage(event, "syntax.mute"));
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null).build()).build());
            return;
        }
        SimpleBot.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user ->
            event.getGuild().retrieveMember(user).queue(member -> {
                if(MessageHelper.cantInteract(event.getMember(), event.getSelfMember(), member, event)) return;
                isMutedRoleHere(event);
                mute(event, member, args[1] == null ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : MessageHelper.translateMessage(event, "text.commands.reason") + " " + event.getArgs().substring(args[0].length() + 1), event.getGuild().getRoleById(SimpleBot.getServerConfig().mutedRole().get(event.getGuild().getId())));
            }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.memberNull"))),
                userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.userNull")));
    }

    public static void mute(CommandEvent event, Member targetMember, String reason, Role mutedRole) {
        if (targetMember.getRoles().contains(mutedRole)) { // Unmute
            event.getGuild().removeRoleFromMember(targetMember, mutedRole).queue();
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage(event, "success.unmute"),
                    targetMember.getEffectiveName(), reason));
        } else { // Mute
            event.getGuild().addRoleToMember(targetMember, mutedRole).queue();
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage(event, "success.mute"),
                    targetMember.getEffectiveName(), reason));
        }
    }

    public static boolean isMutedRoleHere(CommandEvent event){
        if(SimpleBot.getServerConfig().mutedRole().get(event.getGuild().getId()) == null || event.getGuild().getRoleById(SimpleBot.getServerConfig().mutedRole().get(event.getGuild().getId())) == null){
            event.getGuild().createRole()
                    .setName("Muted Role")
                    .setColor(0x010101)
                    .queue(mutedRole -> {
                        SimpleBot.getServerConfig().mutedRole().put(event.getGuild().getId(), mutedRole.getId());
                        for (GuildChannel guildChannel : event.getGuild().getChannels()) {
                            guildChannel.putPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_WRITE).queue();
                        }
                    });
            event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "warning.mute"));
            return false;
        }
        return true;
    }
}
