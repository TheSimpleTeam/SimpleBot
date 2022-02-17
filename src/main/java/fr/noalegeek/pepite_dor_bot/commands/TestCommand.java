package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

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
        /*String[] args = event.getArgs().split("\\s+");
        List<String> list = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        StringBuilder stringB = new StringBuilder();
        stringB.append(MessageHelper.translateMessage("informations.maths", event));
        for(MathsCommand.UnitType unitType : MathsCommand.UnitType.values()){
            for(int index = -1; index <= Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() - 1; index++){
                stringB.append(index == -1 ? unitType == MathsCommand.UnitType.LENGTH ? MessageHelper.translateMessage("text.maths.convert.lengthList", event) + "\n" : MessageHelper.translateMessage("text.maths.convert.timeList", event) + "\n" : index == Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() - 1 ? "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).unitName, event) + ").\n" : "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(index).unitName, event) + ");\n");
            }
        }
        int charactersCount = 0, field = 0;
        for(char c : stringB.toString().toCharArray()){
            stringBuilder.append(c);
            charactersCount++;
            if (field == 0 ? charactersCount == 1024 : charactersCount / field == 1024 || (field == Math.floor(MessageHelper.translateMessage("informations.maths", event).toCharArray().length / 1024D) && (charactersCount / 1024D - field) * 1024 == (MessageHelper.translateMessage("informations.maths", event).toCharArray().length / 1024D - field) * 1024)) {
                field++;
                list.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        }
        for(String str : list){
            System.out.println(str + "\nHere\n");
        }*/

        /*for(MathsCommand.UnitType unitType : MathsCommand.UnitType.values()) {
            for (int length = -1; length <= Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size(); length++) {
                list.add(length == -1 ? unitType == MathsCommand.UnitType.LENGTH ? MessageHelper.translateMessage("text.maths.convert.lengthList", event) + "\n" : MessageHelper.translateMessage("text.maths.convert.timeList", event) + "\n" : length == Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().size() ? "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).unitName, event) + ")." : "- **" + Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).name() + "** (" + MessageHelper.translateMessage(Arrays.stream(MathsCommand.Unit.values()).filter(unit -> unit.unitType == unitType).toList().get(length - 1).unitName, event) + ");");
            }
        }
        int charactersCount = 0;
        for(String listPart : list){
            if(listPart.length() + charactersCount <= 1024){
                charactersCount += listPart.length();
                stringBuilder.append(listPart);
            } else {
                charactersCount = 0;

                //add an embed
                stringBuilder.setLength(0);
            }
        }
        if(str.length() > 1024){
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
    }
}
