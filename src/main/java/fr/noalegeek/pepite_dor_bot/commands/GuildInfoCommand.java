package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
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
        EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.guildInfo.success", Color.BLUE, null, event.getGuild().getIconUrl(), event.getGuild().getName())
                .setTitle(new StringBuilder().append(UnicodeCharacters.informationSourceEmoji).append(" ").append(String.format(MessageHelper.translateMessage(event, "success.guildInfo.success"), event.getGuild().getName())).toString())
                .addField(MessageHelper.translateMessage(event, "success.guildInfo.nitroLevel"), String.valueOf(event.getGuild().getBoostTier().getKey()), false)
                .addField(MessageHelper.translateMessage(event, "success.guildInfo.membersOnTheServer"), String.valueOf(event.getGuild().getMemberCount()), false)
                .addField(MessageHelper.translateMessage(event, "success.guildInfo.membersConnectedToTheServer"), String.valueOf(event.getGuild().getMembers().stream().filter(member -> member.getOnlineStatus() != OnlineStatus.OFFLINE).toList().size()), false);
        if(event.getGuild().getOwner() != null) embedBuilder.addField(MessageHelper.translateMessage(event, "success.guildInfo.serverOwner"), event.getGuild().getOwner().getEffectiveName(), false);
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }
}
