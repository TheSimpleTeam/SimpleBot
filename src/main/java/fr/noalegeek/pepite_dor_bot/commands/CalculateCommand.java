package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;
import java.util.regex.Pattern;

public class CalculateCommand extends Command {

    private final String[] operators = {"+", "-", "/", "*"};

    public CalculateCommand() {
        this.name = "calculate";
        this.aliases = new String[]{"c", "calc"};
        this.arguments = "<premier nombre> <+ ou - ou * ou /> <second nombre>";
        this.cooldown = 5;
        this.help = "Calcule les opérations spécifiés après la commande.";
        this.example = "24*.2 ou 36.7+89-12.102/4";
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] argsArray = event.getMessage().getContentRaw().split(" ");
        if(argsArray.length == 1){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"\nVous pouvez mettre plusieurs nombres après le second en mettant les opérateurs disponibles.");
            return;
        }
        String regex = "\"^[-+](?:[0-9]*[.])?[0-9]+|(?<=\\(|\\/|\\*)[+-](?:[0-9]*[.])?[0-9]+|(?:[0-9]*[.])?[0-9]+|[-+*\\/()]\"g";
        String args = event.getArgs().replaceAll("\\s+", "");
        args = replaceAll(regex, args);
        Expression e = new Expression(args);
        event.replySuccess("Calcul en cours...");
        event.getChannel().sendMessage(MessageHelper.formattedMention(event.getAuthor()) + "Le résultat est : " + e.calculate()).queue();
        Main.LOGGER.info(args);
    }

    public String replaceAll(String regex, String text) {
        Pattern p = Pattern.compile(regex);
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if(Character.isSpaceChar(c) || Arrays.asList(operators).contains(String.valueOf(c)) || p.matcher(String.valueOf(c)).matches() || c == '.') {
                builder.append(c);
            } else if(c == ',') {
                builder.append('.');
            }
        }
        return builder.toString();
    }
}