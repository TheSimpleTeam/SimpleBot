package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SupportCommand extends Command {

    public SupportCommand() {
        this.name = "support";
        this.cooldown = 5;
        this.help = "Envoie le discord de support";
    }

    @Override
    protected void execute(CommandEvent event) {
        event.replySuccess("Et voici le discord officiel de " + event.getSelfUser().getName() + "\n https://discord.gg/jw3kn4gNZW");
    }

}
