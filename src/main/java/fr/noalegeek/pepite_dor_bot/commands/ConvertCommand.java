package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.time.Instant;

public class ConvertCommand extends Command {

    //https://github.com/jrshutske/unit-conversion-api
    private final String BASE_URL = "https://java-unit-conversion.herokuapp.com/api/";

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

        String[] args = event.getArgs().split("\\s+");
        if (args.length != 3) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "syntax");
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
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.unitsDontExist", event));
                return;
            } else if (unit1 == null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.firstUnitDontExist", event));
                return;
            } else if (unit2 == null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.secondUnitDontExist", event));
                return;
            }
            if(unit1.unitType != unit2.unitType){
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.convert.notSameUnitType", event));
                return;
            }
            double factor = unit1.factor / unit2.factor;
            MessageEmbed successEmbed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle("\u2705 " + MessageHelper.translateMessage("success.convert.success", event))
                    .addField(MessageHelper.translateMessage("success.convert.from", event), args[0] + " " + args[1] + " (" + MessageHelper.translateMessage(unit1.unitName, event) + ")", false)
                    .addField(MessageHelper.translateMessage("success.convert.to", event), number * factor + " " + args[2] + " (" + MessageHelper.translateMessage(unit2.unitName, event) + ")", false)
                    .addField(MessageHelper.translateMessage("success.convert.factor", event), String.valueOf(factor), false)
                    .addField(MessageHelper.translateMessage("success.convert.unitType", event), MessageHelper.translateMessage(unit1.unitType.unitTypeName, event), true)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                    .build();
            event.reply(successEmbed);
        } catch (NumberFormatException ignore) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.notAnNumber", event));
        }
    }

    private enum Unit {
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
        NAUTICALMILE(UnitType.LENGTH, 1852D, "nm", "text.convert.nauticalMile"),
        MILE(UnitType.LENGTH, 1609.344D, "mi", "text.convert.mile"),
        KILOMETER(UnitType.LENGTH, 1000D, "km", "text.convert.kilometer"),
        FURLONG(UnitType.LENGTH, 201.16840233680466D, "fur", "text.convert.furlong"),
        HECTOMETER(UnitType.LENGTH, 100D, "hm", "text.convert.hectometer"),
        CHAIN(UnitType.LENGTH, 20.116840233680466D, "ch", "text.convert.chain"),
        DECAMETER(UnitType.LENGTH, 10D, "dam", "text.convert.decameter"),
        ROD(UnitType.LENGTH, 5.0292D, "ro", "text.convert.rod"),
        FATHOM(UnitType.LENGTH, 1.8288D, "fm", "text.convert.fathom"),
        ELL(UnitType.LENGTH, 1.143, "ell", "text.convert.ell"),
        METER(UnitType.LENGTH, 1.0D, "m", "text.convert.meter"),
        YARD(UnitType.LENGTH, 0.9144D, "yd", "text.convert.yard"),
        FOOT(UnitType.LENGTH, 0.3048D, "ft", "text.convert.foot"),
        SPAN(UnitType.LENGTH, 0.2286D, "sp", "text.convert.span"),
        NATURALSPAN(UnitType.LENGTH, 0.2032D, "", "text.convert.naturalSpan"),
        LINK(UnitType.LENGTH, 0.20116840233680466D, "lnk", "text.convert.link"),
        SHAFTMENT(UnitType.LENGTH, 0.1524D, "st", "text.convert.shaftment"),
        HAND(UnitType.LENGTH, 0.1016D, "ha", "text.convert.hand"),
        DECIMETER(UnitType.LENGTH, 0.1D, "dm", "text.convert.decimeter"),
        POPPYSEED(UnitType.LENGTH, 0.088194D, "ps", "text.convert.poppySeed"),
        PALM(UnitType.LENGTH, 0.0762D, "pa", "text.convert.palm"),
        NAIL(UnitType.LENGTH,  0.05715D, "na", "text.convert.nail"),
        INCH(UnitType.LENGTH, 0.0254D, "in", "text.convert.inch"),
        FINGER(UnitType.LENGTH, 0.022225D, "fg", "text.convert.finger"),
        DIGIT(UnitType.LENGTH, 0.01905D, "dg", "text.convert.digit"),
        CENTIMETER(UnitType.LENGTH, 0.01D, "cm", "text.convert.centimeter"),
        BARLEYCORN(UnitType.LENGTH, 0.008466666D, "bc", "text.convert.barleyCorn"),
        PICA(UnitType.LENGTH, 0.004233333D, "pc", "text.convert.pica"),
        LINE(UnitType.LENGTH, 0.002116D, "lin", "text.convert.line"),
        MILLIMETER(UnitType.LENGTH, 0.001D, "mm", "text.convert.millimeter"),
        PICAPOINT(UnitType.LENGTH, 0.0003527778D, "pt", "text.convert.picaPoint"),
        DECIMILLIMETER(UnitType.LENGTH, 0.0001D, "dmm", "text.convert.decimillimeter"),
        MIL(UnitType.LENGTH, 0.0000254D, "mil", "text.convert.mil"),
        CENTIMILLIMETER(UnitType.LENGTH, 0.00001D, "cmm", "text.convert.centimillimeter"),
        MICROMETER(UnitType.LENGTH, 0.000001D, "µm", "text.convert.micrometer"),
        NANOMETER(UnitType.LENGTH, 0.000000001D, "nm", "text.convert.nanometer"),
        BOHRRADIUS(UnitType.LENGTH, 0.0000000000529177210903D, "br", "text.convert.bohrradius"),
        ANGSTROM(UnitType.LENGTH, 0.0000000001D, "as", "text.convert.angstrom"),
        PICOMETER(UnitType.LENGTH, 0.000000000001D, "pm", "text.convert.picometer"),
        TWIP(UnitType.LENGTH, 0.00000000001764D, "tp", "text.convert.twip"),
        FEMTOMETER(UnitType.LENGTH, 0.000000000000001D, "fm", "text.convert.femtometer"),
        SIEGBAHN(UnitType.LENGTH, 0.00000000000010021D, "xu", "text.convert.siegbahn"),
        ATTOMETER(UnitType.LENGTH, 0.000000000000000001D, "am", "text.convert.attometer"),
        ZEPTOMETER(UnitType.LENGTH, 0.000000000000000000001D, "zm", "text.convert.zeptometer"),
        YOCTOMETER(UnitType.LENGTH, 0.000000000000000000000001D, "ym", "text.convert.yoctometer");

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
        LENGTH("text.convert.length");

        private final String unitTypeName;

        UnitType(String unitTypeName){
            this.unitTypeName = unitTypeName;
        }
    }
}
