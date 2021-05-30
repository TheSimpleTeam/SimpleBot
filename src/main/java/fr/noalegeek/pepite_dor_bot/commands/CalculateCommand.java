package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Arrays;
import java.util.regex.Pattern;

public class CalculateCommand extends Command {

    private final String[] operators = {"+", "-", "/", "*"};

    public CalculateCommand() {
        this.name = "Calculate";
        this.aliases = new String[]{"c", "calc"};
        this.arguments = "`+` ou `-` ou `*` ou `/`";
    }

    @Override
    protected void execute(CommandEvent event) {
        String regex = "\"^[-+](?:[0-9]*[.])?[0-9]+|(?<=\\(|\\/|\\*)[+-](?:[0-9]*[.])?[0-9]+|(?:[0-9]*[.])?[0-9]+|[-+*\\/()]\"g";
        String arg = event.getArgs().replaceAll(" ", "");
        arg = replaceAll(regex, arg);
        System.out.println("Calcul : " + arg);
        Expression e = new Expression(arg);
        event.replySuccess("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est : " + e.calculate());
    }

    public String replaceAll(String regex, String text) {
        Pattern p = Pattern.compile(regex);
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if(Character.isSpaceChar(c) || Arrays.asList(operators).contains(String.valueOf(c)) || p.matcher(String.valueOf(c)).matches()) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public boolean isCharDigit(char character) {
        if(Arrays.asList(operators).contains(String.valueOf(character)) || character == '.') return true;
        try{
            Double.valueOf(character);
        } catch(NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
