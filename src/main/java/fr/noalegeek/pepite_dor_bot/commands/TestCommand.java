package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.DiscordFormatUtils;

import java.util.ArrayList;

public class TestCommand extends Command {

    public TestCommand() {
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
        StringBuilder stringBuilder = new StringBuilder().append("\"");
        ArrayList<String> str1 = new ArrayList<>();
        ArrayList<String> str2 = new ArrayList<>();
        for(int i = "channel".toCharArray().length; i > 0; i--){
            str1.add("channel".substring(0, i));
        }
        for(int i = "member".toCharArray().length; i > 0; i--){
            str2.add("member".substring(0, i));
        }
        for(String part1 : str1){
            for(String part2 : str2){
                if(!(part1 + part2).equals("channelmember")) stringBuilder.append(part1).append(part2).append("\",\"");
            }
        }
        event.reply(stringBuilder.toString());
    }
}
