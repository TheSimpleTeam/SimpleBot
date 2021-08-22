package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.aliases = new String[]{"b", "ba"};
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.ban";
        this.example = "363811352688721930";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.ban";
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        if(!event.getMember().hasPermission(Permission.BAN_MEMBERS)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event.getGuild().getId()), Permission.BAN_MEMBERS.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.BAN_MEMBERS)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event.getGuild().getId()), Permission.BAN_MEMBERS.getName()));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 1 && args.length != 2 && args.length != 3) {
            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.ban", event.getGuild().getId()));
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            if (!event.getMember().canInteract(member)) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userCantInteractTarget", event.getGuild().getId()));
                return;
            }
            if (!event.getSelfMember().canInteract(member)) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.botCantInteractTarget", event.getGuild().getId()));
                return;
            }
            if (event.getGuild().retrieveBanList().complete().contains(user)) { // Unban
                event.getGuild().unban(user).queue(unused -> event.replySuccess(String.format(MessageHelper.translateMessage("success.unban", event.getGuild().getId()), user.getName())));
            } else { // Ban
                if (args[1] == null || args[1].isEmpty()) args[1] = "7";
                String reason;
                if (args[2] == null || args[2].isEmpty()) reason = MessageHelper.translateMessage("text.commands.reasonNull", event.getGuild().getId());
                else reason = MessageHelper.translateMessage("text.commands.reason", event.getGuild().getId()) + args[2];
                try {
                    int banTime = Integer.parseInt(args[1]);
                    if (banTime > 7) {
                        banTime = 7;
                        event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.ban", event.getGuild().getId()));
                    }
                    event.getGuild().ban(user, banTime).queue();
                    event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.ban", event.getGuild().getId()), user.getName(), reason));
                } catch (NumberFormatException ex) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.ban.notAnNumber", event.getGuild().getId()));
                }
            }
        }, memberNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event.getGuild().getId()))), userNull -> event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event.getGuild().getId())));
    }
}


