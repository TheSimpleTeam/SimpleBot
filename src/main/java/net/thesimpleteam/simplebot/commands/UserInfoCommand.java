package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.User;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.thesimpleteam.simplebot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.util.Optional;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.name = "userinfo";
        this.aliases = new String[]{"useri", "ui", "uinfo"};
        this.arguments = "arguments.userInfo";
        this.guildOnly = true;
        this.cooldown = 5;
        this.help = "help.userInfo";
        this.example = "363811352688721930";
        this.category = CommandCategories.INFO.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length > 1) {
            MessageHelper.syntaxError(event, this, "information.userInfo");
            return;
        }
        if (event.getArgs().isEmpty()) {
            EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.userInfo.success", Color.BLUE, null, event.getAuthor().getEffectiveAvatarUrl())
                    .setTitle(UnicodeCharacters.INFORMATION_SOURCE_EMOJI + " " + String.format(MessageHelper.translateMessage(event, "success.userInfo.success"), MessageHelper.getTag(event.getAuthor())))
                    .addField(MessageHelper.translateMessage(event, "success.userInfo.userID"), event.getMember().getUser().getId(), false)
                    .addField(MessageHelper.translateMessage(event, "success.userInfo.joinDate"), MessageHelper.formatShortDate(event.getMember().getTimeJoined().toLocalDateTime()), false)
                    .addField(MessageHelper.translateMessage(event, "success.userInfo.creationDate"), MessageHelper.formatShortDate(event.getMember().getTimeCreated().toLocalDateTime()), false)
                    .addField(MessageHelper.translateMessage(event, "success.userInfo.activity"), event.getMember().getActivities().isEmpty() ? MessageHelper.translateMessage(event, "text.commands.nothing") : event.getMember().getActivities().toString(), false);
            if (event.getMember().getNickname() != null)
                embedBuilder.addField(MessageHelper.translateMessage(event, "success.userInfo.nickname"), event.getMember().getNickname(), false);
            event.reply(new MessageBuilder(embedBuilder.build()).build());
            return;
        }
        Optional<User> userOp = event.getMessage().getMentionedUsers().stream().findFirst();
        SimpleBot.getJda().retrieveUserById(userOp.map(ISnowflake::getId)
                .orElseGet(() -> event.getArgs().split("\\s+")[0].replaceAll("\\D+", ""))).queue(user ->
                        event.getGuild().retrieveMember(user).queue(member -> {
                            EmbedBuilder successEmbed = MessageHelper.getEmbed(event, "success.userInfo.success", Color.BLUE, null, user.getEffectiveAvatarUrl())
                                    .setTitle(UnicodeCharacters.INFORMATION_SOURCE_EMOJI + " " + String.format(MessageHelper.translateMessage(event, "success.userInfo.success"), MessageHelper.getTag(user)))
                                    .addField(MessageHelper.translateMessage(event, "success.userInfo.userID"), member.getUser().getId(), false)
                                    .addField(MessageHelper.translateMessage(event, "success.userInfo.joinDate"), MessageHelper.formatShortDate(member.getTimeJoined().toLocalDateTime()), false)
                                    .addField(MessageHelper.translateMessage(event, "success.userInfo.creationDate"), MessageHelper.formatShortDate(member.getTimeCreated().toLocalDateTime()), false)
                                    .addField(MessageHelper.translateMessage(event, "success.userInfo.activity"), member.getActivities().isEmpty() ? MessageHelper.translateMessage(event, "text.commands.nothing") : member.getActivities().toString(), false);
                            if (member.getNickname() != null)
                                successEmbed.addField(MessageHelper.translateMessage(event, "success.userInfo.nickname"), member.getNickname(), false);
                            event.reply(new MessageBuilder(successEmbed.build()).build());
                        }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.memberNull"))),
                userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage(event, "error.commands.userNull")));
    }
}