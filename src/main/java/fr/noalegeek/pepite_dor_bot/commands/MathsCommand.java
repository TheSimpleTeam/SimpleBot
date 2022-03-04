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
import java.util.*;
import java.util.List;

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
        StringBuilder unitsList = new StringBuilder();
        if (args.length < 1) {
            MessageHelper.syntaxError(event, this, String.format("informations.maths", unitsList));
            return;
        }
        switch (args.length) {
            case 1 -> { //Calculate the specified mathematical expression
                mXparser.disableAlmostIntRounding();
                mXparser.disableCanonicalRounding();
                mXparser.disableUlpRounding();
                for (char c : args[0].toCharArray()) {
                    if (Objects.equals(UnicodeCharacters.getAllExponentsCharacters(), c)) {
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.calculate.exponentsCharacters", null, null, null, (Object[]) null).build()).build());
                        return;
                    }
                }
                if (!new Expression(calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).checkSyntax()) {
                    if(args[0].replaceAll("\\D+", "").isEmpty()) {
                        MessageHelper.syntaxError(event, this, "informations.maths");
                        return;
                    }
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[0].replaceAll("\\s+", ""))).build()).build());
                    return;
                }
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.calculate.success", null, null, null, (Object[]) null)
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
                        for (char c : args[1].toCharArray()) {
                            if (Objects.equals(UnicodeCharacters.getAllExponentsCharacters(), c)) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.calculate.exponentsCharacters", null, null, null, (Object[]) null).build()).build());
                            }
                        }
                        if (!new Expression(calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).checkSyntax()) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).build()).build());
                            return;
                        }
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.calculate.success", null, null, null, (Object[]) null)
                                .addField(MessageHelper.translateMessage(event, "success.maths.calculate.mathematicalExpression"), calculateReplaceArgs(args[1].replaceAll("\\s+", "")), false)
                                .addField(MessageHelper.translateMessage(event, "success.maths.calculate.result"), String.valueOf(new Expression(calculateReplaceArgs(args[1].replaceAll("\\s+", ""))).calculate()).replace("E", "x10^"), false)
                                .build()).build());
                    }
                    default -> MessageHelper.syntaxError(event, this, "informations.maths");
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
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[2].replaceAll("\\s+", ""))).build()).build());
                            return;
                        }
                        switch (args[1]) {
                            case "number" -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, numberIsPrime(number) ? "success.maths.primeNumber.isPrime" : "error.maths.primeNumber.isNotPrime", null, null, null, number).build()).build());
                            case "list" -> {
                                //TODO optimize that if possible
                                StringBuilder listBuilder = new StringBuilder();
                                List<String> primeNumberList = new ArrayList<>();
                                for (long i = 2; i <= number; i++) {
                                    if (numberIsPrime(i)) primeNumberList.add(String.valueOf(i));
                                }
                                if(primeNumberList.isEmpty()){
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.primeNumber.list.error", null, null, null, number).build()).build());
                                    return;
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
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.primeNumber.list.success", null, listBuilder.deleteCharAt(listBuilder.toString().length() - 2).toString(), null, number).build()).build());
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
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.syntax", null, null, null, calculateReplaceArgs(args[2].replaceAll("\\s+", ""))).build()).build());
                            return;
                        }
                        switch (args[1]) {
                            case "number" -> event.reply(new MessageBuilder(MessageHelper.getEmbed(event, numberIsPerfect(number) ? "success.maths.perfectNumber.isPerfect" : "error.maths.perfectNumber.isNotPerfect", null, null, null, number).build()).build());
                            case "list" -> {
                                StringBuilder listBuilder = new StringBuilder();
                                List<String> perfectNumberList = new ArrayList<>();
                                for (long i = 2; i <= number; i++) {
                                    if (numberIsPerfect(i)) perfectNumberList.add(String.valueOf(i));
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
                    default -> MessageHelper.syntaxError(event, this, "informations.maths");
                }
            }
            case 4 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "convert" -> {
                        for(int i = 0 ; i < Unit.values().length; i++){
                            for(Unit unit : Unit.values()) {
                                if (Unit.values()[i] != unit && Unit.values()[i].name().equals(unit.name())) {
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.maths.convert.sameSymbols", null, null, null, MessageHelper.translateMessage(event, Unit.values()[i].unitName), MessageHelper.translateMessage(event, unit.unitName), Unit.values()[i].name()).build()).build());
                                    return;
                                }
                            }
                        }
                        try {
                            double number = Double.parseDouble(args[1].replace(',', '.'));
                            Unit unit1 = null;
                            Unit unit2 = null;
                            for (Unit units : Unit.values()) {
                                if (units.name().equals(args[2])) {
                                    unit1 = units;
                                }
                                if (units.name().equals(args[3])) {
                                    unit2 = units;
                                }
                            }
                            if(unit1 == null || unit2 == null || unit1.unitType != unit2.unitType){
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, unit1 == null && unit2 == null ? "error.maths.convert.unitsDontExist" : unit1 == null ? "error.maths.convert.firstUnitDontExist" : unit2 == null ? "error.maths.convert.secondUnitDontExist" : "error.maths.convert.notSameUnitType", null, null, null, (Object[]) null)).build());
                                return;
                            }
                            String factor = String.valueOf(unit1.factor / unit2.factor);
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.maths.convert.success", null, null, null, (Object[]) null)
                                    .addField(MessageHelper.translateMessage(event, "success.maths.convert.from"), args[1] + " " + args[2] + " (" + MessageHelper.translateMessage(event, unit1.unitName) + ")", false)
                                    .addField(MessageHelper.translateMessage(event, "success.maths.convert.to"), String.valueOf(number * Double.parseDouble(factor)).replace("E", "x10^") + " " + args[3] + " (" + MessageHelper.translateMessage(event, unit2.unitName) + ")", false)
                                    .addField(MessageHelper.translateMessage(event, "success.maths.convert.factor"), factor.replace("E", "x10^"), false)
                                    .addField(MessageHelper.translateMessage(event, "success.maths.convert.unitType"), MessageHelper.translateMessage(event, unit1.unitType.unitTypeName), true)
                                    .build()).build());
                        } catch (NumberFormatException ignore) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.notAnNumber", null, null, null, args[1]).build()).build());
                        }
                    }
                    default -> MessageHelper.syntaxError(event, this, "informations.maths");
                }
            }
            default -> MessageHelper.syntaxError(event, this, "informations.maths");
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

    public static boolean notIntegerNumberTooLargeWithEmbed(CommandEvent event, String integerNumber) {
        try {
            Long.parseLong(integerNumber);
            return true;
        } catch (NumberFormatException exception) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, integerNumber).build()).build());
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
        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.notAnIntegerNumber", null, null, null, string).build()).build());
        return false;
    }

    public static boolean isIntegerNumber(String string) {
        return string.chars().allMatch(Character::isDigit);
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

    /**
     * @param specifiedTime the time specified in for example TempbanCommand like 37d or 2086min
     * @return a String explaining in how much years, month, weeks, days, hours, minutes or seconds
     */
    public static String dateTime(String specifiedTime, CommandEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        double time = Arrays.stream(Date.values()).filter(date -> date.name().equals(specifiedTime.replaceAll("\\d+", ""))).findFirst().get().factor * Double.parseDouble(specifiedTime.replaceAll("\\D+", ""));
        int differentUnitsUsed = 0;
        for(Date date : Date.values()){
            if(time / date.factor >= 1){
                differentUnitsUsed++;
                time = Math.floor(((time / date.factor) - Math.floor(time / date.factor)) * date.factor);
            }
        }
        time = Arrays.stream(Date.values()).filter(date -> date.name().equals(specifiedTime.replaceAll("\\d+", ""))).findFirst().get().factor * Double.parseDouble(specifiedTime.replaceAll("\\D+", ""));
        int unitsUsedCount = 0;
        for(Date date : Date.values()) {
            if (time / date.factor >= 1){
                stringBuilder.append((int) Math.floor(time / date.factor)).append(" ").append(time / date.factor >= 2 ? MessageHelper.translateMessage(event, date.dateTimeStringPlural) : MessageHelper.translateMessage(event, date.dateTimeStringSingular));
                if(differentUnitsUsed > 1) {
                    unitsUsedCount++;
                    stringBuilder.append((unitsUsedCount + 1) == differentUnitsUsed ? new StringBuilder().append(MessageHelper.translateMessage(event, "text.maths.date.and")) : (unitsUsedCount + 1) > differentUnitsUsed ? "" : ", ");
                    time = Math.floor(((time / date.factor) - Math.floor(time / date.factor)) * date.factor);
                }
            }
        }
        return stringBuilder.toString();
    }

    public enum Unit {
        //Units of length
        pc(UnitType.LENGTH, 3.0856775814913675E32D, "text.maths.convert.parsec"),
        Ym(UnitType.LENGTH, 1.0E24D, "text.maths.convert.yottameter"),
        Zm(UnitType.LENGTH, 1.0E21D, "text.maths.convert.zettameter"),
        ly(UnitType.LENGTH, 9.4607304725808E20D, "text.maths.convert.lightYear"),
        Em(UnitType.LENGTH, 1.0E18D, "text.maths.convert.exameter"),
        Pm(UnitType.LENGTH, 1.0E15D, "text.maths.convert.petameter"),
        Tm(UnitType.LENGTH, 1.0E12D, "text.maths.convert.terameter"),
        au(UnitType.LENGTH, 1.495978707E11D, "text.maths.convert.astronomicalUnit"),
        Gm(UnitType.LENGTH, 1.0E9D, "text.maths.convert.gigameter"),
        Mm(UnitType.LENGTH, 1000000.0D, "text.maths.convert.megameter"),
        mam(UnitType.LENGTH, 10000.0D, "text.maths.convert.myriameter"),
        lg(UnitType.LENGTH, 4828.032D, "text.maths.convert.league"),
        NM(UnitType.LENGTH, 1852.0D, "text.maths.convert.nauticalMile"),
        mi(UnitType.LENGTH, 1609.344D, "text.maths.convert.mile"),
        km(UnitType.LENGTH, 1000.0D, "text.maths.convert.kilometer"),
        fur(UnitType.LENGTH, 201.16840233680466D, "text.maths.convert.furlong"),
        hm(UnitType.LENGTH, 100.0D, "text.maths.convert.hectometer"),
        ch(UnitType.LENGTH, 20.116840233680467D, "text.maths.convert.chain"),
        dam(UnitType.LENGTH, 10.0D, "text.maths.convert.decameter"),
        ro(UnitType.LENGTH, 5.0292D, "text.maths.convert.rod"),
        fhm(UnitType.LENGTH, 1.8288D, "text.maths.convert.fathom"),
        ell(UnitType.LENGTH, 1.143D, "text.maths.convert.ell"),
        m(UnitType.LENGTH, 1.0D, "text.maths.convert.meter"),
        yd(UnitType.LENGTH, 0.9144D, "text.maths.convert.yard"),
        ft(UnitType.LENGTH, 0.3048D, "text.maths.convert.foot"),
        sp(UnitType.LENGTH, 0.2286D, "text.maths.convert.span"),
        nasp(UnitType.LENGTH, 0.2032D, "text.maths.convert.naturalSpan"),
        lnk(UnitType.LENGTH, 0.20116840233680466D, "text.maths.convert.link"),
        st(UnitType.LENGTH, 0.1524D, "text.maths.convert.shaftment"),
        ha(UnitType.LENGTH, 0.1016D, "text.maths.convert.hand"),
        dm(UnitType.LENGTH, 0.1D, "text.maths.convert.decimeter"),
        pose(UnitType.LENGTH, 0.088194D, "text.maths.convert.poppyseed"),
        plm(UnitType.LENGTH, 0.0762D, "text.maths.convert.palm"),
        na(UnitType.LENGTH, 0.05715D, "text.maths.convert.nail"),
        in(UnitType.LENGTH, 0.0254D, "text.maths.convert.inch"),
        fg(UnitType.LENGTH, 0.022225D, "text.maths.convert.finger"),
        dg(UnitType.LENGTH, 0.01905D, "text.maths.convert.digit"),
        cm(UnitType.LENGTH, 0.01D, "text.maths.convert.centimeter"),
        bc(UnitType.LENGTH, 0.008466666D, "text.maths.convert.barleycorn"),
        pa(UnitType.LENGTH, 0.004233333D, "text.maths.convert.pica"),
        lin(UnitType.LENGTH, 0.002116D, "text.maths.convert.line"),
        mm(UnitType.LENGTH, 0.001D, "text.maths.convert.millimeter"),
        pt(UnitType.LENGTH, 3.527778E-4D, "text.maths.convert.picaPoint"),
        dmm(UnitType.LENGTH, 1.0E-4D, "text.maths.convert.decimillimeter"),
        mil(UnitType.LENGTH, 2.54E-5D, "text.maths.convert.mil"),
        cmm(UnitType.LENGTH, 1.0E-5D, "text.maths.convert.centimillimeter"),
        µm(UnitType.LENGTH, 1.0E-6D, "text.maths.convert.micrometer"),
        nm(UnitType.LENGTH, 1.0E-9D, "text.maths.convert.nanometer"),
        br(UnitType.LENGTH, 5.29177210903E-11D, "text.maths.convert.bohrradius"),
        anst(UnitType.LENGTH, 1.0E-10D, "text.maths.convert.angstrom"),
        pm(UnitType.LENGTH, 1.0E-12D, "text.maths.convert.picometer"),
        tp(UnitType.LENGTH, 1.764E-11D, "text.maths.convert.twip"),
        fm(UnitType.LENGTH, 1.0E-15D, "text.maths.convert.femtometer"),
        xu(UnitType.LENGTH, 1.0021E-13D, "text.maths.convert.siegbahn"),
        am(UnitType.LENGTH, 1.0E-18D, "text.maths.convert.attometer"),
        zm(UnitType.LENGTH, 1.0E-21D, "text.maths.convert.zeptometer"),
        ym(UnitType.LENGTH, 1.0E-24D, "text.maths.convert.yoctometer"),
        //Units of time
        Ys(UnitType.TIME, 1.0E24D, "text.maths.convert.yottasecond"),
        Zs(UnitType.TIME, 1.0E21D, "text.maths.convert.zettasecond"),
        Es(UnitType.TIME, 1.0E18D, "text.maths.convert.exasecond"),
        Ps(UnitType.TIME, 1.0E15D, "text.maths.convert.petasecond"),
        Ts(UnitType.TIME, 1.0E12D, "text.maths.convert.terasecond"),
        my(UnitType.TIME, 3.15576E10D, "text.maths.convert.millennium"),
        ky(UnitType.TIME, 3.15576E9D, "text.maths.convert.century"),
        Gs(UnitType.TIME, 1.0E9D, "text.maths.convert.gigasecond"),
        dy(UnitType.TIME, 3.15576E8D, "text.maths.convert.decade"),
        y(UnitType.TIME, 3.15576E7D, "text.maths.convert.year"),
        M(UnitType.TIME, 2629800.0D, "text.maths.convert.month"),
        Ms(UnitType.TIME, 1000000.0D, "text.maths.convert.megasecond"),
        w(UnitType.TIME, 604800.0D, "text.maths.convert.week"),
        d(UnitType.TIME, 86400.0D, "text.maths.convert.day"),
        h(UnitType.TIME, 3600.0D, "text.maths.convert.hour"),
        ks(UnitType.TIME, 1000.0D, "text.maths.convert.kilosecond"),
        hs(UnitType.TIME, 100.0D, "text.maths.convert.hectosecond"),
        min(UnitType.TIME, 60.0D, "text.maths.convert.minute"),
        das(UnitType.TIME, 10.0D, "text.maths.convert.decasecond"),
        s(UnitType.TIME, 1.0D, "text.maths.convert.second"),
        ds(UnitType.TIME, 0.1D, "text.maths.convert.decisecond"),
        t(UnitType.TIME, 0.01666666666D, "text.maths.convert.tierce"),
        cs(UnitType.TIME, 0.01D, "text.maths.convert.centisecond"),
        ms(UnitType.TIME, 0.001D, "text.maths.convert.millisecond"),
        µs(UnitType.TIME, 1.0E-6D, "text.maths.convert.microsecond"),
        ns(UnitType.TIME, 1.0E-9D, "text.maths.convert.nanosecond"),
        ps(UnitType.TIME, 1.0E-12D, "text.maths.convert.picosecond"),
        fs(UnitType.TIME, 1.0E-15D, "text.maths.convert.femtosecond"),
        as(UnitType.TIME, 1.0E-18D, "text.maths.convert.attosecond"),
        zs(UnitType.TIME, 1.0E-21D, "text.maths.convert.zeptosecond"),
        ys(UnitType.TIME, 1.0E-24D, "text.maths.convert.yoctosecond");

        public final UnitType unitType;
        public final double factor;
        public final String unitName;

        Unit(UnitType unitType, double factor, String unitName) {
            this.unitType = unitType;
            this.factor = factor;
            this.unitName = unitName;
        }

        public static List<String> getAllSymbolsByType(UnitType unitType){
            List<String> symbolsList = new ArrayList<>();
            Arrays.stream(Unit.values()).filter(unit -> unit.unitType == unitType).forEach(unit -> symbolsList.add(unit.name()));
            return symbolsList;
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
