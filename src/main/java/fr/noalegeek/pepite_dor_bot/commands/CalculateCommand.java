package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import org.mariuszgromada.math.mxparser.Expression;

public class CalculateCommand extends Command {

    public final static String operators = "+-*/";

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
        boolean beforeNumber = false;
        boolean operator = false;
        boolean afterNumber = false;
        for (char c : calculation.toCharArray()) {
            if(operators.contains(String.valueOf(c))){
                operator = true;
                builder.append(c);
            } else if(UnicodeCharacters.getNumeralExponents().containsKey(c)){
                if(beforeNumber) {
                    beforeNumber = false;
                    builder.append(UnicodeCharacters.getNumeralExponents().get(c));
                } else if(operator){
                    operator = false;
                    builder.append(UnicodeCharacters.getNumeralExponents().get(c));
                } else {
                    builder.append('^').append(UnicodeCharacters.getNumeralExponents().get(c));
                }
                afterNumber = true;
            } else {
                switch (c) {
                    case UnicodeCharacters.leftParenthesisExponent -> {
                        if(operator){
                            builder.append("(");
                            operator = false;
                        }
                        else builder.append("^(");
                        beforeNumber = true;
                    }
                    case UnicodeCharacters.rightParenthesisExponent -> {
                        if(operator) {
                            builder.append(")");
                            operator = false;
                        } else if(afterNumber){
                            builder.append(")");
                            afterNumber = false;
                        } else {
                            builder.append("^)");
                            beforeNumber = true;
                        }
                    }
                    case UnicodeCharacters.plusExponent -> {
                        builder.append("^+");
                        beforeNumber = true;
                        operator = true;
                    }
                    case UnicodeCharacters.minusExponent -> {
                        builder.append("^-");
                        beforeNumber = true;
                        operator = true;
                    }
                    case '₋' -> {
                        builder.append('-');
                        operator = true;
                    }
                    case '₊' -> {
                        builder.append('+');
                        operator = true;
                    }
                    case '÷' -> {
                        builder.append('/');
                        operator = true;
                    }
                    case ',' -> builder.append('.');
                    case 'x', '×' -> {
                        builder.append('*');
                        operator = true;
                    }
                    default -> builder.append(c);
                }
            }
        }
        System.out.println(builder);
        return builder.toString();
    }
}