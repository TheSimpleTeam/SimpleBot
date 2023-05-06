package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MathUtils;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.thesimpleteam.simplebot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.MessageBuilder;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.IntStream;

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
        StringBuilder unitsList = new StringBuilder();
        if (event.getArgs().isBlank()) {
            MessageHelper.syntaxError(event, this, String.format("information.maths", unitsList));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        switch (args.length) {
            case 1 -> { //Calculate the specified mathematical expression
                mXparser.disableAlmostIntRounding();
                mXparser.disableCanonicalRounding();
                mXparser.disableUlpRounding();
                if(args[1].chars().mapToObj(i -> (char) i).anyMatch(c -> UnicodeCharacters.getAllExponentCharacters().contains(c))) {
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.calculate.exponentsCharacters", null, null, null).build()).build());
                    return;
                }
                if (!new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).checkSyntax()) {
                    if(args[0].replaceAll("\\D+", "").isEmpty()) {
                        MessageHelper.syntaxError(event, this, "information.maths");
                        return;
                    }
                    SimpleBot.LOGGER.log(Level.INFO, new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).getErrorMessage());
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).build()).build());
                    return;
                }
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.calculate.success", null, null, null)
                        .addField(MessageHelper.translateMessage(event, "success.maths.calculate.mathematicalExpression"), calculateReplaceArgs(args[0].replaceAll("\\s+", "")), false)
                        .addField(MessageHelper.translateMessage(event, "success.maths.calculate.result"), String.valueOf(new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).calculate()).replace("E", "x10^"), false)
                        .build()).build());
            }
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "calculate" -> {
                        mXparser.disableAlmostIntRounding();
                        mXparser.disableCanonicalRounding();
                        mXparser.disableUlpRounding();
                        if(args[1].chars().mapToObj(i -> (char) i).anyMatch(c -> UnicodeCharacters.getAllExponentCharacters().contains(c))) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.calculate.exponentsCharacters", null, null, null).build()).build());
                            return;
                        }
                        if (!new Expression(calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).checkSyntax()) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).build()).build());
                            return;
                        }
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.calculate.success", null, null, null)
                                .addField(MessageHelper.translateMessage(event, "success.maths.calculate.mathematicalExpression"), calculateReplaceArgs(args[1].replaceAll("\\s+", "")), false)
                                .addField(MessageHelper.translateMessage(event, "success.maths.calculate.result"), String.valueOf(new Expression(calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).calculate()).replace("E", "x10^"), false)
                                .build()).build());
                    }
                    default -> MessageHelper.syntaxError(event, this, "information.maths");
                }
            }
            case 3 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "primenumber" -> {
                        int number;
                        if (MathUtils.isIntegerNumber(args[2]))
                            number = Integer.parseInt(args[2].split("\\.")[0]);
                        else if (new Expression(args[2]).checkSyntax()){
                            if(MathUtils.isIntegerNumberWithEmbed(event, String.valueOf(new Expression(args[2]).calculate())))
                                number = Integer.parseInt(String.valueOf(new Expression(args[2]).calculate()).split("\\.")[0]);
                            else return;
                        } else {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[2].replaceAll("\\s+", ""))).build()).build());
                            return;
                        }
                        switch (args[1]) {
                            case "number" -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, MathUtils.numberIsPrime(number) ? "success.maths.primeNumber.isPrime" : "error.maths.primeNumber.isNotPrime", null, null, null, number).build()).build());
                            case "list" -> {
                                StringBuilder listBuilder = getListOfPrimes(number);
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.primeNumber.list.success", null, listBuilder.toString(), null, number).build()).build());
                            }
                        }
                    }
                    case "perfectnumber" -> {
                        int number;
                        if (MathUtils.isIntegerNumber(args[2]))
                            number = Integer.parseInt(args[2].split("\\.")[0]);
                        else if (new Expression(args[2]).checkSyntax()){
                            if(MathUtils.isIntegerNumberWithEmbed(event, String.valueOf(new Expression(args[2]).calculate())))
                                number = Integer.parseInt(String.valueOf(new Expression(args[2]).calculate()).split("\\.")[0]);
                            else return;
                        } else {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[2].replaceAll("\\s+", ""))).build()).build());
                            return;
                        }
                        switch (args[1]) {
                            case "number" -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, MathUtils.numberIsPerfect(number) ? "success.maths.perfectNumber.isPerfect" : "error.maths.perfectNumber.isNotPerfect", null, null, null, number).build()).build());
                            case "list" -> {
                                StringBuilder listBuilder = new StringBuilder();
                                List<String> perfectNumberList = new ArrayList<>();
                                for (long i = 2; i <= number; i++) {
                                    if (MathUtils.numberIsPerfect(i)) perfectNumberList.add(String.valueOf(i));
                                }
                                if(perfectNumberList.isEmpty()){
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.primeNumber.list.error", null, null, null, number).build()).build());
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
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.primeNumber.list.success", null, listBuilder.deleteCharAt(listBuilder.toString().length() - 2).toString(), null, number).build()).build());
                            }
                        }
                    }
                    default -> MessageHelper.syntaxError(event, this, "information.maths");
                }
            }
            case 4 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "convert" -> {
                        if(!MathUtils.isParsableDouble(event, args[1].replace(',', '.'))) return;
                        double number = Double.parseDouble(args[1].replace(',', '.'));
                        Unit unit1 = Unit.getByUnitSymbol.get(args[2]), unit2 = Unit.getByUnitSymbol.get(args[3]);
                        if (unit1 == null || unit2 == null || unit1.unitType != unit2.unitType) {
                            String messageKey = (unit1 == null && unit2 == null) ? "unitsDontExist" :
                                    (unit1 == null) ? "firstUnitDontExist" :
                                            (unit2 == null) ? "secondUnitDontExist" :
                                                    "notSameUnitType";
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.convert." + messageKey, null, null, null)).build());
                            return;
                        }
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.convert.success", null, null, null)
                                .addField(MessageHelper.translateMessage(event, "success.maths.convert.from"), args[1] + " " + args[2] + " (" + Unit.getUnitName(event, unit1) + ")", false)
                                .addField(MessageHelper.translateMessage(event, "success.maths.convert.to"), String.valueOf(number * unit1.factor / unit2.factor).replace("E", "x10^") + " " + args[3] + " (" + Unit.getUnitName(event, unit2) + ")", false)
                                .addField(MessageHelper.translateMessage(event, "success.maths.convert.factor"), String.valueOf(unit1.factor / unit2.factor).replace("E", "x10^"), false)
                                .addField(MessageHelper.translateMessage(event, "success.maths.convert.unitType"), MessageHelper.translateMessage(event, unit1.unitType.unitTypeName), true)
                                .build()).build());
                    }
                    default -> MessageHelper.syntaxError(event, this, "information.maths");
                }
            }
            default -> MessageHelper.syntaxError(event, this, "information.maths");
        }
    }

    private static StringBuilder getListOfPrimes(int upToN) {
        StringBuilder builder = new StringBuilder();
        if(upToN >= 6661) { //Abitrary limit since the embed cannot go higher than 4096 chars. You can find this value by finding every prime number and add a space between each integer, it should give you 6659.
            upToN = 6661;
        }
        //This is an implementation of the Sieve of Eratostenes
        boolean[] b = new boolean[upToN + 1];
        b[0] = true;
        b[1] = true;
        for(int i = 2; i < Math.sqrt(upToN); i++) {
            if(!b[i]) {
                for(int j = i*i; j <= upToN; j+= i) b[j] = true;
            }
        }
        IntStream.range(0, b.length).filter(i -> !b[i]).forEach(i -> {
            if(builder.length() + (Math.log10(i) + 1) + 2 >= 4096) { // The + 2 is one char for the space and one char for the … char
                builder.append(" …");
                return;
            }
            if(i != 2) builder.append(" ");
            builder.append(i);
        });
        return builder;
    }

    /**
     * @param calculation the calculation
     * @return the calculation with the replaced characters
     */
    public static String calculateReplaceArgs(String calculation) {
        StringBuilder builder = new StringBuilder();
        for (char c : calculation.toCharArray()) {
            switch (c) {
                case '₋' -> builder.append('-');
                case '₊' -> builder.append('+');
                case '÷' -> builder.append('/');
                case 'x', '×' -> builder.append('*');
                default -> builder.append(c);
            }
        }
        return builder.toString();
    }

    public enum Unit {
        //Units of length
        YOTTAMETER(UnitType.LENGTH, 1.0E24D, "Ym"),
        ZETTAMETER(UnitType.LENGTH, 1.0E21D, "Zm"),
        LIGHTYEAR(UnitType.LENGTH, 9.4607304725808E20D, "ly"),
        EXAMETER(UnitType.LENGTH, 1.0E18D, "Em"),
        PARSEC(UnitType.LENGTH, (96_939_420_213_600_000D/Math.PI), "pc"), //In scientific notation, it's about 3.0856775814913673E16
        PETAMETER(UnitType.LENGTH, 1.0E15D, "Pm"),
        TERAMETER(UnitType.LENGTH, 1.0E12D, "Tm"),
        ASTRONOMICALUNIT(UnitType.LENGTH, 1.495978707E11D, "au"),
        GIGAMETER(UnitType.LENGTH, 1.0E9D, "Gm"),
        MEGAMETER(UnitType.LENGTH, 1.0E6D, "Mm"),
        MYRIAMETER(UnitType.LENGTH, 1.0E4D, "mam"),
        LEAGUE(UnitType.LENGTH, 4.828032E3D, "lea"),
        NAUTICALMILE(UnitType.LENGTH, 1.8520E3D, "NM"),
        MILE(UnitType.LENGTH, 1.609344E3D, "mi"),
        KILOMETER(UnitType.LENGTH, 1.0E3D, "km"),
        CABLE(UnitType.LENGTH, 2.19456E2D, "cb"),
        FURLONG(UnitType.LENGTH, 2.0116840233680466E2D, "fur"),
        HECTOMETER(UnitType.LENGTH, 1.0E2D, "hm"),
        CHAIN(UnitType.LENGTH, 2.0116840233680467E1D, "ch"),
        DECAMETER(UnitType.LENGTH, 1.0E1D, "dam"),
        ROD(UnitType.LENGTH, 5.0292D, "rd"),
        FATHOM(UnitType.LENGTH, 1.8288D, "ftm"),
        ELL(UnitType.LENGTH, 1.143D, "ell"),
        METER(UnitType.LENGTH, 1.0D, "m"),
        YARD(UnitType.LENGTH, 9.144E-1D, "yd"),
        FOOT(UnitType.LENGTH, 3.048E-1D, "ft"),
        SPAN(UnitType.LENGTH, 2.286E-1D, "sp"),
        NATURALSPAN(UnitType.LENGTH, 2.032E-1D, "nasp"),
        LINK(UnitType.LENGTH, 2.0116840233680466E-1D, "li"),
        SHAFTMENT(UnitType.LENGTH, 1.524E-1D, "st"),
        HAND(UnitType.LENGTH, 1.016E-1D, "ha"),
        DECIMETER(UnitType.LENGTH, 1E-1D, "dm"),
        POPPYSEED(UnitType.LENGTH, 8.8194E-2D, "pose"),
        PALM(UnitType.LENGTH, 7.62E-2D, "plm"),
        NAIL(UnitType.LENGTH, 5.715E-2D, "na"),
        INCH(UnitType.LENGTH, 2.54E-2D, "in"),
        FINGER(UnitType.LENGTH, 2.2225E-2D, "fg"),
        DIGIT(UnitType.LENGTH, 1.905E-2D, "dg"),
        CENTIMETER(UnitType.LENGTH, 1E-2D, "cm"),
        BARLEYCORN(UnitType.LENGTH, 8.466666E-3D, "bc"),
        PICA(UnitType.LENGTH, 4.233333E-3D, "pa"),
        LINE(UnitType.LENGTH, 2.116E-3D, "lin"),
        MILLIMETER(UnitType.LENGTH, 1E-3D, "mm"),
        PICAPOINT(UnitType.LENGTH, 3.527778E-4D, "pt"),
        DECIMILLIMETER(UnitType.LENGTH, 1.0E-4D, "dmm"),
        MIL(UnitType.LENGTH, 2.54E-5D, "mil"),
        CENTIMILLIMETER(UnitType.LENGTH, 1.0E-5D, "cmm"),
        MICROMETER(UnitType.LENGTH, 1.0E-6D, "µm"),
        NANOMETER(UnitType.LENGTH, 1.0E-9D, "nm"),
        ANGSTROM(UnitType.LENGTH, 1.0E-10D, "anst"),
        BOHRRADIUS(UnitType.LENGTH, 5.29177210903E-11D, "br"),
        TWIP(UnitType.LENGTH, 1.764E-11D, "tp"),
        PICOMETER(UnitType.LENGTH, 1.0E-12D, "pm"),
        SIEGBAHN(UnitType.LENGTH, 1.0021E-13D, "xu"),
        FEMTOMETER(UnitType.LENGTH, 1.0E-15D, "fm"),
        ATTOMETER(UnitType.LENGTH, 1.0E-18D, "am"),
        ZEPTOMETER(UnitType.LENGTH, 1.0E-21D, "zm"),
        YOCTOMETER(UnitType.LENGTH, 1.0E-24D, "ym"),
        //Units of time
        YOTTASECOND(UnitType.TIME, 1.0E24D, "Ys"),
        ZETTASECOND(UnitType.TIME, 1.0E21D, "Zs"),
        EXASECOND(UnitType.TIME, 1.0E18D, "Es"),
        PETASECOND(UnitType.TIME, 1.0E15D, "Ps"),
        TERASECOND(UnitType.TIME, 1.0E12D, "Ts"),
        MILLENNIUM(UnitType.TIME, 3.15576E10D, "my"),
        CENTURY(UnitType.TIME, 3.15576E9D, "ky"),
        GIGASECOND(UnitType.TIME, 1.0E9D, "Gs"),
        DECADE(UnitType.TIME, 3.15576E8D, "dy"),
        YEAR(UnitType.TIME, 3.15576E7D, "y"),
        MONTH(UnitType.TIME, 2.629800E6D, "M"),
        MEGASECOND(UnitType.TIME, 1.0E6D, "Ms"),
        WEEK(UnitType.TIME, 6.04800E5D, "w"),
        DAY(UnitType.TIME, 8.6400E4D, "d"),
        HOUR(UnitType.TIME, 3.600E3D, "h"),
        KILOSECOND(UnitType.TIME, 1E3D, "ks"),
        HECTOSECOND(UnitType.TIME, 1E2D, "hs"),
        MINUTE(UnitType.TIME, 6E1D, "min"),
        DECASECOND(UnitType.TIME, 1E1D, "das"),
        SECOND(UnitType.TIME, 1.0D, "s"),
        DECISECOND(UnitType.TIME, 1E-1D, "ds"),
        TIERCE(UnitType.TIME, (1D/60D), "t"), // In scientific notation, this is 1.6...E-2D
        CENTISECOND(UnitType.TIME, 0.01D, "cs"),
        MILLISECOND(UnitType.TIME, 0.001D, "ms"),
        MICROSECOND(UnitType.TIME, 1.0E-6D, "µs"),
        NANOSECOND(UnitType.TIME, 1.0E-9D, "ns"),
        PICOSECOND(UnitType.TIME, 1.0E-12D, "ps"),
        FEMTOSECOND(UnitType.TIME, 1.0E-15D, "fs"),
        ATTOSECOND(UnitType.TIME, 1.0E-18D, "as"),
        ZEPTOSECOND(UnitType.TIME, 1.0E-21D, "zs"),
        YOCTOSECOND(UnitType.TIME, 1.0E-24D, "ys");



        public final UnitType unitType;
        public final double factor;
        public final String unitSymbol;
        private static final Map<String, Unit> getByUnitSymbol = new HashMap<>();

        static {
            for (Unit unit : values()) {
                getByUnitSymbol.put(unit.unitSymbol, unit);
            }
        }

        Unit(UnitType unitType, double factor, String unitSymbol) {
            this.unitType = unitType;
            this.factor = factor;
            this.unitSymbol = unitSymbol;
        }

        public static List<String> getAllSymbolsByType(UnitType unitType){
            List<String> symbolsList = new ArrayList<>();
            Arrays.stream(Unit.values()).filter(unit -> unit.unitType == unitType).forEach(unit -> symbolsList.add(unit.name()));
            return symbolsList;
        }

        public static String getUnitName(CommandEvent event, Unit unit){
            return MessageHelper.translateMessage(event, "text.maths.convert.unit." + unit.name().toLowerCase());
        }
    }

    public enum UnitType {
        LENGTH("text.maths.convert.length"),
        TIME("text.maths.convert.time");

        public final String unitTypeName;

        UnitType(String unitTypeName){
            this.unitTypeName = unitTypeName;
        }
    }

    public enum Date {
        y("Years", 31557600, "text.maths.date.year.singular", "text.maths.date.year.plural"),
        M("Months", 2629800, "text.maths.date.month.singular", "text.maths.date.month.plural"),
        w("Weeks", 604800, "text.maths.date.week.singular", "text.maths.date.week.plural"),
        d("Days", 86400, "text.maths.date.day.singular", "text.maths.date.day.plural"),
        h("Hours", 3600, "text.maths.date.hour.singular", "text.maths.date.hour.plural"),
        min("Minutes", 60, "text.maths.date.minute.singular", "text.maths.date.minute.plural"),
        s("Seconds", 1, "text.maths.date.second.singular", "text.maths.date.second.plural");

        public final String functionName;
        public final int factor;
        public final String dateTimeStringSingular;
        public final String dateTimeStringPlural;

        Date(String functionName, int factor, String dateTimeStringSingular, String dateTimeStringPlural) {
            this.functionName = functionName;
            this.factor = factor;
            this.dateTimeStringSingular = dateTimeStringSingular;
            this.dateTimeStringPlural = dateTimeStringPlural;
        }
    }
}
