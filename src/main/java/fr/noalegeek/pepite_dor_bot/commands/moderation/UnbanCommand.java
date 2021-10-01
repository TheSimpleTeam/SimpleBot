package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.name = "unban";
        this.arguments = "arguments.unban";
        this.aliases = new String[]{"ub","unb","uban","pa","pardon"};
        this.category = CommandCategories.STAFF.category;
        this.help = "help.unban";
        this.guildOnly = true;
        this.example = "285829396009451522";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event),
                    Permission.BAN_MEMBERS.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.BAN_MEMBERS)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event),
                    Permission.BAN_MEMBERS.getName()));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 1 && args.length != 2) {
            event.replyError(MessageHelper.syntaxError(event, this));
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> {
            if(event.getGuild().retrieveBanList().complete().stream().anyMatch(ban -> ban.getUser() == user)) {
                String reason;
                if(args[1] == null || args[1].isEmpty()) reason = MessageHelper.translateMessage("text.commands.reasonNull", event);
                else reason = MessageHelper.translateMessage("text.commands.reason", event) + args[1];
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.unban", event), user.getName(), reason));
                event.getGuild().unban(user).queue();
                return;
            }
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.unban", event), user.getName()));
        }, userNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
