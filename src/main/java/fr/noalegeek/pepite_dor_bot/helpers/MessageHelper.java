package fr.noalegeek.pepite_dor_bot.helpers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHelper {

    public final static Pattern ID = Pattern.compile("\\d{17,20}");
    public final static Pattern MENTION = Pattern.compile("<@!?(\\d{17,20})>");

    public static Member getMember(final String arg, final Guild guild) {
        final Matcher userMention = MENTION.matcher(arg);
        if (userMention.matches()) {
            final Member member = guild.getMemberById(userMention.group(1));
            if (member != null) {
                return member;
            }
        } else if (ID.matcher(arg).matches()) {
            final Member member = guild.getMemberById(arg);
            if (member != null) {
                return member;
            }
        }
        return null;
    }

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
