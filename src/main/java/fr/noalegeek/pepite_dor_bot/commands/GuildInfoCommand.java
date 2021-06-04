package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.OffsetDateTime;

public class GuildInfoCommand extends BotCommand {

    public GuildInfoCommand() {
        this.name = "event.getGuild()info";
        this.aliases = new String[]{"event.getGuild()i", "gi","ginfo"};
        this.guildOnly = true;
        this.help = "Donne des informations sur le serveur.";
        this.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageEmbed embedGuildInfo = new EmbedBuilder()
                .setAuthor(MessageHelper.getTag(event.getMember().getUser()), null, event.getMember().getUser().getEffectiveAvatarUrl())
                .setThumbnail(event.getGuild().getIconUrl())
                .addField("Nom du serveur", event.getGuild().getName(), false)
                .addField("Niveau de nitro", String.valueOf(event.getGuild().getBoostTier().getKey()), false)
                .addField("Créateur du serveur", event.getGuild().getOwner().getNickname() == null ? event.getGuild().getOwner().getUser().getName() : event.getGuild().getOwner().getNickname(), false)
                .addField("Membres sur le discord", String.valueOf(event.getGuild().getMemberCount()), false)
                .setColor(Color.GREEN)
                .setTimestamp(OffsetDateTime.now())
                .build();
        event.reply(embedGuildInfo);
    }
}
