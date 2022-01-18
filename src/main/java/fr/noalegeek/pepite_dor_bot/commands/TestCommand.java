package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
        String[] args = event.getArgs().split("\\s+");
        List<String> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        for(MathsCommand.UnitType unitType : MathsCommand.UnitType.values()){
            for(int length = -1; length <= Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size(); length++){
                list.add(length == -1 ? unitType == MathsCommand.UnitType.LENGTH ? MessageHelper.translateMessage("text.maths.convert.lengthList", event) + "\n" : MessageHelper.translateMessage("text.maths.convert.timeList", event) + "\n" : length == Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() ? "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).unitName, event) + ")." : "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).unitName, event) + ");");
            }
        }
        /*if(str.length() > 1024){
            int charactersCount = 0;
            List<String> list = new ArrayList<>();
            for(char c : str.toCharArray()){
                stringBuilder.append(c);
                charactersCount++;
                if(charactersCount == 1024 || charactersCount == ((str.length() / 1024D) - Math.floor(str.length() / 1024D)) * 1024){
                    list.add(stringBuilder.toString());
                    charactersCount = 0;
                    stringBuilder.setLength(0);
                }
            }
            for(int i = 0; i < Math.ceil(str.length() / 1024D); i++){
                embedBuilder.addField(i == 0 ? "__" + MessageHelper.translateMessage("text.commands.syntaxError.informations", event) + "__" : "", list.get(i), false);
            }
        } else embedBuilder.addField("__" + MessageHelper.translateMessage("text.commands.syntaxError.informations", event) + "__", str, false);
        event.reply(embedBuilder.build());*/
        System.out.println(stringBuilder);
    }
}
