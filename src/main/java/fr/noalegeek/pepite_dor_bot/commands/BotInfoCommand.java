package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.OffsetDateTime;

public class BotInfoCommand extends BotCommand {
    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"bi"};
        this.guildOnly = true;
        this.cooldown = 5;
    }
    @Override
    protected void execute(CommandEvent event) {
        MessageEmbed embedBotInfo = new EmbedBuilder()
                .setTimestamp(OffsetDateTime.now())
                .setColor(Color.BLUE)
                .addField("Nom de l'utilistateur", event.getSelfMember().getNickname() == null ? event.getSelfUser().getName() : event.getSelfMember().getNickname(), false)
                .addField("Identifiant de l'utilisateur", event.getSelfUser().getId(), false)
                .addField("Date de création du compte", MessageHelper.formatDate(event.getSelfMember().getTimeCreated()), false)
                .addField("Cet utilisateur à rejoint le", MessageHelper.formatDate(event.getSelfMember().getTimeJoined()), false)
                .addField("Joue actuellement à", event.getSelfUser().getJDA().getPresence().getActivity().getName(), false)
                .addField("Status",MessageHelper.formatEnum(event.getSelfUser().getJDA().getPresence().getStatus()), false)
                .setAuthor(MessageHelper.getTag(event.getSelfUser()), null, event.getSelfUser().getEffectiveAvatarUrl())
                .build();
        event.getSelfUser().getJDA().getPresence().getStatus().getKey();
        event.reply(embedBotInfo);
    }
}
