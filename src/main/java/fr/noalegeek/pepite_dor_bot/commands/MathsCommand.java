package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        if (event.getArgs().isEmpty() || args.length != 1 && args.length != 2 && args.length != 3 && args.length != 4) {
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
                        .addField(MessageHelper.translateMessage("success.maths.calculate.result", event), String.valueOf(new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).calculate()).replace("E", "x10^"), false);
                event.reply(new MessageBuilder(successEmbed.build()).build());
            }
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "calculate" -> {
                        mXparser.disableAlmostIntRounding();
                        mXparser.disableCanonicalRounding();
                        mXparser.disableUlpRounding();
                        for (char c : args[1].toCharArray()) {
                            if (List.of(UnicodeCharacters.getAllExponentsCharacters()).contains(c)) {
                                EmbedBuilder errorExponentsCharactersEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.maths.calculate.exponentsCharacters", event));
                                event.reply(new MessageBuilder(errorExponentsCharactersEmbed.build()).build());
                            }
                        }
                        if (!new Expression(calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).checkSyntax()) {
                            replyMathematicalSyntaxErrorEmbed(event, args[1]);
                            return;
                        }
                        EmbedBuilder successEmbed = new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.maths.calculate.success", event))
                                .addField(MessageHelper.translateMessage("success.maths.calculate.mathematicalExpression", event), calculateReplaceArgs(args[1].replaceAll("\\s+", "")), false)
                                .addField(MessageHelper.translateMessage("success.maths.calculate.result", event), String.valueOf(new Expression(calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).calculate()).replace("E", "x10^"), false);
                        event.reply(new MessageBuilder(successEmbed.build()).build());
                    }
                }
            }
            case 3 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "primenumber" -> { //Verify if a number is a prime number or make a list of all prime numbers up to the specified number
                        long number;
                        if (isIntegerNumber(args[2])) { //Verify if the arg is a number, else if it's an expression, else is not a valid arg. We need to overlay these if because all these boolean functions (isAnNumber, isAnIntegerNumber, notNumberTooLarge) return an embed error if any of these boolean functions return false.
                            if (notIntegerNumberTooLargeWithEmbed(event, args[2])) number = Long.parseLong(args[2]);
                            else return;
                        } else if (new Expression(args[2]).checkSyntax()){
                            if(isIntegerNumberWithEmbed(event, String.valueOf(new Expression(args[2]).calculate()))){
                                if(notIntegerNumberTooLargeWithEmbed(event, String.valueOf(new Expression(args[2]).calculate()))){
                                    number = Long.parseLong(String.valueOf(new Expression(args[2]).calculate()));
                                } else return;
                            } else return;
                        } else {
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
                    case "perfectnumber" -> { //Verify if a number is a perfect number or make a list of all perfect numbers up to the specified number
                        long number;
                        if (isIntegerNumber(args[2])) { //Verify if the arg is a number, else if it's an expression, else is not a valid arg. We need to overlay these if because all these boolean functions (isAnNumber, isAnIntegerNumber, notNumberTooLarge) return an embed error if any of these boolean functions return false.
                            if (notIntegerNumberTooLargeWithEmbed(event, args[2])) number = Long.parseLong(args[2]);
                            else return;
                        } else if (new Expression(args[2]).checkSyntax()){
                            if(isIntegerNumberWithEmbed(event, String.valueOf(new Expression(args[2]).calculate()))){
                                if(notIntegerNumberTooLargeWithEmbed(event, String.valueOf(new Expression(args[2]).calculate()))){
                                    number = Long.parseLong(String.valueOf(new Expression(args[2]).calculate()));
                                } else return;
                            } else return;
                        } else {
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
            case 4 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "convert" -> {
                        for(int i = 0 ; i < Unit.values().length; i++){
                            for(Unit unit : Unit.values()) {
                                if (Unit.values()[i].symbol.equals(unit.symbol) && Unit.values()[i] != unit) {
                                    EmbedBuilder errorSameSymbolsEmbed = new EmbedBuilder()
                                            .setColor(Color.RED)
                                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                            .setTimestamp(Instant.now())
                                            .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.maths.convert.sameSymbols", event), MessageHelper.translateMessage(Unit.values()[i].unitName, event), MessageHelper.translateMessage(unit.unitName, event), Unit.values()[i].symbol));
                                    event.reply(new MessageBuilder(errorSameSymbolsEmbed.build()).build());
                                    return;
                                }
                            }
                        }
                        try {
                            double number = Double.parseDouble(args[1].replace(',', '.'));
                            Unit unit1 = null;
                            Unit unit2 = null;
                            for (Unit units : Unit.values()) {
                                if (units.symbol.equals(args[2])) {
                                    unit1 = units;
                                }
                                if (units.symbol.equals(args[3])) {
                                    unit2 = units;
                                }
                            }
                            if(unit1 == null || unit2 == null || unit1.unitType != unit2.unitType) {
                                EmbedBuilder errorUnitEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now());
                                if (unit1 == null && unit2 == null) { //Verify if the specified units don't exist
                                    event.reply(new MessageBuilder(errorUnitEmbed.setTitle(MessageHelper.translateMessage("error.maths.convert.unitsDontExist", event)).build()).build());
                                    return;
                                } else if (unit1 == null) { //Verify if the first specified unit don't exist
                                    event.reply(new MessageBuilder(errorUnitEmbed.setTitle(MessageHelper.translateMessage("error.maths.convert.firstUnitDontExist", event)).build()).build());
                                    return;
                                } else if (unit2 == null) { //Verify if the second specified unit don't exist
                                    event.reply(new MessageBuilder(errorUnitEmbed.setTitle(MessageHelper.translateMessage("error.maths.convert.secondUnitDontExist", event)).build()).build());
                                    return;
                                } else { //Verify if the unit types specified unit aren't equals
                                    event.reply(new MessageBuilder(errorUnitEmbed.setTitle(MessageHelper.translateMessage("error.maths.convert.notSameUnitType", event)).build()).build());
                                    return;
                                }
                            }
                            String factor = String.valueOf(unit1.factor / unit2.factor);
                            EmbedBuilder successEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.maths.maths.convert.success", event)))
                                    .addField(MessageHelper.translateMessage("success.maths.convert.from", event), args[1] + " " + args[2] + " (" + MessageHelper.translateMessage(unit1.unitName, event) + ")", false)
                                    .addField(MessageHelper.translateMessage("success.maths.convert.to", event), String.valueOf(number * Double.parseDouble(factor)).replace("E", "x10^") + " " + args[3] + " (" + MessageHelper.translateMessage(unit2.unitName, event) + ")", false)
                                    .addField(MessageHelper.translateMessage("success.maths.convert.factor", event), factor.replace("E", "x10^"), false)
                                    .addField(MessageHelper.translateMessage("success.maths.convert.unitType", event), MessageHelper.translateMessage(unit1.unitType.unitTypeName, event), true)
                                    .setTimestamp(Instant.now())
                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl());
                            event.reply(new MessageBuilder(successEmbed.build()).build());
                        } catch (NumberFormatException ignore) {
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.notAnNumber", event));
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

    public static boolean notIntegerNumberTooLargeWithEmbed(CommandEvent event, String integerNumber) {
        try {
            Long.parseLong(integerNumber);
            return true;
        } catch (NumberFormatException exception) {
            EmbedBuilder numberTooLargeEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.commands.numberTooLarge", event), integerNumber)));
            event.reply(new MessageBuilder(numberTooLargeEmbed.build()).build());
            return false;
        }
    }

    public static boolean notIntegerNumberTooLarge(String integerNumber) {
        try {
            Long.parseLong(integerNumber);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean isIntegerNumberWithEmbed(CommandEvent event, String string) {
        if (string.chars().allMatch(Character::isDigit)) return true;
        EmbedBuilder notAnIntegerNumberEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.commands.notAnIntegerNumber", event), string)));
        event.reply(new MessageBuilder(notAnIntegerNumberEmbed.build()).build());
        return false;
    }

    public static boolean isIntegerNumber(String string) {
        if (string.chars().allMatch(Character::isDigit)) return true;
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

    private enum Unit {
        //Units of length
        PARSEC(UnitType.LENGTH, 308567758149136760000000000000000D, "pc", "text.maths.convert.parsec"),
        YOTTAMETER(UnitType.LENGTH, 1000000000000000000000000D, "Ym", "text.maths.convert.yottameter"),
        ZETTAMETER(UnitType.LENGTH, 1000000000000000000000D, "Zm", "text.maths.convert.zettameter"),
        LIGHTYEAR(UnitType.LENGTH, 946073047258080000000D, "ly", "text.maths.convert.lightYear"),
        EXAMETER(UnitType.LENGTH, 1000000000000000000D, "Em", "text.maths.convert.exameter"),
        PETAMETER(UnitType.LENGTH, 1000000000000000D, "Pm", "text.maths.convert.petameter"),
        TELAMETER(UnitType.LENGTH, 1000000000000D, "Tm", "text.maths.convert.telameter"),
        ASTRONOMICALUNIT(UnitType.LENGTH, 149597870700D, "au", "text.maths.convert.astronomicalUnit"),
        GIGAMETER(UnitType.LENGTH, 1000000000D, "Gm", "text.maths.convert.gigameter"),
        MEGAMETER(UnitType.LENGTH, 1000000D, "Mm", "text.maths.convert.megameter"),
        MYRIAMETER(UnitType.LENGTH, 10000D, "mam", "text.maths.convert.myriameter"),
        LEAGUE(UnitType.LENGTH, 4828.032D, "lg", "text.maths.convert.league"),
        NAUTICALMILE(UnitType.LENGTH, 1852D, "NM", "text.maths.convert.nauticalMile"),
        MILE(UnitType.LENGTH, 1609.344D, "mi", "text.maths.convert.mile"),
        KILOMETER(UnitType.LENGTH, 1000D, "km", "text.maths.convert.kilometer"),
        FURLONG(UnitType.LENGTH, 201.16840233680466D, "fur", "text.maths.convert.furlong"),
        HECTOMETER(UnitType.LENGTH, 100D, "hm", "text.maths.convert.hectometer"),
        CHAIN(UnitType.LENGTH, 20.116840233680466D, "ch", "text.maths.convert.chain"),
        DECAMETER(UnitType.LENGTH, 10D, "dam", "text.maths.convert.decameter"),
        ROD(UnitType.LENGTH, 5.0292D, "ro", "text.maths.convert.rod"),
        FATHOM(UnitType.LENGTH, 1.8288D, "fhm", "text.maths.convert.fathom"),
        ELL(UnitType.LENGTH, 1.143, "ell", "text.maths.convert.ell"),
        METER(UnitType.LENGTH, 1D, "m", "text.maths.convert.meter"),
        YARD(UnitType.LENGTH, 0.9144D, "yd", "text.maths.convert.yard"),
        FOOT(UnitType.LENGTH, 0.3048D, "ft", "text.maths.convert.foot"),
        SPAN(UnitType.LENGTH, 0.2286D, "sp", "text.maths.convert.span"),
        NATURALSPAN(UnitType.LENGTH, 0.2032D, "nasp", "text.maths.convert.naturalSpan"),
        LINK(UnitType.LENGTH, 0.20116840233680466D, "lnk", "text.maths.convert.link"),
        SHAFTMENT(UnitType.LENGTH, 0.1524D, "st", "text.maths.convert.shaftment"),
        HAND(UnitType.LENGTH, 0.1016D, "ha", "text.maths.convert.hand"),
        DECIMETER(UnitType.LENGTH, 0.1D, "dm", "text.maths.convert.decimeter"),
        POPPYSEED(UnitType.LENGTH, 0.088194D, "pose", "text.maths.convert.poppyseed"),
        PALM(UnitType.LENGTH, 0.0762D, "plm", "text.maths.convert.palm"),
        NAIL(UnitType.LENGTH,  0.05715D, "na", "text.maths.convert.nail"),
        INCH(UnitType.LENGTH, 0.0254D, "in", "text.maths.convert.inch"),
        FINGER(UnitType.LENGTH, 0.022225D, "fg", "text.maths.convert.finger"),
        DIGIT(UnitType.LENGTH, 0.01905D, "dg", "text.maths.convert.digit"),
        CENTIMETER(UnitType.LENGTH, 0.01D, "cm", "text.maths.convert.centimeter"),
        BARLEYCORN(UnitType.LENGTH, 0.008466666D, "bc", "text.maths.convert.barleycorn"),
        PICA(UnitType.LENGTH, 0.004233333D, "pa", "text.maths.convert.pica"),
        LINE(UnitType.LENGTH, 0.002116D, "lin", "text.maths.convert.line"),
        MILLIMETER(UnitType.LENGTH, 0.001D, "mm", "text.maths.convert.millimeter"),
        PICAPOINT(UnitType.LENGTH, 0.0003527778D, "pt", "text.maths.convert.picaPoint"),
        DECIMILLIMETER(UnitType.LENGTH, 0.0001D, "dmm", "text.maths.convert.decimillimeter"),
        MIL(UnitType.LENGTH, 0.0000254D, "mil", "text.maths.convert.mil"),
        CENTIMILLIMETER(UnitType.LENGTH, 0.00001D, "cmm", "text.maths.convert.centimillimeter"),
        MICROMETER(UnitType.LENGTH, 0.000001D, "µm", "text.maths.convert.micrometer"),
        NANOMETER(UnitType.LENGTH, 0.000000001D, "nm", "text.maths.convert.nanometer"),
        BOHRRADIUS(UnitType.LENGTH, 0.0000000000529177210903D, "br", "text.maths.convert.bohrradius"),
        ANGSTROM(UnitType.LENGTH, 0.0000000001D, "anst", "text.maths.convert.angstrom"),
        PICOMETER(UnitType.LENGTH, 0.000000000001D, "pm", "text.maths.convert.picometer"),
        TWIP(UnitType.LENGTH, 0.00000000001764D, "tp", "text.maths.convert.twip"),
        FEMTOMETER(UnitType.LENGTH, 0.000000000000001D, "fm", "text.maths.convert.femtometer"),
        SIEGBAHN(UnitType.LENGTH, 0.00000000000010021D, "xu", "text.maths.convert.siegbahn"),
        ATTOMETER(UnitType.LENGTH, 0.000000000000000001D, "am", "text.maths.convert.attometer"),
        ZEPTOMETER(UnitType.LENGTH, 0.000000000000000000001D, "zm", "text.maths.convert.zeptometer"),
        YOCTOMETER(UnitType.LENGTH, 0.000000000000000000000001D, "ym", "text.maths.convert.yoctometer"),
        //Units of time
        YOTTASECOND(UnitType.TIME, 1000000000000000000000000D, "Ys", "text.maths.convert.yottasecond"),
        ZETTASECOND(UnitType.TIME, 1000000000000000000000D, "Zs", "text.maths.convert.zettasecond"),
        EXASECOND(UnitType.TIME, 1000000000000000000D, "Es", "text.maths.convert.exasecond"),
        PETASECOND(UnitType.TIME, 1000000000000000D, "Ps", "text.maths.convert.petasecond"),
        TERASECOND(UnitType.TIME, 1000000000000D, "Ts", "text.maths.convert.terasecond"),
        MILLENNIUM(UnitType.TIME, 31557600000D, "my", "text.maths.convert.millennium"),
        CENTURY(UnitType.TIME, 3155760000D, "ky", "text.maths.convert.century"),
        GIGASECOND(UnitType.TIME, 1000000000D, "Gs", "text.maths.convert.gigasecond"),
        DECADE(UnitType.TIME, 315576000D, "dy", "text.maths.convert.decade"),
        YEAR(UnitType.TIME, 31557600D, "y", "text.maths.convert.year"),
        MONTH(UnitType.TIME, 2629800D, "M", "text.maths.convert.month"),
        MEGASECOND(UnitType.TIME, 1000000D, "Ms", "text.maths.convert.megasecond"),
        WEEK(UnitType.TIME, 604800D, "w", "text.maths.convert.week"),
        DAY(UnitType.TIME, 86400D, "d", "text.maths.convert.day"),
        HOUR(UnitType.TIME, 3600D, "h", "text.maths.convert.hour"),
        KILOSECOND(UnitType.TIME, 1000D, "ks", "text.maths.convert.kilosecond"),
        HECTOSECOND(UnitType.TIME, 100D, "hs", "text.maths.convert.hectosecond"),
        MINUTE(UnitType.TIME, 60D, "min", "text.maths.convert.minute"),
        DECASECOND(UnitType.TIME, 10D, "das", "text.maths.convert.decasecond"),
        SECOND(UnitType.TIME, 1D, "s", "text.maths.convert.second"),
        DECISECOND(UnitType.TIME, 0.1D, "ds", "text.maths.convert.decisecond"),
        TIERCE(UnitType.TIME, 0.01666666666D, "t", "text.maths.convert.tierce"),
        CENTISECOND(UnitType.TIME, 0.01D, "cs", "text.maths.convert.centisecond"),
        MILLISECOND(UnitType.TIME, 0.001D, "ms", "text.maths.convert.millisecond"),
        MICROSECOND(UnitType.TIME, 0.000001D,"µs", "text.maths.convert.microsecond"),
        NANOSECOND(UnitType.TIME, 0.000000001D, "ns", "text.maths.convert.nanosecond"),
        PICOSECOND(UnitType.TIME, 0.000000000001D, "ps", "text.maths.convert.picosecond"),
        FEMTOSECOND(UnitType.TIME, 0.000000000000001D, "fs", "text.maths.convert.femtosecond"),
        ATTOSECONDE(UnitType.TIME, 0.000000000000000001D, "as","text.maths.convert.attosecond"),
        ZEPTOSECOND(UnitType.TIME, 0.000000000000000000001D, "zs", "text.maths.convert.zeptosecond"),
        YOCTOSECOND(UnitType.TIME, 0.000000000000000000000001D, "ys", "text.maths.convert.yoctosecond");

        private final UnitType unitType;
        private final double factor;
        private final String symbol;
        private final String unitName;

        Unit(UnitType unitType, double factor, String symbol, String unitName) {
            this.unitType = unitType;
            this.factor = factor;
            this.symbol = symbol;
            this.unitName = unitName;
        }
    }

    private enum UnitType {
        LENGTH("text.maths.convert.length"),
        TIME("text.maths.convert.time");

        private final String unitTypeName;

        UnitType(String unitTypeName){
            this.unitTypeName = unitTypeName;
        }
    }
}
