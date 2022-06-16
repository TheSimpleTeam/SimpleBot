package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.Color;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        this.name = "shutdown";
        this.help = "help.shutdown";
        this.aliases = new String[]{"sd","shutd","sdown"};
        this.guildOnly = false;
        this.ownerCommand = true;
        this.hidden = true;
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.shutdown", Color.GREEN, null, null).build()).build()).queue(e -> event.getJDA().shutdown());
    }
}
