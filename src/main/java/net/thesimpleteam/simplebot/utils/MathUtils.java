package net.thesimpleteam.simplebot.utils;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MathUtils {

    private MathUtils() {}

    /**
     * @param string a string
     * @return {@code true} if the string is a number, {@code false} otherwise
     */
    public static boolean isNumber(String string){
        return string.replaceAll("\\d+", "").replace(".", "").isEmpty() && StringUtils.countMatches(string, '.') < 2;
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is a number, {@code false} otherwise and send an error embed
     */
    public static boolean isNumber(CommandEvent event, String string){
        if(isNumber(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.notAnNumber", null, null, null, string).build()).build());
        return false;
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is parsable in a long variable, {@code false} otherwise and send an error embed
     */
    public static boolean isParsableLong(CommandEvent event, String string) {
        if(isNumber(event, string) && isParsableLong(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, string, Long.MIN_VALUE, Long.MAX_VALUE).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is parsable in a long variable, {@code false} otherwise
     */
    public static boolean isParsableLong(String string) {
        if(!isNumber(string)) return false;
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is parsable in a double variable, {@code false} otherwise and send an error embed
     */
    public static boolean isParsableDouble(CommandEvent event, String string) {
        if(isNumber(event, string) && isParsableDouble(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, string, Double.MIN_VALUE, Double.MAX_VALUE).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is parsable in a double variable, {@code false} otherwise
     */
    public static boolean isParsableDouble(String string) {
        if(!isNumber(string)) return false;
        try {
            Double.parseDouble(string);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is parsable in a byte variable, {@code false} otherwise and send an error embed
     */
    public static boolean isParsableByte(CommandEvent event, String string) {
        if(isNumber(event, string) && isParsableByte(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, string, Byte.MIN_VALUE, Byte.MAX_VALUE).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is parsable in a byte variable, {@code false} otherwise
     */
    public static boolean isParsableByte(String string) {
        if(!isNumber(string)) return false;
        try {
            Byte.parseByte(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is parsable in a short variable, {@code false} otherwise and send an error embed
     */
    public static boolean isParsableShort(CommandEvent event, String string) {
        if(isNumber(event, string) && isParsableShort(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, string, Short.MIN_VALUE, Short.MAX_VALUE).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is parsable in a short variable, {@code false} otherwise
     */
    public static boolean isParsableShort(String string) {
        if(!isNumber(string)) return false;
        try {
            Short.parseShort(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is parsable in a float variable, {@code false} otherwise and send an error embed
     */
    public static boolean isParsableFloat(CommandEvent event, String string) {
        if(isNumber(event, string) && isParsableFloat(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, string, Float.MIN_VALUE, Float.MAX_VALUE).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is parsable in a float variable, {@code false} otherwise
     */
    public static boolean isParsableFloat(String string) {
        if(!isNumber(string)) return false;
        try {
            Float.parseFloat(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is parsable in an int variable, {@code false} otherwise and send an error embed
     */
    public static boolean isParsableInt(CommandEvent event, String string) {
        if(isNumber(event, string) && isParsableInt(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, string, Integer.MIN_VALUE, Integer.MAX_VALUE).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is parsable in an int variable, {@code false} otherwise
     */
    public static boolean isParsableInt(String string) {
        if(!isNumber(string)) return false;
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param event the event
     * @param string a string
     * @return {@code true} if the string is an integer number, {@code false} otherwise and send an error embed
     */
    public static boolean isIntegerNumberWithEmbed(CommandEvent event, String string) {
        if(isParsableDouble(event, string) && isIntegerNumber(string)) return true;
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.notAnIntegerNumber", null, null, null, string).build()).build());
        return false;
    }

    /**
     * @param string a string
     * @return {@code true} if the string is an integer number, {@code false} otherwise
     */
    public static boolean isIntegerNumber(String string) {
        return isParsableDouble(string) && Math.ceil(Double.parseDouble(string)) == Double.parseDouble(string);
    }

    /**
     * @param number a long number
     * @return {@code true} if the number is prime, i.e. the only two number's divisors are itself and 1 {@code false} otherwise
     */
    public static boolean numberIsPrime(long number) {
        if (number <= 1) return false;
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    /**
     * @param number a long number
     * @return {@code true} if the number is perfect, i.e the sum of the number's divisors is equal to the number {@code false} otherwise
     */
    public static boolean numberIsPerfect(long number) {
        return number > 1 && longListSum(getDivisorsWithoutItself(number)) == number;
    }

    /**
     * @param number a long number
     * @return a long list of the number's divisors without itself
     */
    public static List<Long> getDivisorsWithoutItself(long number){
        List<Long> divisors = new ArrayList<>();
        for(long divisor = 1; divisor <= (int) number / 2; divisor++){
            if(number % divisor == 0) divisors.add(divisor);
        }
        return divisors;
    }

    /**
     * @param number a long number
     * @return a long list of the number's divisors
     */
    public static List<Long> getDivisors(long number){
        List<Long> divisors = getDivisorsWithoutItself(number);
        divisors.add(number);
        return divisors;
    }

    /**
     * @param longList a list of longs numbers
     * @return a long sum of the list's elements
     */
    public static long longListSum(List<Long> longList){
        long longListSum = 0;
        for(long longNumber : longList){
            longListSum += longNumber;
        }
        return longListSum;
    }

}
