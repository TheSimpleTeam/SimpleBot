package fr.noalegeek.pepite_dor_bot.utils.helpers;

import com.google.gson.JsonElement;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.entities.User;

import java.time.OffsetDateTime;
import java.util.Optional;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static String syntaxError(CommandEvent event, Command command) {
        String syntaxMessage = MessageHelper.formattedMention(event.getAuthor()) + translateMessage("text.syntaxError.syntax", event) + Main.getInfos().prefix + command.getName() + " : `" +
                Main.getInfos().prefix + command.getName() + " ";
        if(!command.getArguments().isEmpty()){
            if(command.getArguments().startsWith("arguments.")) syntaxMessage += translateMessage(command.getArguments(), event);
            else syntaxMessage += command.getArguments();
        }
        syntaxMessage += "`.\n";
        if(!command.getHelp().isEmpty()) syntaxMessage += translateMessage(command.getHelp(), event) + "\n";
        if(!command.getExample().isEmpty()){
            syntaxMessage += translateMessage("text.syntaxError.example", event) + Main.getInfos().prefix + command.getName() + " ";
            if(command.getExample().startsWith("example.")) syntaxMessage += translateMessage(command.getExample(), event);
            else syntaxMessage += command.getExample();
        }
        return syntaxMessage + "`.\n";
    }

    public static void sendError(Exception ex, CommandEvent event) {
        event.replyError(formattedMention(event.getAuthor()) + translateMessage("text.sendError", event) + "\n" + ex.getMessage());
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

    /**
     *
     * @param key the localization key
     * @param event for getting the guild's ID
     * @return the translated value
     * @throws NullPointerException if the key does not exist in any localization files.
     */
    public static String translateMessage(String key, CommandEvent event) { 
        return translateMessage(key, event, false);
    }

    private static String translateMessage(String key, CommandEvent event, boolean ignoreError) {
        String lang = Main.getServerConfig().language.getOrDefault(event.getGuild().getId(), "en");
        Optional<JsonElement> s = Optional.ofNullable(Main.getLocalizations().get(lang).get(key));
        if(s.isPresent()) return s.get().getAsString();
        if (!ignoreError && Main.getLocalizations().get("en").get(key) == null) {
            event.replyError(formattedMention(event.getAuthor()) + translateMessage("text.sendError", event) + "\n" + String.format(translateMessage("error.translateMessage", event), key));
            throw new NullPointerException(String.format(translateMessage("error.translateMessage", event), key));
        }
        return Optional.ofNullable(Main.getLocalizations().get("en").get(key).getAsString()).orElse(key);
    }
}
