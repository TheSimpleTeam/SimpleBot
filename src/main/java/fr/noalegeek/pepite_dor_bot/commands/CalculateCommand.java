package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Expression;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CalculateCommand extends Command {

    public CalculateCommand() {
        this.name = "calculate";
        this.aliases = new String[]{"c", "calc", "ca", "cal", "calcul", "calcu", "calcula", "calculat"};
        this.arguments = "arguments.calculate";
        this.cooldown = 5;
        this.help = "help.calculate";
        this.example = "example.calculate";
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().split("\\s+").length == 0 || event.getArgs().isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        if(!new Expression(replaceAll(event.getArgs().replaceAll("\\s+", ""))).checkSyntax()){
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.calculate", event), event.getArgs().replaceAll("\\s+", "")));
            return;
        }
        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.calculate", event), replaceAll(event.getArgs().replaceAll("\\s+", "")), new Expression(replaceAll(event.getArgs().replaceAll("\\s+", ""))).calculate()));
    }

    public String replaceAll(String calculation) {
        StringBuilder builder = new StringBuilder();
        for (char c : calculation.toCharArray()) {
            if(UnicodeCharacters.getNumeralExponents().containsKey(c)){
                builder.append('^').append(UnicodeCharacters.getNumeralExponents().get(c));
            } else {
                switch (c) {
                    case '÷' -> builder.append('/');
                    case ',' -> builder.append('.');
                    case 'x', '×' -> builder.append('*');
                    default -> builder.append(c);
                }
            }
        }
        return builder.toString();
    }
}