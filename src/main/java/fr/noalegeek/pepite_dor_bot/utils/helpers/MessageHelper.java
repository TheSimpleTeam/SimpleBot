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
        String syntaxMessage = MessageHelper.formattedMention(event.getAuthor()) + translateMessage("messageHelper.syntaxError.syntax", event.getGuild().getId()) + Main.getInfos().prefix + command.getName() + " : `" +
                Main.getInfos().prefix + command.getName() + " ";
        if(!command.getArguments().isEmpty()){
            if(command.getArguments().startsWith("arguments.")) syntaxMessage += translateMessage(command.getArguments(), event.getGuild().getId());
            else syntaxMessage += command.getArguments();
        }
        syntaxMessage += "`.\n";
        if(!command.getHelp().isEmpty()) syntaxMessage += translateMessage(command.getHelp(), event.getGuild().getId()) + "\n";
        if(!command.getExample().isEmpty()){
            syntaxMessage += translateMessage("messageHelper.syntaxError.example", event.getGuild().getId()) + Main.getInfos().prefix + command.getName() + " ";
            if(command.getExample().startsWith("example.")) syntaxMessage += translateMessage(command.getExample(), event.getGuild().getId());
            else syntaxMessage += command.getExample();
        }
        return syntaxMessage + "`.\n";
    }

    public static void sendError(Exception ex, CommandEvent event) {
        event.replyError(translateMessage("messageHelper.sendError", event.getGuild().getId()) + ex.getMessage());
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
     * @param guildID ID of the guild
     * @return the translated value
     * @throws NullPointerException if the key does not exist in any localization files.
     */
    public static String translateMessage(String key, String guildID) {
        String lang = Main.getServerConfig().language.getOrDefault(guildID, "en");
        Optional<JsonElement> s = Optional.ofNullable(Main.getLocalizations().get(lang).get(key));
        if(s.isPresent()) return s.get().getAsString();
        if (Main.getLocalizations().get("en").get(key) == null) {
            throw new NullPointerException("This key does not exist in any localization file!");
        }
        return Main.getLocalizations().get("en").get(key).getAsString();
    }
}
