package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

public class ConfigCommand extends Command {

    public ConfigCommand(){
        this.name = "config";
        this.cooldown = 5;
        this.help = "help.config";
        this.example = "example.config";
        this.aliases = new String[]{"cf", "parameter", "par"};
        this.arguments = "arguments.config";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3){
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        switch(args.length){
            case 2 -> {

            }
            case 3 -> {

            }
        }
    }
}
