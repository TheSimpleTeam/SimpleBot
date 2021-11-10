package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.Color;
import java.time.Instant;

public class SayCommand extends Command {

    public SayCommand() {
        this.name = "say";
        this.cooldown = 5;
        this.arguments = "arguments.say";
        this.aliases = new String[]{"s"};
        this.help = "help.say";
        this.example = "example.say";
        this.category = CommandCategories.STAFF.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            MessageHelper.syntaxError(event,this, "syntax.say");
            return;
        }
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji,
                        MessageHelper.translateMessage("success.say.success", event)))
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN)
                .setDescription(event.getArgs());
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) successEmbed.setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl());
        event.reply(new MessageBuilder(successEmbed.build()).build());
        event.getMessage().delete().queue();
    }
}
