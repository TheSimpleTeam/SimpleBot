package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;

public class SuggestionCommand extends Command {

    public SuggestionCommand() {
        this.guildOnly = true;
        this.help = "Send a suggestion to the devs";
        this.name = "suggestion";
        this.aliases = new String[]{"sugg", "s"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        TextChannel suggestionChannel = event.getJDA().getGuildById(846048803554852904L).getTextChannelById(848599555540123648L);
        MessageEmbed builder = new EmbedBuilder()
                .setTitle(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " a fait une suggestion")
                .setColor(Color.magenta)
                .setTimestamp(Instant.now())
                .addField("Suggestion: ", "```" + event.getArgs() + "```", false)
                .build();
        suggestionChannel.sendMessage(builder).queue();
        event.replySuccess("**[**" + event.getAuthor().getAsMention() + "**]** La suggestion à bien été envoyée.");
    }
}