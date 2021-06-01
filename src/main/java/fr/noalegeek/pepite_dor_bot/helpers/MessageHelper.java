package fr.noalegeek.pepite_dor_bot.helpers;

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
}
