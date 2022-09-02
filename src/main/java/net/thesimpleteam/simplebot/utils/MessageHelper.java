package net.thesimpleteam.simplebot.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.thesimpleteam.simplebot.SimpleBot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Stream;

public class MessageHelper {

    private MessageHelper() {}

    /**
     * @param user the user
     * @return user's name + # + user's discriminator
     */
    public static String getTag(final User user) {
        return user.getName() + "#" + user.getDiscriminator();
    }

    /**
     * @param event the event
     * @param command the command that is being executed
     * @param info provides information on the usages of the command
     */
    public static void syntaxError(CommandEvent event, Command command, String info) {
        StringBuilder argumentsBuilder = new StringBuilder();
        if (command.getArguments() == null)
            argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.argumentsNull"));
        else if (!command.getArguments().startsWith("arguments."))
            argumentsBuilder.append(command.getArguments());
        else {
            if (translateMessage(event, command.getArguments()).split("²").length == 1) {
                argumentsBuilder.append(SimpleBot.getPrefix(event.getGuild())).append(command.getName()).append(" ").append(translateMessage(event, command.getArguments()));
            } else {
                int loop = 1;
                for (String arg : Arrays.stream(translateMessage(event, command.getArguments()).split("²")).toList()) {
                    loop = Math.max(loop, arg.split(">").length);
                }
                int indexList = 1;
                for (int length = 1; length <= loop; length++) {
                    int finalLength = length;
                    if (Arrays.stream(translateMessage(event, command.getArguments()).split("²")).anyMatch(arguments -> arguments.split(">").length == finalLength)) {
                        if (!Arrays.stream(translateMessage(event, command.getArguments()).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLength)
                                .toList().isEmpty()) {
                            argumentsBuilder.append("__");
                            switch (finalLength) {
                                case 1 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.oneArgument"));
                                case 2 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.twoArguments"));
                                case 3 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.threeArguments"));
                                case 4 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.fourArguments"));
                                default -> argumentsBuilder.append("The devs forgotten to add the syntax with the length of ").append(finalLength);
                            }
                            argumentsBuilder.append("__").append("\n\n");
                            for (int index = 0; index < Arrays.stream(translateMessage(event, command.getArguments()).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLength).toList().size(); index++) {
                                argumentsBuilder.append(SimpleBot.getPrefix(event.getGuild())).append(command.getName()).append(" ").append(Arrays.stream(translateMessage(event, command.getArguments()).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLength).toList().get(index)).append(" \u27A1 *").append(translateMessage(event, command.getHelp()).split("²")[indexList]).append("*\n");
                                indexList++;
                            }
                            argumentsBuilder.append("\n");
                        }
                    }
                }
            }
        }
        String examples;
        if (command.getExample() == null)
            examples = translateMessage(event, "error.commands.syntaxError.examples.exampleNull");
        else if (command.getExample().startsWith("example."))
            examples = Arrays.toString(Stream.of(translateMessage(event, command.getExample()).split("²")).map(example -> example = SimpleBot.getPrefix(event.getGuild()) + command.getName() + " " + example).toArray()).replace("[", "").replace("]", "").replace(",", "");
        else
            examples = Arrays.toString(Stream.of(command.getExample().split("²")).map(example -> example = SimpleBot.getPrefix(event.getGuild()) + command.getName() + " " + example).toArray()).replace("[", "").replace("]", "").replace(",", "");
        EmbedBuilder embedBuilder = getEmbed(event, "error.commands.syntaxError.syntaxError", null, null, null, command.getName())
                .addField(command.getArguments().startsWith("arguments.") ? translateMessage(event, command.getArguments()).split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.arguments.argument") : translateMessage(event, "error.commands.syntaxError.arguments.arguments") : command.getArguments().split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.arguments.argument") : translateMessage(event, "error.commands.syntaxError.arguments.arguments"), argumentsBuilder.toString(), false)
                .addField(translateMessage(event, "error.commands.syntaxError.help"), command.getHelp() == null || command.getHelp().isEmpty() ? translateMessage(event, "error.commands.syntaxError.help.helpNull") : translateMessage(event, command.getHelp()).contains("²") ? translateMessage(event, command.getHelp()).split("²")[0] : translateMessage(event, command.getHelp()), false)
                .addField(command.getExample().startsWith("example.") ? translateMessage(event, command.getExample()).split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.examples.example") : translateMessage(event, "error.commands.syntaxError.examples.examples") : command.getExample().split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.examples.example") : translateMessage(event, "error.commands.syntaxError.examples.examples"), examples, false);
        if (info != null) {
            if (translateMessage(event, info).length() > 1024) {
                int field = 0;
                StringBuilder infoBuilder = new StringBuilder();
                for (char character : info.toCharArray()) {
                    infoBuilder.append(character);
                    if(character == '²') {
                        field++;
                        embedBuilder.addField(field == 1 ? translateMessage(event, "error.commands.syntaxError.info") : "", infoBuilder.toString(), false);
                    }
                }
            } else
                embedBuilder.addField(translateMessage(event, "error.commands.syntaxError.info"), translateMessage(event, info), false);
        }
        //TODO [REMINDER] When all syntaxError of commands are translated, remove the info lambda thing and add "translateMessage(info, event)"
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }

    /**
     * @param exception the exception that was caught
     * @param event the event
     * @param command the command
     */
    public static void sendError(Exception exception, CommandEvent event, Command command) {
        EmbedBuilder embedBuilder = getEmbed(event, "error.commands.sendError.error", null, null, null)
                .addField(translateMessage(event, "error.commands.sendError.sendError"), exception.getMessage(), false)
                .addField(translateMessage(event, "error.commands.sendError.command"), SimpleBot.getPrefix(event.getGuild()) + command.getName(), false);
        if (command.getArguments() == null || command.getArguments().isEmpty()) embedBuilder.addField(translateMessage(event, "error.commands.sendError.arguments"), event.getArgs(), false);
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }

    /**
     * @param event the event
     * @param title the title's embed
     * @param color the color's embed
     * @param description the description's embed
     * @param thumbnail the thumbnail's embed
     * @param formatArgs formats the title with a {@link String#format(String, Object...)} when formatArgs isn't null
     * @return an embed builder depending on the parameters
     */
    public static EmbedBuilder getEmbed(CommandEvent event, String title, @Nullable Color color, @Nullable String description, @Nullable String thumbnail, @Nullable Object... formatArgs){
        return getEmbed(event.getAuthor(), event.getMessage(), event.getGuild(), title, color, description, thumbnail, formatArgs);
    }

    /**
     * @param author used to set footer's embed
     * @param message used by the {@link net.thesimpleteam.simplebot.utils.MessageHelper#translateMessage(User, Message, Guild, String, String)} function
     * @param guild used by the {@link net.thesimpleteam.simplebot.utils.MessageHelper#translateMessage(User, Message, Guild, String, String)} function
     * @param title the title's embed
     * @param color the color's embed
     * @param description the description's embed
     * @param thumbnail the thumbnail's embed
     * @param formatArgs formats the title with a {@link String#format(String, Object...)} when formatArgs isn't null
     * @return an embed builder depending on the parameters
     */
    public static EmbedBuilder getEmbed(@NotNull User author, @Nullable Message message, @NotNull Guild guild, @NotNull String title, @Nullable Color color, @Nullable String description, @Nullable String thumbnail, @Nullable Object... formatArgs) {
        EmbedBuilder embedBuilder = new EmbedBuilder().setTimestamp(Instant.now()).setFooter(getTag(author), author.getEffectiveAvatarUrl());
        if(color != null) embedBuilder.setColor(color);
        else {
            if (title.startsWith("success.")) {
                embedBuilder.setColor(Color.GREEN).setTitle(UnicodeCharacters.WHITE_HEAVY_CHECK_MARK_EMOJI + " " + (formatArgs != null ? String.format(translateMessage(author, message, guild, title, SimpleBot.getServerConfig().language().get(guild.getId())), formatArgs) : translateMessage(author, message, guild, title, SimpleBot.getServerConfig().language().get(guild.getId()))));
            } else if (title.startsWith("error.")) {
                embedBuilder.setColor(Color.RED).setTitle(UnicodeCharacters.CROSS_MARK_EMOJI + " " + (formatArgs != null ? String.format(translateMessage(author, message, guild, title, SimpleBot.getServerConfig().language().get(guild.getId())), formatArgs) : translateMessage(author, message, guild, title, SimpleBot.getServerConfig().language().get(guild.getId()))));
            } else if (title.startsWith("warning.")) {
                embedBuilder.setColor(0xff7f00).setTitle(UnicodeCharacters.WARNING_SIGN_EMOJI + " " + (formatArgs != null ? String.format(translateMessage(author, message, guild, title, SimpleBot.getServerConfig().language().get(guild.getId())), formatArgs) : translateMessage(author, message, guild, title, SimpleBot.getServerConfig().language().get(guild.getId()))));
            }
        }
        if(description != null && description.length() <= 4096) embedBuilder.setDescription(description);
        if(thumbnail != null) embedBuilder.setThumbnail(thumbnail);
        return embedBuilder;
    }

    /**
     * @param string the string that will be shortened depending on the intDelimiter
     * @param intDelimiter the number of characters that will be displayed in the string - 3
     * @return the string shortened depending on the intDelimiter
     */
    public static String stringShortener(String string, int intDelimiter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < string.toCharArray().length; i++) {
            if (i == intDelimiter - 3) {
                stringBuilder.append("...");
                break;
            }
            stringBuilder.append(string.toCharArray()[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * @param date the date that will be formatted
     * @return the formatted date
     */
    public static String formatShortDate(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yy"));
    }

    /**
     * @param member the member that will be verified if the member can interact with the target
     * @param bot the bot that will be verified if the bot can interact with the target
     * @param target the target
     * @param event the event
     * @return {@code true} if the member and the bot can interact with the target, {@code false} otherwise
     */
    public static boolean cantInteract(Member member, Member bot, Member target, CommandEvent event) {
        if (member.canInteract(target) && bot.canInteract(target)) return false;
        event.reply(new MessageBuilder(getEmbed(event, !member.canInteract(target) ? "error.commands.cantInteract.member" : "error.commands.cantInteract.bot", null, null, null).build()).build());
        return true;
    }

    /**
     * @param key the localization key
     * @param event the event
     * @return the translated key in the guild's configured language
     */
    public static String translateMessage(@NotNull CommandEvent event, @NotNull String key) {
        return translateMessage(event.getAuthor(), event.getMessage(), event.getGuild(), key, SimpleBot.getServerConfig().language().get(event.getGuild().getId()));
    }

    /**
     * @param key the localization key
     * @param event used to get the guild's ID
     * @param lang the language where the key will be taken
     * @return the translated key in the specified language
     */
    public static String translateMessage(@NotNull CommandEvent event, @NotNull String key, @NotNull String lang) {
        return translateMessage(event.getAuthor(), event.getMessage(), event.getGuild(), key, lang);
    }

    /**
     * @param author used by the {@link net.thesimpleteam.simplebot.utils.MessageHelper#getEmbed(User, Message, Guild, String, Color, String, String, Object...)} function
     * @param message used to send the embedBuilder
     * @param guild used to get the configured language and to send the embedBuilder to the guild's owner's private channel if owner isn't null
     * @param key the localization key
     * @param lang the language where the key will be taken
     * @return the translated key in the specified language
     * @throws NullPointerException if the key does not exist in the specified language or in the English localization file
     */
    public static String translateMessage(@NotNull User author, @Nullable Message message, @NotNull Guild guild, @NotNull String key, @NotNull String lang) {
        if (Arrays.asList(SimpleBot.getLangs()).contains(lang)) return SimpleBot.getLocalizations().get(lang).get(key).getAsString();
        if (SimpleBot.getLocalizations().get("en").get(key) == null) {
            long skip = 2;
            if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(f -> f.skip(1).findFirst().orElseThrow()).getMethodName().equalsIgnoreCase("getHelpConsumer"))
                skip++;
            final long _skip = skip;
            EmbedBuilder embedBuilder = getEmbed(author, message, guild, "error.translateMessage.error", null, null, null, key)
                    .addField(translateMessage(author, message, guild, "error.translateMessage.key", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(guild.getId(), "en")).getAsString()), key, false)
                    .addField(translateMessage(author, message, guild, "error.translateMessage.class", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(guild.getId(), "en")).getAsString()), StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(translateMessage(author, message, guild, "error.translateMessage.method", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(guild.getId(), "en")).getAsString()), StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(translateMessage(author, message, guild, "error.translateMessage.lineNumber", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(guild.getId(), "en")).getAsString()), String.valueOf(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false);
            if (message != null)
                message.reply(new MessageBuilder(embedBuilder.build()).build()).queue();
            if (guild.getOwner() != null)
                guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(embedBuilder.build()).build()).queue());
            throw new NullPointerException("The key " + key + " does not exist!");
        }
        try {
            return SimpleBot.getLocalizations().get("en").get(key).getAsString();
        } catch (NullPointerException ex) {
            return key;
        }
    }
}