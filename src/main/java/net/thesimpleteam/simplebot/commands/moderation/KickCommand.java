package net.thesimpleteam.simplebot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

public class KickCommand extends Command {
    public KickCommand() {
        this.name = "kick";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.kick";
        this.example = "363811352688721930 spam";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.kick";
        this.aliases = new String[]{"exclude"};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 1) {
            MessageHelper.syntaxError(event, this, "information.kick");
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null).build()).build());
            return;
        }
        SimpleBot.getJda().retrieveUserById(args[1].replaceAll("\\D+","")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage(event, "success.kick"), user.getName(), args.length == 2 ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : MessageHelper.translateMessage(event, "text.commands.reason") + " " + event.getArgs().substring(args[0].length() + args[1].length() + 2)));
            event.getGuild().kick(member).queue();
        }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.memberNull"))),
                userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.userNull")));
    }
}
