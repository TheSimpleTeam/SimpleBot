package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Instant;

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
                .setThumbnail(event.getGuild().getIconUrl())
                .setTitle("Informations sur le serveur " + event.getGuild().getName())
                .addField("Niveau de nitro", String.valueOf(event.getGuild().getBoostTier().getKey()), false)
                .addField("Créateur du serveur", event.getGuild().getOwner().getNickname() == null ? event.getGuild().getOwner().getUser().getName() : event.getGuild().getOwner().getNickname(), false)
                .addField("Membres sur le discord", String.valueOf(event.getGuild().getMemberCount()), false)
                .setColor(Color.GREEN)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .build();
        event.reply(embedGuildInfo);
    }
}
