package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.SimpleBot;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.ban";
        this.example = "363811352688721930 7 spam";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.ban";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 2) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage(event, "informations.ban"));
            return;
        }
        if (args[0].replaceAll("\\D+", "").isEmpty()) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null, (Object[]) null).build()).build());
            return;
        }
        SimpleBot.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> {
            event.getGuild().retrieveMember(user).queue(member -> {
                if (MessageHelper.cantInteract(event.getMember(), event.getSelfMember(), member, event)) return;
                if (args[1] == null || args[1].isEmpty()) args[1] = "7";
                try {
                    int days = Integer.parseInt(args[1]);
                    if (days > 7) {
                        days = 7;
                        event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "warning.ban"));
                    }
                    event.getGuild().ban(user, days).queue();
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage(event, "success.ban"), user.getName(), args.length == 2 ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : MessageHelper.translateMessage(event, "text.commands.reason") + " " + event.getArgs().substring(args[0].length() + args[1].length() + 2)));
                } catch (NumberFormatException ex) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.ban.notAnNumber"));
                }
            }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.memberNull")));
        }, userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.userNull")));
    }
}


