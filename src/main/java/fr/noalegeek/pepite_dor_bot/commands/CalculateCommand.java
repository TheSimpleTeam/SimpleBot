package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;

public class CalculateCommand extends Command {

    private final String[] operators = {"+", "-", "/", "*"};

    public CalculateCommand() {
        this.name = "Calculate";
        this.aliases = new String[]{"c", "calc"};
        this.arguments = "`+` ou `-` ou `*` ou `/`";
    }

    @Override
    protected void execute(CommandEvent event) {
        String arg = event.getArgs();
        StringBuilder argsBuilder = new StringBuilder();
        String fullArg;
        for (char c : arg.toCharArray()) {
            String character = String.valueOf(c);
            if(!isCharDigit(c)) {
                event.replyError("Le caractère " + c + " est invalide");
                return;
            }
            if(Arrays.asList(operators).contains(character)) {
                if(!Character.isSpaceChar(arg.charAt(arg.indexOf(character) - 1))) {
                    argsBuilder.append(" ").append(c);
                }
                if(!Character.isSpaceChar(arg.charAt(arg.indexOf(character) + 1))) {
                    argsBuilder.append(" ");
                }
            } else {
                argsBuilder.append(c);
            }
        }
        fullArg = argsBuilder.toString();
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            Object result = engine.eval(fullArg);
            event.replySuccess("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est : " + result);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public boolean isCharDigit(char character) {
        if(Arrays.asList(operators).contains(String.valueOf(character)) || character == '.') return true;
        try{
            Double.valueOf(character);
        } catch(NumberFormatException ne) {
            return false;
        }
        return true;
    }
}
