package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;

public class ConfigCommand extends Command {

    public ConfigCommand(){
        this.name = "config";
        this.cooldown = 5;
        this.help = "help.channelMember";
        this.example = "join 848965362971574282\nleave reset";
        this.aliases = new String[]{"cf", "parameter", "par"};
        this.arguments = "arguments.channelMember";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {

    }
}
