package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
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
        this.help = "Donne des informations sur le bot Pépite d'or Bot.";
        this.category = CommandCategories.INFO.category;
    }
    @Override
    protected void execute(CommandEvent event) {
        MessageEmbed embedBotInfo = new EmbedBuilder()
                .setFooter("ℹ " + Instant.now())
                .setColor(Color.BLUE)
                .addField("Nom de l'utilistateur", event.getSelfMember().getNickname() == null ? event.getSelfUser().getName() : event.getSelfMember().getNickname(), false)
                .addField("Identifiant de l'utilisateur", event.getSelfUser().getId(), false)
                .addField("Date de création du compte", MessageHelper.formatShortDate(event.getSelfMember().getTimeCreated()), false)
                .addField("Cet utilisateur à rejoint le", MessageHelper.formatShortDate(event.getSelfMember().getTimeJoined()), false)
                .addField("Joue actuellement à", event.getSelfUser().getJDA().getPresence().getActivity().getName(), false)
                .addField("Status", StringUtils.capitalize(String.valueOf(event.getSelfUser().getJDA().getPresence().getStatus()).toLowerCase(Locale.ROOT).replaceAll("_", "")), false)
                .setAuthor(MessageHelper.getTag(event.getSelfUser()), null, event.getSelfUser().getEffectiveAvatarUrl())
                .build();
        event.getSelfUser().getJDA().getPresence().getStatus();
        event.reply(embedBotInfo);
    }
}
