package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;

public class TestCommand extends Command {

    public TestCommand(){
        this.category = CommandCategories.MISC.category;
        this.help = "Une commande de test selon les tests à faire.";
        this.cooldown = 5;
        this.name = "test";
        this.hidden = true;
        this.aliases = new String[]{"t", "te", "tes"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        long number = Long.parseLong(args[0]);
        StringBuilder stringBuilder = new StringBuilder().append(number).append(" ");
        while (number != 1){
            if(number % 2 == 0) number /= 2;
            else number = number * 3 + 1;
            stringBuilder.append("; ").append(number).append(" ");
        }
        event.reply(String.valueOf(stringBuilder));
    }
}
