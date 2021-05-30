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
        String regex = "^(-?\\\\d+(?:\\\\.\\\\d+)?)([-+*\\\\/])(-?\\\\d+(?:\\\\.\\\\d+)?)$";
        String arg = event.getArgs().replaceAll(" ", "");
        arg = replaceAll(regex, arg);
        Expression e = new Expression(arg);
        event.replySuccess("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est : " + e.calculate());
    }

    public String replaceAll(String regex, String text) {
        Pattern p = Pattern.compile(regex);
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if(Character.isSpaceChar(c) || !p.matcher(String.valueOf(c)).matches()) {
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

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = Math.sin(Math.toRadians(x));
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
