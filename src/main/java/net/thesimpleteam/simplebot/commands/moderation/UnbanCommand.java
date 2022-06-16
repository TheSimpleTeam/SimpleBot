package net.thesimpleteam.simplebot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.name = "unban";
        this.arguments = "arguments.unban";
        this.aliases = new String[]{"pardon"};
        this.category = CommandCategories.STAFF.category;
        this.help = "help.unban";
        this.guildOnly = true;
        this.example = "285829396009451522 wrong person";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 1) {
            MessageHelper.syntaxError(event, this, "information.unban");
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null).build()).build());
            return;
        }
        SimpleBot.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveBanList().queue(banList -> {
            if(banList.stream().anyMatch(banUser -> user.getId().equals(banUser.getUser().getId()))) {
                event.getGuild().unban(user).queue(unused -> {
                    SimpleBot.getServerConfig().tempBan().remove(new StringBuilder().append(user.getId()).append("-").append(event.getGuild().getId()).toString());
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.unban", null, null, null, user.getName(), args.length == 1 ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : new StringBuilder().append(MessageHelper.translateMessage(event, "text.commands.reason")).append(" ").append(event.getArgs().substring(args[0].length() + 1)).toString()).build()).build());
                });
                return;
            }
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.unban", null, null, null, user.getName()).build()).build());
        }), userNull -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.userNull", null, null, null).build()).build()));
    }
}
