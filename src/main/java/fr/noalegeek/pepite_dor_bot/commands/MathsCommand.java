package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MathsCommand extends Command {

    public MathsCommand() {
        this.category = CommandCategories.FUN.category;
        this.aliases = new String[]{"math", "m"};
        this.name = "maths";
        this.arguments = "arguments.maths";
        this.help = "help.maths";
        this.cooldown = 5;
        this.example = "14";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (event.getArgs().isEmpty() || args.length != 1 && args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        switch (args.length) {
            case 1 -> { //Calculate the specified mathematical expression
                mXparser.disableAlmostIntRounding();
                mXparser.disableCanonicalRounding();
                mXparser.disableUlpRounding();
                for (char c : args[0].toCharArray()) {
                    if (List.of(UnicodeCharacters.getAllExponentsCharacters()).contains(c)) {
                        EmbedBuilder errorExponentsCharactersEmbed = new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.maths.calculate.exponentsCharacters", event));
                        event.reply(new MessageBuilder(errorExponentsCharactersEmbed.build()).build());
                    }
                }
                if (!new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).checkSyntax()) {
                    replyMathematicalSyntaxErrorEmbed(event, args[0]);
                    return;
                }
                EmbedBuilder successEmbed = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.maths.calculate.success", event))
                        .addField(MessageHelper.translateMessage("success.maths.calculate.mathematicalExpression", event), calculateReplaceArgs(args[0].replaceAll("\\s+", "")), false)
                        .addField(MessageHelper.translateMessage("success.maths.calculate.result", event), String.valueOf(new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).calculate()), false);
                event.reply(new MessageBuilder(successEmbed.build()).build());
            }
            case 2 -> {

            }
            case 3 -> {
                switch (args[0]) {
                    case "primenumber", "primeNumber" -> { //Verify if a number is a prime number or make a list of all prime numbers up to the specified number
                        long number;
                        if (isAnNumber(event, args[2]) && isAnIntegerNumber(event, args[2]) && notNumberTooLarge(event, args[2])) number = Long.parseLong(args[2]);//Verify if the arg is a number, else if it's an expression, else is not a valid arg
                        else if (new Expression(args[2]).checkSyntax() && isAnIntegerNumber(event, String.valueOf(new Expression(args[2]).calculate())) && notNumberTooLarge(event, String.valueOf(new Expression(args[2]).calculate()))) number = Long.parseLong(String.valueOf(new Expression(args[2]).calculate()));
                        else {
                            replyMathematicalSyntaxErrorEmbed(event, args[2]);
                            return;
                        }
                        switch (args[1]) {
                            case "number" -> {
                                EmbedBuilder successPrimeNumberNumberEmbed = new EmbedBuilder()
                                        .setTimestamp(Instant.now())
                                        .setColor(numberIsPrime(number) ? Color.GREEN : Color.RED)
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(String.format("%s %s", numberIsPrime(number) ? UnicodeCharacters.whiteHeavyCheckMarkEmoji : UnicodeCharacters.crossMarkEmoji, numberIsPrime(number) ? String.format(MessageHelper.translateMessage("success.maths.primeNumber.isPrime", event), number) : String.format(MessageHelper.translateMessage("success.maths.primeNumber.isNotPrime", event), number)));
                                event.reply(new MessageBuilder(successPrimeNumberNumberEmbed.build()).build());
                            }
                            case "list" -> {
                                //TODO optimize that if possible
                                EmbedBuilder successPrimeNumberListEmbed = new EmbedBuilder()
                                        .setTimestamp(Instant.now())
                                        .setColor(Color.GREEN)
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, String.format(MessageHelper.translateMessage("success.maths.primeNumber.list.success", event), number)));
                                StringBuilder listBuilder = new StringBuilder();
                                List<String> primeNumberList = new ArrayList<>();
                                for (long i = 2; i <= number; i++) {
                                    if (numberIsPrime(i)) primeNumberList.add(String.valueOf(i));
                                }
                                for (String string : primeNumberList) {
                                    listBuilder.append(string).append(", ");
                                }
                                if(listBuilder.toString().length() >= 4096) {
                                    while (listBuilder.toString().length() > 4096 - 3) { //We subtract 3 because we add that string "..." into the listBuilder
                                        primeNumberList.remove(0);
                                        listBuilder = new StringBuilder();
                                        for (String string : primeNumberList) {
                                            listBuilder.append(string).append(", ");
                                        }
                                    }
                                    listBuilder.insert(0, "...");
                                }
                                event.reply(new MessageBuilder(successPrimeNumberListEmbed.setDescription(listBuilder.deleteCharAt(listBuilder.toString().length() - 2)).build()).build());
                            }
                        }
                    }
                    case "perfectnumber", "perfectNumber" -> { //Verify if a number is a perfect number or make a list of all perfect numbers up to the specified number
                        long number;
                        if (isAnNumber(event, args[2]) && isAnIntegerNumber(event, args[2]) && notNumberTooLarge(event, args[2])) number = Long.parseLong(args[2]); //Verify if the arg is a number, else if it's an expression, else is not a valid arg
                        else if (new Expression(args[2]).checkSyntax() && isAnIntegerNumber(event, String.valueOf(new Expression(args[2]).calculate())) && notNumberTooLarge(event, String.valueOf(new Expression(args[2]).calculate()))) number = Long.parseLong(String.valueOf(new Expression(args[2]).calculate()));
                        else {
                            replyMathematicalSyntaxErrorEmbed(event, args[2]);
                            return;
                        }
                        switch (args[1]) {
                            case "number" -> {
                                EmbedBuilder successPerfectNumberNumberEmbed = new EmbedBuilder()
                                        .setTimestamp(Instant.now())
                                        .setColor(numberIsPerfect(number) ? Color.GREEN : Color.RED)
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(String.format("%s %s", numberIsPerfect(number) ? UnicodeCharacters.whiteHeavyCheckMarkEmoji : UnicodeCharacters.crossMarkEmoji, numberIsPerfect(number) ? String.format(MessageHelper.translateMessage("success.maths.perfectNumber.isPerfect", event), number) : String.format(MessageHelper.translateMessage("success.maths.perfectNumber.isNotPerfect", event), number)));
                                event.reply(new MessageBuilder(successPerfectNumberNumberEmbed.build()).build());
                            }
                            case "list" -> {
                                EmbedBuilder successPerfectNumberListEmbed = new EmbedBuilder()
                                        .setTimestamp(Instant.now())
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, String.format(MessageHelper.translateMessage("success.maths.perfectNumber.list.success", event), number)));
                                StringBuilder listBuilder = new StringBuilder();
                                List<String> perfectNumberList = new ArrayList<>();
                                for (long i = 2; i <= number; i++) {
                                    if (numberIsPerfect(i)) perfectNumberList.add(String.valueOf(i));
                                }
                                successPerfectNumberListEmbed
                                        .setColor(perfectNumberList.isEmpty() ? Color.RED : Color.GREEN)
                                        .setTitle(String.format("%s %s", perfectNumberList.isEmpty() ? UnicodeCharacters.crossMarkEmoji : UnicodeCharacters.whiteHeavyCheckMarkEmoji, perfectNumberList.isEmpty() ? String.format(MessageHelper.translateMessage("success.maths.perfectNumber.list.error", event), number) : String.format(MessageHelper.translateMessage("success.maths.perfectNumber.list.success", event), number)));
                                if(perfectNumberList.isEmpty()){
                                    event.reply(new MessageBuilder(successPerfectNumberListEmbed.build()).build());
                                    return;
                                }
                                for (String string : perfectNumberList) {
                                    listBuilder.append(string).append(", ");
                                }
                                if(listBuilder.toString().length() >= 4096) {
                                    while (listBuilder.toString().length() > 4096 - 3) { //We subtract 3 because we add that string "..." into the listBuilder
                                        perfectNumberList.remove(0);
                                        listBuilder = new StringBuilder();
                                        for (String string : perfectNumberList) {
                                            listBuilder.append(string).append(", ");
                                        }
                                    }
                                    listBuilder.insert(0, "...");
                                }
                                event.reply(new MessageBuilder(successPerfectNumberListEmbed.setDescription(listBuilder.deleteCharAt(listBuilder.toString().length() - 2)).build()).build());
                            }
                        }
                    }
                }
            }
        }
    }

    public static String calculateReplaceArgs(String calculation) {
        StringBuilder builder = new StringBuilder();
        for (char c : calculation.toCharArray()) {
            switch (c) {
                case '₋' -> builder.append('-');
                case '₊' -> builder.append('+');
                case '÷' -> builder.append('/');
                case ',' -> builder.append('.');
                case 'x', '×' -> builder.append('*');
                default -> builder.append(c);
            }
        }
        return builder.toString();
    }

    private static void replyMathematicalSyntaxErrorEmbed(CommandEvent event, String args){
        EmbedBuilder errorSyntaxEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.maths.syntax", event), calculateReplaceArgs(args.replaceAll("\\s+", ""))));
        event.reply(new MessageBuilder(errorSyntaxEmbed.build()).build());
    }

    public static boolean isAnNumber(CommandEvent event, String string) {
        if (string.chars().allMatch(Character::isDigit)) return true;
        EmbedBuilder notAnNumberEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.commands.notAnNumber", event), string)));
        event.reply(new MessageBuilder(notAnNumberEmbed.build()).build());
        return false;
    }

    public static boolean notNumberTooLarge(CommandEvent event, String string) {
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException exception) {
            EmbedBuilder numberTooLargeEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.commands.numberTooLarge", event), string)));
            event.reply(new MessageBuilder(numberTooLargeEmbed.build()).build());
            return false;
        }
    }

    public static boolean isAnIntegerNumber(CommandEvent event, String string) {
        if (string.chars().allMatch(Character::isDigit)) return true;
        EmbedBuilder notAnIntegerNumberEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.commands.notAnIntegerNumber", event), string)));
        event.reply(new MessageBuilder(notAnIntegerNumberEmbed.build()).build());
        return false;
    }

    public static boolean numberIsPrime(long number) {
        if (number <= 1) return false;
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    public static boolean numberIsPerfect(long number) {
        if (number <= 1) return false;
        return longListSum(getDivisorsWithoutNumber(number)) == number;
    }

    public static List<Long> getDivisorsWithoutNumber(long number){
        List<Long> divisors = new ArrayList<>();
        for(long divisor = 1; divisor < number; divisor++){
            if(number % divisor == 0) divisors.add(divisor);
        }
        return divisors;
    }

    public static long longListSum(List<Long> longList){
        long longListSum = 0;
        for(long longNumber : longList){
            longListSum += longNumber;
        }
        return longListSum;
    }
}
