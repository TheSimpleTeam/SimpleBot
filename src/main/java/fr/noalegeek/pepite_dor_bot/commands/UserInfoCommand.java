package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class UserInfoCommand extends Command {

    public UserInfoCommand() {
        this.name = "userinfo";
        this.aliases = new String[]{"useri", "ui","uinfo"};
        this.arguments = "<mention de l'utilisateur>";
        this.guildOnly = true;
        this.cooldown = 5;
        this.help = "Donne des informations sur l'auteur ou sur la personne mentionnée.";
        this.example = "@minemobs";
        this.category = CommandCategories.INFO.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        Member member = event.getMember();
        User user = event.getAuthor();
        if(!event.getMessage().getMentionedMembers().isEmpty() && event.getMessage().getMentionedMembers().get(0) != null && !event.getMessage().getMentionedUsers().isEmpty() &&
                event.getMessage().getMentionedUsers().get(0) != null) {
            member = event.getMessage().getMentionedMembers().get(0);
            user = event.getMessage().getMentionedUsers().get(0);
        } else if(event.getMessage().getMentionedMembers().isEmpty() && !event.getMessage().getMentionedUsers().isEmpty() &&
                event.getMessage().getMentionedUsers().get(0) != null) {
            user = event.getMessage().getMentionedUsers().get(0);
        }
        MessageEmbed embedUserInfo = new EmbedBuilder()
                .setFooter("ℹ "+Instant.now())
                .setColor(Color.BLUE)
                .addField("Nom d'utilistateur", member.getNickname() == null ? member.getUser().getName() : member.getNickname(), false)
                .addField("Identifiant", member.getUser().getId(), false)
                .addField("Date de création du compte", MessageHelper.formatShortDate(member.getTimeCreated()), true)
                .addField("Cet utilisateur à rejoint le", MessageHelper.formatShortDate(member.getTimeJoined()), false)
                .addField("Joue à", getUserActivityName(member), false)
                .setAuthor(MessageHelper.getTag(user), null, user.getEffectiveAvatarUrl())
                .build();
        event.reply(embedUserInfo);
    }

    private String getUserActivityName(Member member) {
        if(member.getActivities().isEmpty() || member.getActivities().get(0) == null) return "Rien";
        return member.getActivities().get(0).getName();
    }
}