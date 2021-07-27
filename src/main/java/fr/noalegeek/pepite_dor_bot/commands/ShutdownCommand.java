package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        this.name = "shutdown";
        this.help = "Eteint le bot \"proprement\".";
        this.aliases = new String[]{"sd","shutd","sdown"};
        this.guildOnly = false;
        this.ownerCommand = true;
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "Le bot a bien été éteint.");
        event.getJDA().shutdown();
    }
}
