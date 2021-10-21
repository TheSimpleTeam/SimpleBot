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
        StringBuilder b = new StringBuilder();
        Main.getClient().getCommands().stream().map(Command::getName).sorted().forEachOrdered(e -> b.append(e).append('\n'));
        event.reply(b.toString());
    }
}
