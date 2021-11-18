package fr.noalegeek.pepite_dor_bot.utils;

import com.google.gson.JsonElement;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageHelper {

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static void syntaxError(CommandEvent event, Command command, String informations) {
        //TODO Optimize that if possible (only argument part)
        StringBuilder argumentsBuilder = new StringBuilder();
        List<String> oneArgumentList = new ArrayList<>();
        List<String> twoArgumentsList = new ArrayList<>();
        List<String> threeArgumentsList = new ArrayList<>();
        if (command.getArguments() == null)
            argumentsBuilder.append(translateMessage("text.commands.syntaxError.arguments.argumentsNull", event));
        else if (!command.getArguments().startsWith("arguments."))
            argumentsBuilder.append(translateMessage(command.getArguments(), event));
        else {
            if (translateMessage(command.getArguments(), event).split("\n").length == 1) {
                argumentsBuilder.append(translateMessage(command.getArguments(), event));
            } else {
                for (int index = 0; index < translateMessage(command.getArguments(), event).split("\n").length; index++) {
                    if (translateMessage(command.getArguments(), event).split("\n")[index].split(">").length == 1)
                        oneArgumentList.add(translateMessage(command.getArguments(), event).split("\n")[index]);
                    if (translateMessage(command.getArguments(), event).split("\n")[index].split(">").length == 2)
                        twoArgumentsList.add(translateMessage(command.getArguments(), event).split("\n")[index]);
                    if (translateMessage(command.getArguments(), event).split("\n")[index].split(">").length == 3)
                        threeArgumentsList.add(translateMessage(command.getArguments(), event).split("\n")[index]);
                }
                if (!oneArgumentList.isEmpty()) {
                    argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.arguments.oneArgument", event)).append("__").append("\n\n");
                    oneArgumentList.forEach(oneArgument -> argumentsBuilder.append(oneArgument).append("\n"));
                    argumentsBuilder.append("\n");
                }
                if (!twoArgumentsList.isEmpty()) {
                    argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.arguments.twoArguments", event)).append("__").append("\n\n");
                    twoArgumentsList.forEach(twoArguments -> argumentsBuilder.append(twoArguments).append("\n"));
                    argumentsBuilder.append("\n");
                }
                if (!threeArgumentsList.isEmpty()) {
                    argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.arguments.threeArguments", event)).append("__").append("\n\n");
                    threeArgumentsList.forEach(threeArguments -> argumentsBuilder.append(threeArguments).append("\n"));
                    argumentsBuilder.append("\n");
                }
            }
        }
        EmbedBuilder syntaxErrorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(translateMessage("text.commands.syntaxError.syntaxError", event), command.getName()))
                .addField(translateMessage("text.commands.syntaxError.arguments.arguments", event), argumentsBuilder.toString(), false)
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
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
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
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTimestamp(Instant.now());
        if (!member.canInteract(target))
            errorCantInteractEmbed.setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("text.commands.cantInteract.member", event));
        if (!bot.canInteract(target))
            errorCantInteractEmbed.setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("text.commands.cantInteract.bot", event));
        event.reply(new MessageBuilder(errorCantInteractEmbed.build()).build());
        return true;
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
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
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
}