package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.utils.UnicodeCharacters;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.Color;

public class SuggestionCommand extends Command {

    public SuggestionCommand() {
        this.guildOnly = true;
        this.help = "help.suggestion";
        this.name = "suggestion";
        this.aliases = new String[]{"sugg", "suggest"};
        this.cooldown = 30;
        this.arguments = "arguments.suggestion";
        this.category = CommandCategories.UTILITY.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            MessageHelper.syntaxError(event, this, "information.suggestion");
            return;
        }
        var devGuild = event.getJDA().getGuildById("846048803554852904");
        if(devGuild == null) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.suggestion.guildNull", null, null, null).build()).build());
            return;
        }
        var suggestionChannel = devGuild.getTextChannelById("848599555540123648");
        if(suggestionChannel == null) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.suggestion.channelNull", null, null, null).build()).build());
            return;
        }
        suggestionChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event, "success.suggestion.suggestion", Color.YELLOW, null, null)
                .setTitle(new StringBuilder().append(UnicodeCharacters.ELECTRIC_LIGHT_BULB_EMOJI).append(" ").append(MessageHelper.translateMessage(event, "success.suggestion.suggestion")).toString())
                .addField(MessageHelper.translateMessage(event, "success.suggestion.suggestion"), new StringBuilder().append("```").append(event.getArgs()).append("```").toString(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.author"), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.id"), event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.guildName"), event.getGuild().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.guildID"), event.getGuild().getId(), false)
                .build()).build()).queue(unused -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.suggestion.success", null, null, null)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.suggestion"), new StringBuilder().append("```").append(event.getArgs()).append("```").toString(), false)
                .build()).build()));
    }
}