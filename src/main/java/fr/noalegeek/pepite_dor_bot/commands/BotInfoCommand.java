package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.time.Instant;
import java.util.Locale;

public class BotInfoCommand extends Command {

    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"bi","boti","binfo"};
        this.guildOnly = true;
        this.cooldown = 5;
        this.help = "help.botInfo";
        this.category = CommandCategories.INFO.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage(event, "success.unshortURL.success")))
                .setTimestamp(Instant.now())
                .setColor(Color.BLUE)
                .setThumbnail(event.getSelfUser().getEffectiveAvatarUrl())
                .addField(MessageHelper.translateMessage(event, "success.botInfo.id"), event.getSelfUser().getId(), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.tag"), "#" + event.getSelfUser().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.creationDate"), MessageHelper.formatShortDate(event.getSelfMember().getTimeCreated()), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.joinDate"), MessageHelper.formatShortDate(event.getSelfMember().getTimeJoined()), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.activity"), event.getSelfUser().getJDA().getPresence().getActivity() == null ? MessageHelper.translateMessage(event, "text.commands.nothing") : event.getSelfUser().getJDA().getPresence().getActivity().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.status"), StringUtils.capitalize(String.valueOf(event.getSelfUser().getJDA().getPresence().getStatus()).toLowerCase(Locale.ROOT).replace("_", "")), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.github"), "https://github.com/TheSimpleTeam/SimpleBot", false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.invitationLink"), String.format("https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=8589934591", event.getJDA().getSelfUser().getId()), false)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl());
        if(event.getSelfMember().getNickname() != null) successEmbed.addField(MessageHelper.translateMessage(event, "success.botInfo.name"), event.getSelfUser().getName(), false);
        if(Main.getServerConfig().prefix().get(event.getGuild().getId()) != null) successEmbed.addField(MessageHelper.translateMessage(event, "success.botInfo.serverPrefix"), Main.getServerConfig().prefix().get(event.getGuild().getId()), false).addField(MessageHelper.translateMessage(event, "success.botInfo.prefix"), Main.getInfos().prefix(), false);
        event.reply(new MessageBuilder(successEmbed.build()).build());
    }
}
