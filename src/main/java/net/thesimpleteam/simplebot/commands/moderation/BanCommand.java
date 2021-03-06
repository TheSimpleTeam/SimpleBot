package net.thesimpleteam.simplebot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
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
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage(event, "information.ban"));
            return;
        }
        if (args[0].replaceAll("\\D+", "").isEmpty()) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null).build()).build());
            return;
        }
        SimpleBot.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            if (MessageHelper.cantInteract(event.getMember(), event.getSelfMember(), member, event)) return;
            if (args[1] == null || args[1].isEmpty()) args[1] = "7";
            try {
                int days = Integer.parseInt(args[1]);
                if (days > 7) {
                    days = 7;
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "warning.ban", null, null, null)).build());
                }
                event.getGuild().ban(user, days).queue();
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.ban", null, null, null, user.getName(), args.length == 2 ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : MessageHelper.translateMessage(event, "text.commands.reason") + " " + event.getArgs().substring(args[0].length() + args[1].length() + 2))).build());
            } catch (NumberFormatException ex) {
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.ban.notAnNumber", null, null, null)).build());
            }
        }, memberNull -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.userNull", null, null, null)).build())), userNull -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.userNull", null, null, null)).build()));
    }
}


