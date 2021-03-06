package net.thesimpleteam.simplebot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        this.name = "unmute";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.unmute";
        this.example = "363811352688721930 wrong person";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.unmute";
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 2) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null).build()).build());
            return;
        }
        if (MuteCommand.isMutedRoleHere(event)) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.unmute.mutedRoleDontExist", null, null, null)).build());
            return;
        }
        SimpleBot.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            if (!member.getRoles().contains(event.getGuild().getRoleById(SimpleBot.getServerConfig().mutedRole().get(event.getGuild().getId()))) || !MuteCommand.isMutedRoleHere(event)) {
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.unmute.notMuted", null, null, null, user.getName())).build());
                return;
            }
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.unmute", null, null, null, user.getName(), args.length == 2 ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : MessageHelper.translateMessage(event, "text.commands.reason") + " " + event.getArgs().substring(args[0].length() + args[1].length() + 2))).build());
            event.getGuild().removeRoleFromMember(member, event.getGuild().getRoleById(SimpleBot.getServerConfig().mutedRole().get(event.getGuild().getId()))).queue();
        }, memberNull -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.memberNull", null, null, null).build()).build())), userNull -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.userNull", null, null, null).build()).build()));
    }
}
