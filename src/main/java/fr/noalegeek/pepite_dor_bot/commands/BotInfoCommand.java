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
                .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.unshortURL.success", event)))
                .setTimestamp(Instant.now())
                .setColor(Color.BLUE)
                .setThumbnail(event.getSelfUser().getEffectiveAvatarUrl())
                .addField(MessageHelper.translateMessage("success.botInfo.id", event), event.getSelfUser().getId(), false)
                .addField(MessageHelper.translateMessage("success.botInfo.tag", event), "#" + event.getSelfUser().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage("success.botInfo.creationDate", event), MessageHelper.formatShortDate(event.getSelfMember().getTimeCreated()), false)
                .addField(MessageHelper.translateMessage("success.botInfo.joinDate", event), MessageHelper.formatShortDate(event.getSelfMember().getTimeJoined()), false)
                .addField(MessageHelper.translateMessage("success.botInfo.activity", event), event.getSelfUser().getJDA().getPresence().getActivity() == null ? MessageHelper.translateMessage("text.commands.nothing", event) : event.getSelfUser().getJDA().getPresence().getActivity().getName(), false)
                .addField(MessageHelper.translateMessage("success.botInfo.status", event), StringUtils.capitalize(String.valueOf(event.getSelfUser().getJDA().getPresence().getStatus()).toLowerCase(Locale.ROOT).replace("_", "")), false)
                .addField(MessageHelper.translateMessage("success.botInfo.github", event), "https://github.com/TheSimpleTeam/SimpleBot", false)
                .addField(MessageHelper.translateMessage("success.botInfo.invitationLink", event), String.format("https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=8589934591", event.getJDA().getSelfUser().getId()), false)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl());
        if(event.getSelfMember().getNickname() != null) successEmbed.addField(MessageHelper.translateMessage("success.botInfo.name", event), event.getSelfUser().getName(), false);
        if(Main.getServerConfig().prefix().get(event.getGuild().getId()) != null) successEmbed.addField(MessageHelper.translateMessage("success.botInfo.serverPrefix", event), Main.getServerConfig().prefix().get(event.getGuild().getId()), false).addField(MessageHelper.translateMessage("success.botInfo.prefix", event), Main.getInfos().prefix(), false);
        event.reply(new MessageBuilder(successEmbed.build()).build());
    }
}
