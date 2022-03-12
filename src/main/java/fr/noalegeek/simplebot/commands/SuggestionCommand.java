package fr.noalegeek.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.simplebot.utils.UnicodeCharacters;
import fr.noalegeek.simplebot.enums.CommandCategories;
import fr.noalegeek.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.Color;

public class SuggestionCommand extends Command {

    public SuggestionCommand() {
        this.guildOnly = true;
        this.help = "help.suggestion";
        this.name = "suggestion";
        this.aliases = new String[]{"su", "suggest"};
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
        if(event.getJDA().getGuildById("846048803554852904") == null) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.suggestion.guildNull", null, null, null, (Object[]) null).build()).build());
            return;
        }
        if (event.getJDA().getGuildById("846048803554852904").getTextChannelById("848599555540123648") == null) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.suggestion.channelNull", null, null, null, (Object[]) null).build()).build());
            return;
        }
        event.getJDA().getGuildById("846048803554852904").getTextChannelById("848599555540123648").sendMessage(new MessageBuilder(MessageHelper.getEmbed(event, "success.suggestion.suggestion", null, null, null, (Object[]) null)
                .setTitle(new StringBuilder().append(UnicodeCharacters.electricLightBulbEmoji).append(" ").append("success.suggestion.suggestion").toString())
                .setColor(Color.YELLOW)
                .addField(MessageHelper.translateMessage(event, "success.suggestion"), event.getArgs(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.author"), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.id"), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.tag"), "#" + event.getAuthor().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.guildName"), event.getGuild().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.suggestion.guildID"), event.getGuild().getId(), false)
                .build()).build()).queue();
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.suggestion.success", null, null, null, (Object[]) null).build()).build());
    }
}