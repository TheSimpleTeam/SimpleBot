package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.OffsetDateTime;


public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.name = "user";
        this.aliases = new String[]{"userinfo", "ui"};
    }

    @Override
    protected void execute(CommandEvent event) {
        final String[] args = event.getArgs().split(" ");

        Member member = event.getMember();
        if (MessageHelper.getMember(args[0], event.getGuild()) != null) {
            member = MessageHelper.getMember(args[0], event.getGuild());
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.GREEN)
                .addField("Nom d'utilistateur", MessageHelper.getTag(member.getUser()), false)
                .addField("Identifiant", member.getId(), false)
                .addField("Date de création du compte", MessageHelper.formatDate(member.getTimeCreated()), false)
                .addField("Cet utilisateur à rejoint le", MessageHelper.formatDate(member.getTimeJoined()), false)
                .setAuthor(MessageHelper.getTag(member.getUser()), null, member.getUser().getEffectiveAvatarUrl())
                .build();
        event.reply(embed);
    }
}