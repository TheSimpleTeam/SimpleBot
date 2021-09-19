package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sun.org.apache.bcel.internal.generic.ATHROW;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.helpers.RequestHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;

public class ConvertCommand extends Command {

    public ConvertCommand() {
        this.name = "convert";
        this.aliases = new String[]{"co"};
        this.arguments = "<nombre> <unité de mesure> <autre unité de mesure>";
        this.help = "Convertis une unité de mesure en une autre.";
        this.example = "10 meter centimeter";
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 3) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "syntax");
        }
        try {
            double number = Double.parseDouble(args[0]);
        } catch (NumberFormatException ignore) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous n'avez pas spécifié un nombre.");
            return;
        }
        double factor = Unit.setFactor(args[1], args[2]);
        if (factor == 2) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "mesure1 n'existe pas");
            return;
        }
        if (factor == 3) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "mesure2 n'existe pas");
            return;
        }
        MessageEmbed successEmbed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("\u2705 Convertion réussie")
                .addField("De :", args[0] + " " + args[1], false)
                .addField("À :", Integer.parseInt(args[0]) * factor + " " + args[2], true)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .build();
        event.reply(successEmbed);
    }

    private enum Unit {
        YOTTAMETER(UnitType.LENGTH, 1000000000000000000000000D, "Ym"),
        ZETTAMETER(UnitType.LENGTH, 1000000000000000000000D, "Zm"),
        EXAMETER(UnitType.LENGTH, 1000000000000000000D, "Em"),
        PETAMETER(UnitType.LENGTH, 1000000000000000D, "Pm"),
        TELAMETER(UnitType.LENGTH, 1000000000000D, "Tm"),
        GIGAMETER(UnitType.LENGTH, 1000000000D, "Gm"),
        MEGAMETER(UnitType.LENGTH, 1000000D, "Mm"),
        MYRIAMETER(UnitType.LENGTH, 10000D, "mam"),
        KILOMETER(UnitType.LENGTH, 1000D, "km"),
        HECTOMETER(UnitType.LENGTH, 100D, "hm"),
        DECAMETER(UnitType.LENGTH, 10D, "dam"),
        METER(UnitType.LENGTH, 1.0D, "m"),
        DECIMETER(UnitType.LENGTH, 0.1D, "dm"),
        CENTIMETER(UnitType.LENGTH, 0.01D, "cm"),
        MILLIMETER(UnitType.LENGTH, 0.001D, "mm"),
        DECIMILLIMETER(UnitType.LENGTH, 0.0001D, "dmm"),
        CENTIMILLIMETER(UnitType.LENGTH, 0.00001D, "cmm"),
        MICROMETER(UnitType.LENGTH, 0.000001D, "µm"),
        NANOMETER(UnitType.LENGTH, 0.000000001D, "nm"),
        ANGSTROM(UnitType.LENGTH, 0.0000000001D, "as"),
        PICOMETER(UnitType.LENGTH, 0.000000000001D, "pm"),
        FEMTOMETER(UnitType.LENGTH, 0.000000000000001D, "fm"),
        ATTOMETER(UnitType.LENGTH, 0.000000000000000001D, "am"),
        ZEPTOMETER(UnitType.LENGTH, 0.000000000000000000001D, "zm"),
        YOCTOMETER(UnitType.LENGTH, 0.000000000000000000000001D, "ym");

        private final UnitType unitType;
        private final double factor;
        private final String symbol;

        Unit(UnitType unitType, double factor, String symbol) {
            this.unitType = unitType;
            this.factor = factor;
            this.symbol = symbol;
        }

        private static double setFactor(String symbol1, String symbol2) {
            double factor1 = 0, factor2 = 0;
            for (Unit unit : Unit.values()) {
                if (unit.symbol.equals(symbol1)) {
                    factor1 = unit.factor;
                    break;
                }
            }
            for (Unit unit : Unit.values()) {
                if (unit.symbol.equals(symbol2)) {
                    factor2 = unit.factor;
                    break;
                }
            }
            if (factor1 == 0) return 2;
            if (factor2 == 0) return 3;
            return factor1 / factor2;
        }
    }

    private enum UnitType {
        LENGTH()
    }
}
