package fr.noalegeek.pepite_dor_bot.utils.helpers;

import com.jagrosh.jdautilities.command.Command;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formatDate(OffsetDateTime date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        return day + "-" + month + "-" + year;
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static String syntaxError(User user, Command command) {
        return MessageHelper.formattedMention(user) + "Syntaxe de la commande " + Main.getInfos().prefix + command.getName() + " : ``" + Main.getInfos().prefix + command.getName()
                + " " + command.getArguments() + "``.";
    }

}
