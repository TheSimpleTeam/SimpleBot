package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        MessageEmbed embedBotInfo = new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setColor(Color.BLUE)
                .addField(MessageHelper.translateMessage("text.botInfo.name", event), event.getSelfMember().getNickname() == null ? event.getSelfUser().getName() : event.getSelfMember().getNickname(), false)
                .addField(MessageHelper.translateMessage("text.botInfo.id", event), event.getSelfUser().getId(), false)
                .addField(MessageHelper.translateMessage("text.botInfo.creationDate", event), MessageHelper.formatShortDate(event.getSelfMember().getTimeCreated()), false)
                .addField(MessageHelper.translateMessage("text.botInfo.joinDate", event), MessageHelper.formatShortDate(event.getSelfMember().getTimeJoined()), false)
                .addField(MessageHelper.translateMessage("text.botInfo.activity", event), getActivity(event), false)
                .addField(MessageHelper.translateMessage("text.botInfo.status", event), StringUtils.capitalize(String.valueOf(event.getSelfUser().getJDA().getPresence().getStatus()).toLowerCase(Locale.ROOT).replaceAll("_", "")), false)
                .setAuthor(MessageHelper.getTag(event.getSelfUser()), null, event.getSelfUser().getEffectiveAvatarUrl())
                .build();
        event.reply(embedBotInfo);
    }

    private String getActivity(CommandEvent event) {
        try {
            return event.getSelfUser().getJDA().getPresence().getActivity().getName();
        } catch (NullPointerException ignore){
            return MessageHelper.translateMessage("test.botInfo.getActivity", event);
        }
    }
}
