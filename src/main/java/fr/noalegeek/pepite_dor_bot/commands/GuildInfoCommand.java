package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;

import java.awt.Color;
import java.time.Instant;

public class GuildInfoCommand extends Command {

    public GuildInfoCommand() {
        this.name = "guildinfo";
        this.aliases = new String[]{"guildi", "gi","ginfo"};
        this.guildOnly = true;
        this.help = "help.guildInfo";
        this.cooldown = 5;
        this.category = CommandCategories.INFO.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setTitle("\u2139 " + String.format(MessageHelper.translateMessage("success.guildInfo.serverName", event), event.getGuild().getName()))
                .setThumbnail(event.getGuild().getIconUrl())
                .addField(MessageHelper.translateMessage("success.guildInfo.nitroLevel", event), String.valueOf(event.getGuild().getBoostTier().getKey()), false)
                .addField(MessageHelper.translateMessage("success.guildInfo.serverOwner", event), event.getGuild().getOwner().getEffectiveName(), false)
                .addField(MessageHelper.translateMessage("success.guildInfo.membersOnTheServer", event), String.valueOf(event.getGuild().getMemberCount()), false)
                .addField(MessageHelper.translateMessage("success.guildInfo.membersConnectedToTheServer", event), String.valueOf(event.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).toList().size()), false)
                .setColor(Color.BLUE)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl());
        event.reply(new MessageBuilder(successEmbed.build()).build());
    }
}
