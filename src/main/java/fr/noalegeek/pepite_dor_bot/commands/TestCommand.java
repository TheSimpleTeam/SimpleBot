package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;

public class TestCommand extends Command {

    public TestCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "help.test";
        this.cooldown = 5;
        this.name = "test";
        this.hidden = true;
        this.aliases = new String[]{"t", "te", "tes"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        StringBuilder stringBuilder = new StringBuilder().append("\"");
        for(String part1 : new String[]{"temp", "tem", "te", "t"}){
            for(String part2 : new String[]{"b", "ban", "ba"}){
                if(!(part1 + part2).equals("tempban")){
                    stringBuilder.append(part1).append(part2).append("\",\"");
                }
            }
        }
        event.reply(stringBuilder.toString());
    }
}
