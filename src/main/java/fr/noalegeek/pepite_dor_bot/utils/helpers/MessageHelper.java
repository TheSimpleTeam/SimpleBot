package fr.noalegeek.pepite_dor_bot.utils.helpers;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static String syntaxError(User user, Command command) {
        String syntaxMessage = formattedMention(user) + "Syntaxe de la commande " + Main.getInfos().prefix + command.getName() + " : `" + Main.getInfos().prefix + command.getName();
        if(!command.getArguments().isEmpty()) syntaxMessage += " " + command.getArguments() + "`.\n";
        else syntaxMessage += "`.\n";
        if(!command.getHelp().isEmpty()) syntaxMessage += command.getHelp() + "\n";
        if(!command.getExample().isEmpty()) syntaxMessage += "Par exemple : `" + Main.getInfos().prefix+command.getName() + " " + command.getExample() + "`.\n";
        return syntaxMessage + "\n";
    }

    public static void sendError(Exception ex, CommandEvent event) {
        event.replyError("Une erreur est survenue. Veuillez contacter les développeurs et envoyez ce message :\n" + ex.getMessage());
        Main.LOGGER.severe(ex.getMessage());
    }

    public static String formatShortDate(OffsetDateTime date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        if(month < 10){
            String strMonth = "0" + month;
            return day + "/" + strMonth + "/" + year;
        }
        return day + "/" + month + "/" + year;
    }
}
