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
        if(event.getJDA().getGuildById("846048803554852904") == null) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed("error.suggestion.guildNull", event).build()).build());
            return;
        }
        if (event.getJDA().getGuildById("846048803554852904").getTextChannelById("848599555540123648") == null) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed("error.suggestion.channelNull", event).build()).build());
            return;
        }
        event.getJDA().getGuildById("846048803554852904").getTextChannelById("848599555540123648").sendMessage(new MessageBuilder(MessageHelper.getEmbed("success.suggestion.suggestion", event)
                .setTitle(new StringBuilder().append(UnicodeCharacters.electricLightBulbEmoji).append(" ").append("success.suggestion.suggestion").toString())
                .setColor(Color.YELLOW)
                .addField(MessageHelper.translateMessage("success.suggestion", event), event.getArgs(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.author", event), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.id", event), event.getAuthor().getName(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.tag", event), "#" + event.getAuthor().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.guildName", event), event.getGuild().getName(), false)
                .addField(MessageHelper.translateMessage("success.suggestion.guildID", event), event.getGuild().getId(), false)
                .build()).build()).queue();
        event.reply(new MessageBuilder(MessageHelper.getEmbed("success.suggestion.success", event).build()).build());
    }
}