package fr.noalegeek.pepite_dor_bot.utils;

import com.google.gson.JsonElement;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static void syntaxError(CommandEvent event, Command command) {
        syntaxError(event, command, null);
    }

    public static void syntaxError(CommandEvent event, Command command, String informations) {
        EmbedBuilder syntaxErrorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(translateMessage("text.commands.syntaxError", event), command.getName()))
                .addField(translateMessage("text.commands.syntaxError.syntax", event), command.getArguments() == null ? translateMessage("text.commands.syntaxError.arguments.argumentsNull", event) : command.getArguments().startsWith("arguments.") ? translateMessage(command.getArguments(), event) : command.getArguments(), false)
                .addField(translateMessage("text.commands.syntaxError.help", event), command.getHelp() == null || command.getHelp().isEmpty() ? translateMessage("text.commands.syntaxError.help.helpNull", event) : translateMessage(command.getHelp(), event), false)
                .addField(translateMessage("text.commands.syntaxError.example", event), command.getExample() == null ? translateMessage("text.commands.syntaxError.example.exampleNull", event) : command.getExample().startsWith("example.") ? translateMessage(command.getExample(), event) : command.getExample(), false);
        if (informations != null) {
            syntaxErrorEmbed.addField(translateMessage("text.commands.syntaxError.informations", event), informations.startsWith("syntax.") ? translateMessage(informations, event) : informations, false);
        }
        //TODO [REMINDER] When all syntaxError of commands are translated, remove the informations lambda thing and add "translateMessage(informations, event)"
        event.reply(new MessageBuilder(syntaxErrorEmbed.build()).build());
    }

    public static void sendError(Exception exception, CommandEvent event, Command command) {
        EmbedBuilder sendErrorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("text.commands.sendError.error", event))
                .addField(MessageHelper.translateMessage("text.commands.sendError.sendError", event), exception.getMessage(), false)
                .addField(MessageHelper.translateMessage("text.commands.sendError.command", event), Main.getPrefix(event.getGuild()) + command.getName(), false);
        if (command.getArguments() == null || command.getArguments().isEmpty()) {
            event.reply(new MessageBuilder(sendErrorEmbed.build()).build());
            return;
        }
        sendErrorEmbed.addField(MessageHelper.translateMessage("text.commands.sendError.arguments", event), event.getArgs(), false);
        event.reply(new MessageBuilder(sendErrorEmbed.build()).build());
    }

    public static void sendError(Exception exception, SlashCommandEvent event, Command command) {
        EmbedBuilder sendErrorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getUser()), event.getUser().getAvatarUrl() == null ? event.getUser().getDefaultAvatarUrl() : event.getUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("text.commands.sendError.error", event))
                .addField(MessageHelper.translateMessage("text.commands.sendError.sendError", event), exception.getMessage(), false)
                .addField(MessageHelper.translateMessage("text.commands.sendError.command", event), Main.getPrefix(event.getGuild()) + command.getName(), false);
        if (command.getArguments() == null || command.getArguments().isEmpty()) {
            event.reply(new MessageBuilder(sendErrorEmbed.build()).build()).setEphemeral(true).queue();
            return;
        }
        sendErrorEmbed.addField(MessageHelper.translateMessage("text.commands.sendError.arguments", event),
                String.join(" ", ArrayUtils.removeElement(event.getCommandString().split("\\s+"), 1)), false);
        event.reply(new MessageBuilder(sendErrorEmbed.build()).build()).setEphemeral(true).queue();
    }

    public static String formatShortDate(OffsetDateTime date) {
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();
        if (month < 10) {
            String strMonth = "0" + month;
            return day + "/" + strMonth + "/" + year;
        }
        return day + "/" + month + "/" + year;
    }

    public static boolean cantInteract(Member member, Member bot, Member target, CommandEvent event) {
        if (member.canInteract(target) && bot.canInteract(target)) return false;
        EmbedBuilder errorCantInteractEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                .setTimestamp(Instant.now());
        if (!member.canInteract(target)) errorCantInteractEmbed.setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("text.commands.cantInteract.member", event));
        if (!bot.canInteract(target)) errorCantInteractEmbed.setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("text.commands.cantInteract.bot", event));
        event.reply(new MessageBuilder(errorCantInteractEmbed.build()).build());
        return true;
    }

    public static String arrayToString(List<CharSequence> charSequenceList, @Nullable String delimiter, @Nullable String prefix, @Nullable String suffix){
        StringBuilder stringBuilder = prefix != null ? new StringBuilder().append(prefix) : new StringBuilder();
        for (CharSequence charSequence : charSequenceList) {
            stringBuilder.append(charSequence);
            if (delimiter != null) stringBuilder.append(delimiter);
        }
        return suffix != null ? stringBuilder.append(suffix).toString() : stringBuilder.toString();
    }

    public static String arrayToString(List<CharSequence> charSequenceList){
        return arrayToString(charSequenceList, null, null, null);
    }

    public static String arrayToStringDelimiter(List<CharSequence> charSequenceList, @Nullable String delimiter){
        return arrayToString(charSequenceList, delimiter, null, null);
    }

    public static String arrayToStringDelimiterPrefix(List<CharSequence> charSequenceList, @Nullable String delimiter, @Nullable String prefix){
        return arrayToString(charSequenceList, delimiter, prefix, null);
    }

    public static String arrayToStringPrefixSuffix(List<CharSequence> charSequenceList, @Nullable String prefix, @Nullable String suffix){
        return arrayToString(charSequenceList, null, prefix, suffix);
    }

    public static String arrayToStringDelimiterSuffix(List<CharSequence> charSequenceList, @Nullable String delimiter, @Nullable String suffix){
        return arrayToString(charSequenceList, delimiter, null, suffix);
    }

    public static String arrayToStringPrefix(List<CharSequence> charSequenceList, @Nullable String prefix){
        return arrayToString(charSequenceList, null, prefix, null);
    }

    public static String arrayToStringSuffix(List<CharSequence> charSequenceList, @Nullable String suffix){
        return arrayToString(charSequenceList, null, null, suffix);
    }

    public static List<CharSequence> regroupTogetherTwoArray(@Nonnull List<CharSequence> list1, @Nonnull List<CharSequence> list2, @Nullable String... dontAddStrings){
        if(list1.isEmpty() || list2.isEmpty()) return new ArrayList<>();
        List<CharSequence> charSequenceList = new ArrayList<>();
        for(CharSequence charSequence1 : list1){
            for(CharSequence charSequence2 : list2){
                if(dontAddStrings == null || !List.of(dontAddStrings).contains(charSequence1.toString() + charSequence2.toString())) charSequenceList.add(charSequence1.toString() + charSequence2.toString());
            }
        }
        return charSequenceList;
    }

    public static List<CharSequence> regroupTogetherThreeArray(@Nonnull List<CharSequence> list1, @Nonnull List<CharSequence> list2, @Nonnull List<CharSequence> list3, @Nullable String... dontAddStrings){
        if(list1.isEmpty() || list2.isEmpty()) return new ArrayList<>();
        List<CharSequence> charSequenceList = new ArrayList<>();
        for(CharSequence charSequence1 : list1){
            for(CharSequence charSequence2 : list2){
                for(CharSequence charSequence3 : list3) {
                    if (dontAddStrings == null || !List.of(dontAddStrings).contains(charSequence1.toString() + charSequence2.toString() + charSequence3.toString())) charSequenceList.add(charSequence1.toString() + charSequence2.toString() + charSequence3.toString());
                }
            }
        }
        return charSequenceList;
    }

    public static List<CharSequence> regroupTogetherTwoArray(@Nonnull List<CharSequence> list1, @Nonnull List<CharSequence> list2){
        return regroupTogetherTwoArray(list1, list2, (String[]) null);
    }

    public static List<CharSequence> regroupTogetherThreeArray(@Nonnull List<CharSequence> list1, @Nonnull List<CharSequence> list2, @Nonnull List<CharSequence> list3){
        return regroupTogetherThreeArray(list1, list2, list3, (String[]) null);
    }

    public static List<CharSequence> toTextArrayWithoutFinalsCharacters(boolean keepOriginal, String... strings){
        List<CharSequence> stringList = new ArrayList<>();
        for(String string : strings) {
            if(keepOriginal) stringList.add(string);
            for (int i = string.length() - 1; i > 0; i--) {
                stringList.add(string.substring(0, i));
            }
        }
        return stringList;
    }

    public static List<CharSequence> toTextArrayWithoutFinalsCharacters(String... strings){
        return toTextArrayWithoutFinalsCharacters(false, strings);
    }

    public static String setReason(String reason, CommandEvent event) {
        return reason == null ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + reason;
    }

    public static void sendTranslatedMessage(String key, CommandEvent event) {
        event.reply(translateMessage(key, event));
    }

    /**
     * @param key   the localization key
     * @param event for getting the guild's ID
     * @return the translated value
     * @throws NullPointerException if the key does not exist in any localization files.
     */
    public static String translateMessage(String key, CommandEvent event) {
        String lang = Main.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en");
        Optional<JsonElement> s = Optional.ofNullable(Main.getLocalizations().get(lang).get(key));
        if (s.isPresent()) return s.get().getAsString();
        if (Main.getLocalizations().get("en").get(key) == null) {
            StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
            int skip = 2;
            if (stackWalker.walk(f -> f.skip(1).findFirst().orElseThrow()).getMethodName().equalsIgnoreCase("getHelpConsumer"))
                skip++;
            final var _skip = skip;
            EmbedBuilder errorKeyNullEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.translateMessage.error", event), key))
                    .addField(MessageHelper.translateMessage("error.translateMessage.key", event), key, false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.class", event), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.method", event), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.lineNumber", event), String.valueOf(stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false);
            event.reply(new MessageBuilder(errorKeyNullEmbed.build()).build());
            throw new NullPointerException("The key " + key + " does not exist!");
        }
        try {
            return Main.getLocalizations().get("en").get(key).getAsString();
        } catch (NullPointerException ex) {
            return key;
        }
    }

    public static String translateMessage(String key, SlashCommandEvent event) {
        String lang = Main.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en");
        Optional<JsonElement> s = Optional.ofNullable(Main.getLocalizations().get(lang).get(key));
        if (s.isPresent()) return s.get().getAsString();
        if (Main.getLocalizations().get("en").get(key) == null) {
            StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
            int skip = 2;
            if (stackWalker.walk(f -> f.skip(1).findFirst().orElseThrow()).getMethodName().equalsIgnoreCase("getHelpConsumer"))
                skip++;
            final var _skip = skip;
            EmbedBuilder errorKeyNullEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getUser()), event.getUser().getAvatarUrl() == null ? event.getUser().getDefaultAvatarUrl() : event.getUser().getAvatarUrl())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.translateMessage.error", event), key))
                    .addField(MessageHelper.translateMessage("error.translateMessage.key", event), key, false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.class", event), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.method", event), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.lineNumber", event), String.valueOf(stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false);
            event.reply(new MessageBuilder(errorKeyNullEmbed.build()).build()).setEphemeral(true).queue();
            throw new NullPointerException("The key " + key + " does not exist!");
        }
        try {
            return Main.getLocalizations().get("en").get(key).getAsString();
        } catch (NullPointerException ex) {
            return key;
        }
    }
}