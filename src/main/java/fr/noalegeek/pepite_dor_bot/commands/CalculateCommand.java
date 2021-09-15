package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import org.mariuszgromada.math.mxparser.Expression;

public class CalculateCommand extends Command {

    public CalculateCommand() {
        this.name = "calculate";
        this.aliases = new String[]{"c", "calc", "ca", "cal", "calcul", "calcu", "calcula", "calculat"};
        this.arguments = "syntax.calculate";
        this.cooldown = 5;
        this.help = "help.calculate";
        this.example = "example.calculate";
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getArgs().split("\\s+").length == 0) {
            event.replyError(MessageHelper.syntaxError(event, this));
            return;
        }
        if(!new Expression(replaceAll(event.getArgs().replaceAll("\\s+", ""))).checkSyntax()){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.calculate", event), event.getArgs().replaceAll("\\s+", "")));
            return;
        }
        event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.calculate", event), replaceAll(event.getArgs().replaceAll("\\s+", "")), new Expression(replaceAll(event.getArgs().replaceAll("\\s+", ""))).calculate()));
    }

    public String replaceAll(String calculation) {
        StringBuilder builder = new StringBuilder();
        for (char c : calculation.toCharArray()) {
            switch (c) {
                case ',':
                    builder.append('.');
                    break;
                case 'x':
                    builder.append('*');
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }
}