package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.thesimpleteam.simplebot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.OnlineStatus;

import java.awt.Color;

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
        EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.guildInfo.success", Color.BLUE, null, event.getGuild().getIconUrl())
                .setTitle(new StringBuilder().append(UnicodeCharacters.INFORMATION_SOURCE_EMOJI).append(" ").append(String.format(MessageHelper.translateMessage(event, "success.guildInfo.success"), event.getGuild().getName())).toString())
                .addField(MessageHelper.translateMessage(event, "success.guildInfo.nitroLevel"), String.valueOf(event.getGuild().getBoostTier().getKey()), false)
                .addField(MessageHelper.translateMessage(event, "success.guildInfo.membersOnTheServer"), String.valueOf(event.getGuild().getMemberCount()), false)
                .addField(MessageHelper.translateMessage(event, "success.guildInfo.membersConnectedToTheServer"), String.valueOf(event.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).toList().size()), false);
        if(event.getGuild().getOwner() != null) embedBuilder.addField(MessageHelper.translateMessage(event, "success.guildInfo.serverOwner"), event.getGuild().getOwner().getEffectiveName(), false);
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }
}
