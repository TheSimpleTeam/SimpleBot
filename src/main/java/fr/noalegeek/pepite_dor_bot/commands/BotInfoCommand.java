package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.OffsetDateTime;

public class BotInfoCommand extends Command {

    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"bi"};
        this.cooldown = 5;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        User selfUser = event.getSelfUser();
        Member selfMember = event.getSelfMember();
        MessageEmbed embed = new EmbedBuilder()
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.GREEN)
                .addField("Nom d'utilistateur", selfMember.getNickname() == null ? selfUser.getName() : selfMember.getNickname(), false)
                .addField("Identifiant", selfUser.getId(), false)
                .addField("Date de création du compte", MessageHelper.formatDate(selfMember.getTimeCreated()), false)
                .addField("Cet utilisateur à rejoint le", MessageHelper.formatDate(selfMember.getTimeJoined()), false)
                .addField("Joue à ", selfUser.getJDA().getPresence().getActivity().getName(), false)
                .addField("Status ", selfUser.getJDA().getPresence().getStatus().getKey(), false)
                .setAuthor(MessageHelper.getTag(selfUser), null, selfUser.getEffectiveAvatarUrl())
                .build();
        event.reply(embed);
    }
}
