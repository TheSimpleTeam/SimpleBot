package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
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
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = "spigot".toCharArray().length - 1; i > 0; i--){
            stringBuilder.append("\"").append("spigot".substring(0, i)).append("\",");
        }
        event.reply(stringBuilder.toString());
    }
}
