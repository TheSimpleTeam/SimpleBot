package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.Color;
import java.time.Instant;

public class SuggestionCommand extends Command {

    public SuggestionCommand() {
        this.guildOnly = true;
        this.help = "help.suggestion";
        this.name = "suggestion";
        this.aliases = new String[]{"su", "suggest"};
        this.cooldown = 30;
        this.arguments = "arguments.suggestion";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.getArgs().isEmpty()) MessageHelper.syntaxError(event,this, null);
        if(event.getJDA().getGuildById(846048803554852904L) == null) {
            EmbedBuilder errorGuildNullEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.suggestion.guildNull", event));
            event.reply(new MessageBuilder(errorGuildNullEmbed.build()).build());
            return;
        }
        if (event.getJDA().getGuildById(846048803554852904L).getTextChannelById(848599555540123648L) == null) {
            EmbedBuilder errorChannelNullEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.suggestion.channelNull", event));
            event.reply(new MessageBuilder(errorChannelNullEmbed.build()).build());
            return;
        }
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.suggestion.success", event));
        EmbedBuilder successSuggestionEmbed = new EmbedBuilder()
                .setTitle(UnicodeCharacters.electricLightBulbEmoji + " " + MessageHelper.translateMessage("success.suggestion", event))
                .setColor(Color.YELLOW)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .addField(MessageHelper.translateMessage("success.suggestion", event), event.getArgs(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.author", event), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.id", event), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.tag", event), "#" + event.getAuthor().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.guildName", event), event.getGuild().getName(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.guildID", event), event.getGuild().getId(), false);
        event.getJDA().getGuildById(846048803554852904L).getTextChannelById(848599555540123648L).sendMessage(new MessageBuilder(successSuggestionEmbed.build()).build()).queue();
        event.reply(new MessageBuilder(successEmbed.build()).build());
    }
}