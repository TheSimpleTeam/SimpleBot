package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Instant;
import java.util.stream.Collectors;

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
                .setTitle("\u2139 " + String.format(MessageHelper.translateMessage("success.guildInfo.serverName", event), event.getGuild().getName()))
                .setThumbnail(event.getGuild().getIconUrl())
                .addField(MessageHelper.translateMessage("success.guildInfo.nitroLevel", event), String.valueOf(event.getGuild().getBoostTier().getKey()), false)
                .addField(MessageHelper.translateMessage("success.guildInfo.serverOwner", event), event.getGuild().getOwner().getEffectiveName(), false)
                .addField(MessageHelper.translateMessage("success.guildInfo.membersOnTheServer", event), String.valueOf(event.getGuild().getMemberCount()), false)
                .addField(MessageHelper.translateMessage("success.guildInfo.membersConnectedToTheServer", event), String.valueOf(event.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).toList().size()), false)
                .setColor(Color.GREEN)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .build();
        event.reply(embedGuildInfo);
    }
}
