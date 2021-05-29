package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;

public class HelpCommand extends Command {

    /**
     * Experimental command
     */

    public HelpCommand() {
        this.name = "help";
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("**" + event.getSelfUser().getName() + "** " + "commands :")
                .setAuthor(event.getAuthor().getName())
                .setColor(Color.GREEN);
        Category lastCategory = null;
        for (Command command : event.getClient().getCommands()) {
            if(lastCategory == null || lastCategory != command.getCategory()) {
                lastCategory = command.getCategory();
                builder.addBlankField(false);
                builder.addBlankField(false);
                builder.addField(category.getName() + " commands:", "", false);
                builder.addBlankField(false);
            }
            builder.addField( event.getClient().getPrefix() + command.getName() + command.getArguments(), command.getHelp(), false);
            builder.addBlankField(false);
            event.replyInDm(builder.build());
        }
    }
}
