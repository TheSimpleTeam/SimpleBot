package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.OffsetDateTime;

public class GuildInfoCommand extends BotCommand {

    public GuildInfoCommand() {
        this.name = "guildinfo";
        this.aliases = new String[]{"guildi", "gi","ginfo"};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Guild guild = event.getGuild();
        MessageEmbed embed = new EmbedBuilder()
                .setAuthor(MessageHelper.getTag(event.getMember().getUser()), null, event.getMember().getUser().getEffectiveAvatarUrl())
                .setThumbnail(guild.getIconUrl())
                .addField("Nom du serveur", guild.getName(), false)
                .addField("Niveau de nitro", String.valueOf(guild.getBoostTier().getKey()), false)
                .addField("Créateur du serveur", guild.getOwner().getNickname() == null ? guild.getOwner().getUser().getName() : guild.getOwner().getNickname(), false)
                .addField("Membres sur le discord", String.valueOf(guild.getMemberCount()), false)
                .setColor(Color.GREEN)
                .setTimestamp(OffsetDateTime.now())
                .build();
        event.reply(embed);
    }
}
