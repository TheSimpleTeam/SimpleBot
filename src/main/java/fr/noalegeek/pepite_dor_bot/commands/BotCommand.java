package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public abstract class BotCommand extends Command {

    @Override
    public String getCooldownError(CommandEvent event, int remaining)
    {
        if(remaining<=0)
            return null;
        return event.getClient().getWarning()+" Cette commande est encore en cooldown pendant "+remaining+" secondes";
    }

}
