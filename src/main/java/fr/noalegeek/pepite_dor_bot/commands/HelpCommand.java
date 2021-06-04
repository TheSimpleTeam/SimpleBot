package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.stream.Collectors;

public class HelpCommand extends BotCommand {

    /**
     * Experimental command
     */

    public HelpCommand() {
        this.name = "help";
        this.hidden = true;
        this.guildOnly = true;
        this.aliases = new String[]{"aide", "?"};
        this.help = "Envoie un message de toutes les commandes ainsi que leur utilité.";
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("**" + event.getSelfUser().getName() + "** " + "commands :")
                .setAuthor(event.getAuthor().getName())
                .setColor(Color.GREEN);
        Category lastCategory = null;
        for (Command command : event.getClient().getCommands().stream().filter(command -> !command.isHidden() && command.getHelp() != null &&
                !command.getHelp().isEmpty()).collect(Collectors.toList())) {
            if(command.getCategory() != null && lastCategory != command.getCategory()) {
                lastCategory = command.getCategory();
                builder.addBlankField(false);
                builder.addField(lastCategory.getName() + " commands:", "", false);
            }
            builder.addField(event.getClient().getPrefix() + command.getName() + " " + command.getArguments(), command.getHelp(), false);
        }
        event.replyInDm(builder.build());
    }
}
