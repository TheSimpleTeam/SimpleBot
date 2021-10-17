package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;
import java.time.Clock;
import java.time.OffsetDateTime;

public class SuggestionCommand extends Command {

    public SuggestionCommand() {
        this.guildOnly = true;
        this.help = "Envoie une suggestion aux développeurs.";
        this.name = "suggestion";
        this.aliases = new String[]{"sugg", "su","sug","sugge","suggest"};
        this.cooldown = 30;
        this.arguments = "<suggestion>";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        TextChannel suggestionChannel = event.getJDA().getGuildById(846048803554852904L).getTextChannelById(848599555540123648L);
        EmbedBuilder embedSuggestion = new EmbedBuilder()
                .setTitle(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " a fait une suggestion.")
                .setColor(Color.YELLOW)
                .setFooter("💡 "+OffsetDateTime.now(Clock.systemUTC()))
                .addField("Suggestion  ", "```" + event.getArgs() + "```", false);
        if(event.getArgs().isEmpty()){
            event.reply(MessageHelper.syntaxError(event,this, null));
        }
        suggestionChannel.sendMessage(new MessageBuilder(embedSuggestion.build()).build()).queue();
        event.reply(MessageHelper.formattedMention(event.getAuthor()) + "La suggestion à bien été envoyée.");
        event.getMessage().delete().queue();
    }
}