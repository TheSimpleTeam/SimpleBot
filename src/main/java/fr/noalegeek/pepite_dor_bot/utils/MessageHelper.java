package fr.noalegeek.pepite_dor_bot.utils;

import com.google.gson.JsonElement;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static Message syntaxError(CommandEvent event, Command command) {
        return syntaxError(event, command, null);
    }

    public static Message syntaxError(CommandEvent event, Command command, String informations) {
        EmbedBuilder syntaxEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .setTitle("\u274C " + String.format(translateMessage("text.commands.syntaxError", event), command.getName()))
                .addField(translateMessage("text.commands.syntaxError.syntax", event), command.getArguments() == null ? translateMessage("text.commands.syntaxError.arguments.argumentsNull", event) : command.getArguments().startsWith("arguments.") ? translateMessage(command.getArguments(), event) : command.getArguments(),false)
                .addField(translateMessage("text.commands.syntaxError.help", event), command.getHelp() == null ? translateMessage("text.commands.syntaxError.help.helpNull", event) : translateMessage(command.getHelp(), event), false)
                .addField(translateMessage("text.commands.syntaxError.example", event), command.getExample() == null ? translateMessage("text.commands.syntaxError.example.exampleNull", event) : command.getExample().startsWith("example.") ? translateMessage(command.getExample(), event) : command.getExample(), false);
        if(informations != null) {
            syntaxEmbed.addField(translateMessage("text.commands.syntaxError.informations", event), informations.startsWith("syntax.") ? translateMessage(informations, event) : informations, false);
        }
        //TODO [REMINDER] When all syntaxError of commands are translated, remove the informations lambda thing and add "translateMessage(informations, event)"
        return new MessageBuilder(syntaxEmbed.build()).build();
    }

    public static void sendError(Exception ex, CommandEvent event) {
        event.reply(formattedMention(event.getAuthor()) + translateMessage("text.sendError", event) + "\n" + ex.getMessage());
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
        String lang = Main.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en");
        Optional<JsonElement> s = Optional.ofNullable(Main.getLocalizations().get(lang).get(key));
        if(s.isPresent()) return s.get().getAsString();
        if (!ignoreError && Main.getLocalizations().get("en").get(key) == null) {
            event.reply(formattedMention(event.getAuthor()) + translateMessage("text.sendError", event) + "\n" + String.format(translateMessage("error.translateMessage",
                    event), key));
            throw new NullPointerException(String.format(translateMessage("error.translateMessage", event), key));
        }
        return Optional.ofNullable(Main.getLocalizations().get("en").get(key).getAsString()).orElse(key);
    }
}