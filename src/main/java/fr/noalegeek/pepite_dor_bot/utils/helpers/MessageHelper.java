package fr.noalegeek.pepite_dor_bot.utils.helpers;

import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.BotCommand;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;
import java.util.Locale;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formatDate(OffsetDateTime date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        if(month < 10){
            String strMonth = "0"+month;
            return day + "/" + strMonth + "/" + year;
        }
        return day + "/" + month + "/" + year;
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static String syntaxError(User user, BotCommand command) {
        String syntaxMessage = MessageHelper.formattedMention(user) + "Syntaxe de la commande " + Main.getInfos().prefix + command.getName() + " : ``" + Main.getInfos().prefix + command.getName()
                + " " + command.getArguments() + "``.";
        if(!command.getExample().isEmpty()) {
            syntaxMessage += "\n" +
                    "Par exemple : " + Main.getInfos().prefix + command.getName() + " " + command.getExample();
        }
        return syntaxMessage;
    }

    public static String formatEnum(String name) {
        StringBuilder builder = new StringBuilder();
        char[] nameChar = name.toCharArray();
        for (int i = 0; i < nameChar.length; i++) {
            if(i == 0) {
                builder.append(String.valueOf(nameChar[i]).toUpperCase(Locale.ROOT));
            } else {
                builder.append(nameChar[i]);
            }
        }
        return builder.toString();
    }


    public static String formatEnum(Enum<?> _enum) {
        StringBuilder builder = new StringBuilder();
        char[] nameArray = _enum.name().toLowerCase(Locale.ROOT).toCharArray();
        for (char c : nameArray) {
            if(c == nameArray[0]) {
                builder.append(String.valueOf(c).toUpperCase(Locale.ROOT));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

}
