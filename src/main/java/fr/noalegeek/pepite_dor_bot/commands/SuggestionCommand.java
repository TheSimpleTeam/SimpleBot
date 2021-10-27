package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
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
        this.aliases = new String[]{"sugg", "su","sug","sugge","suggest", "suggesti", "suggestio", "sugges"};
        this.cooldown = 30;
        this.arguments = "arguments.suggestion";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        if(event.getArgs().isEmpty()) event.reply(MessageHelper.syntaxError(event,this, null));
        if(event.getJDA().getGuildById(846048803554852904L).getTextChannelById(848599555540123648L) == null){
            EmbedBuilder errorChannelNullEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " ");
            //TODO Embed
            return;
        }
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setTitle("\u1F4A" + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " a fait une suggestion.")
                .setColor(Color.YELLOW)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                .setTimestamp(Instant.now())
                .addField("Suggestion  ", "```" + event.getArgs() + "```", false);
        event.getJDA().getGuildById(846048803554852904L).getTextChannelById(848599555540123648L).sendMessage(new MessageBuilder(successEmbed.build()).build()).queue();
        event.reply(MessageHelper.formattedMention(event.getAuthor()) + "La suggestion à bien été envoyée.");
        event.getMessage().delete().queue();
    }
}