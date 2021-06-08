package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Clock;
import java.time.OffsetDateTime;

public class GuildInfoCommand extends Command {

    public GuildInfoCommand() {
        this.name = "guildinfo";
        this.aliases = new String[]{"guildi", "gi","ginfo"};
        this.guildOnly = true;
        this.help = "Donne des informations sur le serveur.";
        this.cooldown = 5;
        this.category = CommandCategories.INFO.category;
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
                .setFooter("ℹ️ "+OffsetDateTime.now(Clock.systemUTC()))
                .build();
        event.reply(embedGuildInfo);
    }
}
