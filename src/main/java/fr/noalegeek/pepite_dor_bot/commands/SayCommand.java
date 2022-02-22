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
            MessageHelper.syntaxError(event,this, "informations.say");
            return;
        }
        event.getChannel().sendMessage(new StringBuilder().append((!event.getMember().hasPermission(Permission.MESSAGE_MANAGE) ? new StringBuilder(MessageHelper.getTag(event.getAuthor())).append(" ").toString() : "")).append(event.getArgs()).toString()).queue(unused -> event.getMessage().delete().queue());
    }
}
