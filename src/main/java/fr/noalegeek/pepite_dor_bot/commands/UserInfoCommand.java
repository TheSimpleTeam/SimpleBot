package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.OffsetDateTime;


public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.name = "user";
        this.aliases = new String[]{"userinfo", "ui"};
        this.arguments = "[@User]";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Member member = event.getMember();

        if(!event.getMessage().getMentionedMembers().isEmpty() && event.getMessage().getMentionedMembers().get(0) != null) {
            member = event.getMessage().getMentionedMembers().get(0);
        }

        MessageEmbed embed = new EmbedBuilder()
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.GREEN)
                .addField("Nom d'utilistateur", member.getNickname() == null ? member.getUser().getName() : member.getNickname(), false)
                .addField("Identifiant", member.getUser().getId(), false)
                .addField("Date de création du compte", MessageHelper.formatDate(member.getTimeCreated()), true)
                .addField("Cet utilisateur à rejoint le", MessageHelper.formatDate(member.getTimeJoined()), false)
                .addField("Joue à ", getUserActivityName(member), false)
                .setAuthor(MessageHelper.getTag(member.getUser()), null, member.getUser().getEffectiveAvatarUrl())
                .build();
        event.reply(embed);
    }

    private String getUserActivityName(Member member) {
        if(member.getActivities().isEmpty() || member.getActivities().get(0) == null) return "rien";
        return member.getActivities().get(0).getName();
    }
}