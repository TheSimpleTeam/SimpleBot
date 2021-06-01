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
        this.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent event) {
        String regex = "\"^[-+](?:[0-9]*[.])?[0-9]+|(?<=\\(|\\/|\\*)[+-](?:[0-9]*[.])?[0-9]+|(?:[0-9]*[.])?[0-9]+|[-+*\\/()]\"g";
        String args = event.getArgs().replaceAll(" ", "");
        args = replaceAll(regex, args);
        Expression e = new Expression(args);
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
}