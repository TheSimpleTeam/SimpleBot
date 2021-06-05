package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.helpers.RequestHelper;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;

public class ConvertCommand extends BotCommand {

    public ConvertCommand() {
        this.name = "convert";
        this.aliases = new String[]{"co"};
        this.arguments = "<type> <unité de mesure> <valeur> <convertion en unité de mesure>";
        this.example = "length meter 10 centimeter";
    }

    private final String BASE_URL = "https://java-unit-conversion.herokuapp.com/api/";

    private String capitalize(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }
        StringBuilder builder = new StringBuilder();
        char[] c = string.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if(i == 0) {
                builder.append(String.valueOf(c[0]).toUpperCase());
            } else {
                builder.append(String.valueOf(c[i]).toLowerCase(Locale.ROOT));
            }
        }
        return builder.toString();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if(args.length < 3) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this)+"\nLe type de mesure doit être un de ces 4 types de mesure : \"**volume**\",\"**longueur**\",\"**poids**\",\"**température**\".\n");
            return;
        }

        UnitType type;
        String unit = args[1];
        double value;
        String convertTo = args[3];

        try {
            type = UnitType.valueOf(args[0].toUpperCase());
        }catch (IllegalArgumentException e) {
            event.replyError("Le premier argument est invalide. Les types de mesure acceptés sont \"**volume**\",\"**longueur**\",\"**poids**\",\"**température**\".");
            return;
        }

        try{
            value = Double.parseDouble(args[2]);
        }catch (NumberFormatException ignored) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }

        if(unit.isEmpty() || convertTo.isEmpty()) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }

        try {
            Response response = RequestHelper.sendRequest(String.format("%s%s/%s/%f", BASE_URL, type.name().toLowerCase(), unit, value));
            if(!response.isSuccessful()) {
                event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
                return;
            }
            JsonObject object = Main.gson.fromJson(RequestHelper.getResponseAsString(response), JsonObject.class);
            JsonElement valueConverted = object.get(convertTo.toLowerCase());
            if(valueConverted == null) {
                event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
                return;
            }
            event.replySuccess("Convertion en cours...");
            event.getChannel().sendMessage(value + " " + capitalize(type.name()) + " = " + valueConverted.getAsDouble() + " " + capitalize(convertTo)).queue();
        } catch (IOException exception) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
        }

    }

    private enum UnitType {
        VOLUME,
        LONGUEUR,
        POIDS,
        TEMPERATURE
    }
}
