package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PingCommand extends Command {
    public PingCommand(){
        this.cooldown = 1;
        this.aliases = new String[]{"Pong","P"};
        this.name = "Ping";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        event.getMessage().reply("Hey "+event.getChannel()).queue();
    }
}
