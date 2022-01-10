package fr.noalegeek.pepite_dor_bot.utils;

import com.google.gson.JsonElement;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class MessageHelper {

    private MessageHelper() {
    }

    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    public static String formattedMention(User user) {
        return String.format("**[**%s**]** ", user.getAsMention());
    }

    public static void syntaxError(CommandEvent event, Command command, String informations) {
        StringBuilder argumentsBuilder = new StringBuilder();
        if (command.getArguments() == null)
            argumentsBuilder.append(translateMessage("text.commands.syntaxError.arguments.argumentsNull", event));
        else if (!command.getArguments().startsWith("arguments."))
            argumentsBuilder.append(command.getArguments());
        else {
            if (translateMessage(command.getArguments(), event).split("²").length == 1) {
                argumentsBuilder.append(translateMessage(command.getArguments(), event));
            } else {
                int index = 1;
                if (!Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(arg -> arg != null && arg.split(">").length == 1).toList().isEmpty()) {
                    argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.arguments.oneArgument", event)).append("__").append("\n\n");
                    for (int index1 = 0; index1 < Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 1).toList().size(); index1++) {
                        argumentsBuilder.append(Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 1).toList().get(index1)).append(" **->** *").append(translateMessage(command.getHelp(), event).split("²")[index]).append("*\n");
                        index++;
                    }
                    argumentsBuilder.append("\n");
                }
                if (!Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 2).toList().isEmpty()) {
                    argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.arguments.twoArguments", event)).append("__").append("\n\n");
                    for (int index2 = 0; index2 < Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 2).toList().size(); index2++) {
                        argumentsBuilder.append(Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 2).toList().get(index2)).append(" **->** *").append(translateMessage(command.getHelp(), event).split("²")[index]).append("*\n");
                        index++;
                    }
                    argumentsBuilder.append("\n");
                }
                if (!Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 3).toList().isEmpty()) {
                    argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.arguments.threeArguments", event)).append("__").append("\n\n");
                    for (int index3 = 0; index3 < Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 3).toList().size(); index3++) {
                        argumentsBuilder.append(Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(args -> args != null && args.split(">").length == 3).toList().get(index3)).append(" **->** *").append(translateMessage(command.getHelp(), event).split("²")[index]).append("*\n");
                        index++;
                    }
                }
            }
        }
        if (informations != null)
            argumentsBuilder.append("__").append(translateMessage("text.commands.syntaxError.informations", event)).append("__").append("\n").append(informations.startsWith("informations.") ? translateMessage(informations, event) : informations);
        String examples;
        if (command.getExample() == null)
            examples = translateMessage("text.commands.syntaxError.examples.exampleNull", event);
        else if (command.getExample().startsWith("example."))
            examples = Arrays.toString(Stream.of(translateMessage(command.getExample(), event).split("²")).map(example -> example = Main.getPrefix(event.getGuild()) + command.getName() + " " + example).toArray()).replace("[", "").replace("]", "").replace(",", "");
        else
            examples = Arrays.toString(Stream.of(command.getExample().split("²")).map(example -> example = Main.getPrefix(event.getGuild()) + command.getName() + " " + example).toArray()).replace("[", "").replace("]", "").replace(",", "");
        EmbedBuilder syntaxErrorEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(translateMessage("text.commands.syntaxError.syntaxError", event), command.getName()))
                .addField(command.getArguments().startsWith("arguments.") ? translateMessage(command.getArguments(), event).split("²").length == 1 ? translateMessage("text.commands.syntaxError.arguments.argument", event) : translateMessage("text.commands.syntaxError.arguments.arguments", event) : command.getArguments().split("²").length == 1 ? translateMessage("text.commands.syntaxError.arguments.argument", event) : translateMessage("text.commands.syntaxError.arguments.arguments", event), argumentsBuilder.toString(), false)
                .addField(translateMessage("text.commands.syntaxError.help", event), command.getHelp() == null || command.getHelp().isEmpty() ? translateMessage("text.commands.syntaxError.help.helpNull", event) : translateMessage(command.getHelp(), event).contains("²") ? translateMessage(command.getHelp(), event).split("²")[0] : translateMessage(command.getHelp(), event), false)
                .addField(command.getExample().startsWith("example.") ? translateMessage(command.getExample(), event).split("²").length == 1 ? translateMessage("text.commands.syntaxError.examples.example", event) : translateMessage("text.commands.syntaxError.examples.examples", event) : command.getExample().split("²").length == 1 ? translateMessage("text.commands.syntaxError.examples.example", event) : translateMessage("text.commands.syntaxError.examples.examples", event), examples, false);
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

    /**
     * @param key   the localization key
     * @param event for getting the guild's ID
     * @return the translated value
     * @throws NullPointerException if the key does not exist in any localization files.
     */
    public static String translateMessage(@NotNull String key, @NotNull CommandEvent event) {
        return translateMessage(key, event.getAuthor(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getTextChannel(), event.getGuild());
    }

    public static String translateMessage(@NotNull String key, @NotNull User author, @Nullable User owner, @NotNull Guild guild) {
        return translateMessage(key, author, owner, null, guild);
    }

    private static String translateMessage(@NotNull String key, @NotNull User author, @Nullable User owner, @Nullable TextChannel channel, @NotNull Guild guild) {
        String lang = Main.getServerConfig().language().getOrDefault(guild.getId(), "en");
        Optional<JsonElement> s = Optional.ofNullable(Main.getLocalizations().get(lang).get(key));
        if (s.isPresent()) return s.get().getAsString();
        if (Main.getLocalizations().get("en").get(key) == null) {
            StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
            long skip = 2;
            if (stackWalker.walk(f -> f.skip(1).findFirst().orElseThrow()).getMethodName().equalsIgnoreCase("getHelpConsumer")) skip++;
            final long _skip = skip;
            if (channel != null) channel.sendMessage(new MessageBuilder(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(author), author.getEffectiveAvatarUrl())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.translateMessage.error", author, owner, channel, guild), key))
                    .addField(MessageHelper.translateMessage("error.translateMessage.key", author, owner, channel, guild), key, false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.class", author, owner, channel, guild), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.method", author, owner, channel, guild), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.lineNumber", author, owner, channel, guild), String.valueOf(stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false).build()).build()).queue();
            if(owner != null) owner.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(owner), owner.getEffectiveAvatarUrl())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.translateMessage.error", author, owner, channel, guild), key))
                    .addField(MessageHelper.translateMessage("error.translateMessage.key", author, owner, channel, guild), key, false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.class", author, owner, channel, guild), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.method", author, owner, channel, guild), stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.lineNumber", author, owner, channel, guild), String.valueOf(stackWalker.walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false).build()).build()).queue());
            throw new NullPointerException("The key " + key + " does not exist!");
        }
        try {
            return Main.getLocalizations().get("en").get(key).getAsString();
        } catch (NullPointerException ex) {
            return key;
        }
    }
}