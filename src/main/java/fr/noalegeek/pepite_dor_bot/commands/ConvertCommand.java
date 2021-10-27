package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.time.Instant;

public class ConvertCommand extends Command {

    public ConvertCommand() {
        this.name = "convert";
        this.aliases = new String[]{"co"};
        this.arguments = "arguments.convert";
        this.help = "help.convert";
        this.example = "10 m cm";
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        for(int i = 0 ; i < Unit.values().length; i++){
            for(Unit unit : Unit.values()) {
                if (Unit.values()[i].symbol.equals(unit.symbol) && Unit.values()[i] != unit) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("text.sendError", event) + String.format(MessageHelper.translateMessage("error.convert.sameSymbols", event), MessageHelper.translateMessage(Unit.values()[i].unitName, event), MessageHelper.translateMessage(unit.unitName, event), Unit.values()[i].symbol));
                    return;
                }
            }
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 3) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        try {
            double number = Double.parseDouble(args[0].replace(',', '.'));
            Unit unit1 = null;
            Unit unit2 = null;
            for (Unit units : Unit.values()) {
                if (units.symbol.equals(args[1])) {
                    unit1 = units;
                }
                if (units.symbol.equals(args[2])) {
                    unit2 = units;
                }
            }
            if (unit1 == null && unit2 == null) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.unitsDontExist", event));
                return;
            } else if (unit1 == null) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.firstUnitDontExist", event));
                return;
            } else if (unit2 == null) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.secondUnitDontExist", event));
                return;
            }
            if(unit1.unitType != unit2.unitType){
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.notSameUnitType", event));
                return;
            }
            String factor = String.valueOf(unit1.factor / unit2.factor);
            EmbedBuilder successEmbed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.convert.success", event))
                    .addField(MessageHelper.translateMessage("success.convert.from", event), args[0] + " " + args[1] + " (" + MessageHelper.translateMessage(unit1.unitName, event) + ")", false)
                    .addField(MessageHelper.translateMessage("success.convert.to", event), String.valueOf(number * Double.parseDouble(factor)).replace("E", "x10^") + " " + args[2] + " (" + MessageHelper.translateMessage(unit2.unitName, event) + ")", false)
                    .addField(MessageHelper.translateMessage("success.convert.factor", event), factor.replace("E", "x10^"), false)
                    .addField(MessageHelper.translateMessage("success.convert.unitType", event), MessageHelper.translateMessage(unit1.unitType.unitTypeName, event), true)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl());
            event.reply(new MessageBuilder(successEmbed.build()).build());
        } catch (NumberFormatException ignore) {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.notAnNumber", event));
        }
    }

    private enum Unit {
        //Units of length
        PARSEC(UnitType.LENGTH, 308567758149136760000000000000000D, "pc", "text.convert.parsec"),
        YOTTAMETER(UnitType.LENGTH, 1000000000000000000000000D, "Ym", "text.convert.yottameter"),
        ZETTAMETER(UnitType.LENGTH, 1000000000000000000000D, "Zm", "text.convert.zettameter"),
        LIGHTYEAR(UnitType.LENGTH, 946073000000000000000D, "ly", "text.convert.lightYear"),
        EXAMETER(UnitType.LENGTH, 1000000000000000000D, "Em", "text.convert.exameter"),
        PETAMETER(UnitType.LENGTH, 1000000000000000D, "Pm", "text.convert.petameter"),
        TELAMETER(UnitType.LENGTH, 1000000000000D, "Tm", "text.convert.telameter"),
        ASTRONOMICALUNIT(UnitType.LENGTH, 149597870700D, "au", "text.convert.astronomicalUnit"),
        GIGAMETER(UnitType.LENGTH, 1000000000D, "Gm", "text.convert.gigameter"),
        MEGAMETER(UnitType.LENGTH, 1000000D, "Mm", "text.convert.megameter"),
        MYRIAMETER(UnitType.LENGTH, 10000D, "mam", "text.convert.myriameter"),
        LEAGUE(UnitType.LENGTH, 4828.032D, "lg", "text.convert.league"),
        NAUTICALMILE(UnitType.LENGTH, 1852D, "NM", "text.convert.nauticalMile"),
        MILE(UnitType.LENGTH, 1609.344D, "mi", "text.convert.mile"),
        KILOMETER(UnitType.LENGTH, 1000D, "km", "text.convert.kilometer"),
        FURLONG(UnitType.LENGTH, 201.16840233680466D, "fur", "text.convert.furlong"),
        HECTOMETER(UnitType.LENGTH, 100D, "hm", "text.convert.hectometer"),
        CHAIN(UnitType.LENGTH, 20.116840233680466D, "ch", "text.convert.chain"),
        DECAMETER(UnitType.LENGTH, 10D, "dam", "text.convert.decameter"),
        ROD(UnitType.LENGTH, 5.0292D, "ro", "text.convert.rod"),
        FATHOM(UnitType.LENGTH, 1.8288D, "fhm", "text.convert.fathom"),
        ELL(UnitType.LENGTH, 1.143, "ell", "text.convert.ell"),
        METER(UnitType.LENGTH, 1.0D, "m", "text.convert.meter"),
        YARD(UnitType.LENGTH, 0.9144D, "yd", "text.convert.yard"),
        FOOT(UnitType.LENGTH, 0.3048D, "ft", "text.convert.foot"),
        SPAN(UnitType.LENGTH, 0.2286D, "sp", "text.convert.span"),
        NATURALSPAN(UnitType.LENGTH, 0.2032D, "nasp", "text.convert.naturalSpan"),
        LINK(UnitType.LENGTH, 0.20116840233680466D, "lnk", "text.convert.link"),
        SHAFTMENT(UnitType.LENGTH, 0.1524D, "st", "text.convert.shaftment"),
        HAND(UnitType.LENGTH, 0.1016D, "ha", "text.convert.hand"),
        DECIMETER(UnitType.LENGTH, 0.1D, "dm", "text.convert.decimeter"),
        POPPYSEED(UnitType.LENGTH, 0.088194D, "pose", "text.convert.poppyseed"),
        PALM(UnitType.LENGTH, 0.0762D, "plm", "text.convert.palm"),
        NAIL(UnitType.LENGTH,  0.05715D, "na", "text.convert.nail"),
        INCH(UnitType.LENGTH, 0.0254D, "in", "text.convert.inch"),
        FINGER(UnitType.LENGTH, 0.022225D, "fg", "text.convert.finger"),
        DIGIT(UnitType.LENGTH, 0.01905D, "dg", "text.convert.digit"),
        CENTIMETER(UnitType.LENGTH, 0.01D, "cm", "text.convert.centimeter"),
        BARLEYCORN(UnitType.LENGTH, 0.008466666D, "bc", "text.convert.barleycorn"),
        PICA(UnitType.LENGTH, 0.004233333D, "pa", "text.convert.pica"),
        LINE(UnitType.LENGTH, 0.002116D, "lin", "text.convert.line"),
        MILLIMETER(UnitType.LENGTH, 0.001D, "mm", "text.convert.millimeter"),
        PICAPOINT(UnitType.LENGTH, 0.0003527778D, "pt", "text.convert.picaPoint"),
        DECIMILLIMETER(UnitType.LENGTH, 0.0001D, "dmm", "text.convert.decimillimeter"),
        MIL(UnitType.LENGTH, 0.0000254D, "mil", "text.convert.mil"),
        CENTIMILLIMETER(UnitType.LENGTH, 0.00001D, "cmm", "text.convert.centimillimeter"),
        MICROMETER(UnitType.LENGTH, 0.000001D, "µm", "text.convert.micrometer"),
        NANOMETER(UnitType.LENGTH, 0.000000001D, "nm", "text.convert.nanometer"),
        BOHRRADIUS(UnitType.LENGTH, 0.0000000000529177210903D, "br", "text.convert.bohrradius"),
        ANGSTROM(UnitType.LENGTH, 0.0000000001D, "anst", "text.convert.angstrom"),
        PICOMETER(UnitType.LENGTH, 0.000000000001D, "pm", "text.convert.picometer"),
        TWIP(UnitType.LENGTH, 0.00000000001764D, "tp", "text.convert.twip"),
        FEMTOMETER(UnitType.LENGTH, 0.000000000000001D, "fm", "text.convert.femtometer"),
        SIEGBAHN(UnitType.LENGTH, 0.00000000000010021D, "xu", "text.convert.siegbahn"),
        ATTOMETER(UnitType.LENGTH, 0.000000000000000001D, "am", "text.convert.attometer"),
        ZEPTOMETER(UnitType.LENGTH, 0.000000000000000000001D, "zm", "text.convert.zeptometer"),
        YOCTOMETER(UnitType.LENGTH, 0.000000000000000000000001D, "ym", "text.convert.yoctometer"),
        //Units of time
        YOTTASECOND(UnitType.TIME, 1000000000000000000000000D, "Ys", "text.convert.yottasecond"),
        ZETTASECOND(UnitType.TIME, 1000000000000000000000D, "Zs", "text.convert.zettasecond"),
        EXASECOND(UnitType.TIME, 1000000000000000000D, "Es", "text.convert.exasecond"),
        PETASECOND(UnitType.TIME, 1000000000000000D, "Ps", "text.convert.petasecond"),
        TERASECOND(UnitType.TIME, 1000000000000D, "Ts", "text.convert.terasecond"),
        MILLENNIUM(UnitType.TIME, 31557600000D, "my", "text.convert.millennium"),
        CENTURY(UnitType.TIME, 3155760000D, "ky", "text.convert.century"),
        GIGASECOND(UnitType.TIME, 1000000000D, "Gs", "text.convert.gigasecond"),
        DECADE(UnitType.TIME, 315576000D, "dy", "text.convert.decade"),
        YEAR(UnitType.TIME, 31557600D, "y", "text.convert.year"),
        MONTH(UnitType.TIME, 2629800D, "M", "text.convert.month"),
        MEGASECOND(UnitType.TIME, 1000000D, "Ms", "text.convert.megasecond"),
        WEEK(UnitType.TIME, 604800D, "w", "text.convert.week"),
        DAY(UnitType.TIME, 86400D, "d", "text.convert.day"),
        HOUR(UnitType.TIME, 3600D, "h", "text.convert.hour"),
        KILOSECOND(UnitType.TIME, 1000D, "ks", "text.convert.kilosecond"),
        HECTOSECOND(UnitType.TIME, 100D, "hs", "text.convert.hectosecond"),
        MINUTE(UnitType.TIME, 60D, "min", "text.convert.minute"),
        DECASECOND(UnitType.TIME, 10D, "das", "text.convert.decasecond"),
        SECOND(UnitType.TIME, 1D, "s", "text.convert.second"),
        DECISECOND(UnitType.TIME, 0.1D, "ds", "text.convert.decisecond"),
        TIERCE(UnitType.TIME, 0.01666666666D, "t", "text.convert.tierce"),
        CENTISECOND(UnitType.TIME, 0.01D, "cs", "text.convert.centisecond"),
        MILLISECOND(UnitType.TIME, 0.001D, "ms", "text.convert.millisecond"),
        MICROSECOND(UnitType.TIME, 0.000001D,"µs", "text.convert.microsecond"),
        NANOSECOND(UnitType.TIME, 0.000000001D, "ns", "text.convert.nanosecond"),
        PICOSECOND(UnitType.TIME, 0.000000000001D, "ps", "text.convert.picosecond"),
        FEMTOSECOND(UnitType.TIME, 0.000000000000001D, "fs", "text.convert.femtosecond"),
        ATTOSECONDE(UnitType.TIME, 0.000000000000000001D, "as","text.convert.attosecond"),
        ZEPTOSECOND(UnitType.TIME, 0.000000000000000000001D, "zs", "text.convert.zeptosecond"),
        YOCTOSECOND(UnitType.TIME, 0.000000000000000000000001D, "ys", "text.convert.yoctosecond");

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
        LENGTH("text.convert.length"),
        TIME("text.convert.time");

        private final String unitTypeName;

        UnitType(String unitTypeName){
            this.unitTypeName = unitTypeName;
        }
    }
}