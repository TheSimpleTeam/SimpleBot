package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.Instant;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.name = "userinfo";
        this.aliases = new String[]{"useri", "ui","uinfo"};
        this.arguments = "arguments.userInfo";
        this.guildOnly = true;
        this.cooldown = 5;
        this.help = "help.userInfo";
        this.example = "363811352688721930";
        this.category = CommandCategories.INFO.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty() || event.getArgs().isBlank()){
            EmbedBuilder successEmbed = new EmbedBuilder()
                    .setTitle("\u2139 " + String.format(MessageHelper.translateMessage("success.botInfo.success", event), MessageHelper.getTag(event.getAuthor())))
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setColor(Color.BLUE)
                    .addField(MessageHelper.translateMessage("error.commands.userID", event), event.getMember().getUser().getId(), false)
                    .addField(MessageHelper.translateMessage("error.commands.joinDate", event), MessageHelper.formatShortDate(event.getMember().getTimeJoined()), false)
                    .addField(MessageHelper.translateMessage("error.commands.creationDate", event), MessageHelper.formatShortDate(event.getMember().getTimeCreated()), false)
                    .addField(MessageHelper.translateMessage("error.commands.activity", event), event.getMember().getActivities().isEmpty() ? MessageHelper.translateMessage("text.commands.nothing", event) : event.getMember().getActivities().toString(), false);
            if (event.getMember().getNickname() != null)
                successEmbed.addField(MessageHelper.translateMessage("success.userInfo.nickname", event), event.getMember().getNickname(), false);
            event.reply(new MessageBuilder(successEmbed.build()).build());
        } else {
            Main.getJda().retrieveUserById(event.getArgs().split("\\s+")[0].replaceAll("\\D+", "")).queue(user ->
                            event.getGuild().retrieveMember(user).queue(member -> {
                                EmbedBuilder successEmbed = new EmbedBuilder()
                                        .setTitle("\u2139 " + String.format(MessageHelper.translateMessage("success.botInfo.success", event), MessageHelper.getTag(user)))
                                        .setFooter(MessageHelper.getTag(user), user.getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setColor(Color.BLUE)
                                        .addField(MessageHelper.translateMessage("error.commands.userID", event), member.getUser().getId(), false)
                                        .addField(MessageHelper.translateMessage("error.commands.joinDate", event), MessageHelper.formatShortDate(member.getTimeJoined()), false)
                                        .addField(MessageHelper.translateMessage("error.commands.creationDate", event), MessageHelper.formatShortDate(member.getTimeCreated()), false)
                                        .addField(MessageHelper.translateMessage("error.commands.activity", event), member.getActivities().isEmpty() ? MessageHelper.translateMessage("text.commands.nothing", event) : member.getActivities().toString(), false);
                                if (member.getNickname() != null)
                                    successEmbed.addField(MessageHelper.translateMessage("success.userInfo.nickname", event), member.getNickname(), false);
                                event.reply(new MessageBuilder(successEmbed.build()).build());
                            }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))),
                    userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
        }
    }
}