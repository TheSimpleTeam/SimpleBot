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
import java.util.*;
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
                int loop = 1;
                for (String arg : Arrays.stream(translateMessage(command.getArguments(), event).split("²")).toList()) {
                    loop = Math.max(loop, arg.split(">").length);
                }
                int indexList = 1;
                for (int length = 1; length <= loop; length++) {
                    int finalLenght = length;
                    if (Arrays.stream(translateMessage(command.getArguments(), event).split("²")).anyMatch(arguments -> arguments.split(">").length == finalLenght)) {
                        if (!Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLenght).toList().isEmpty()) {
                            argumentsBuilder.append("__");
                            switch (finalLenght) {
                                case 1 -> argumentsBuilder.append(translateMessage("text.commands.syntaxError.arguments.oneArgument", event));
                                case 2 -> argumentsBuilder.append(translateMessage("text.commands.syntaxError.arguments.twoArguments", event));
                                case 3 -> argumentsBuilder.append(translateMessage("text.commands.syntaxError.arguments.threeArguments", event));
                                case 4 -> argumentsBuilder.append(translateMessage("text.commands.syntaxError.arguments.fourArguments", event));
                                default -> argumentsBuilder.append("The devs forgotten to add the syntax with the length of ").append(finalLenght);
                            }
                            argumentsBuilder.append("__").append("\n\n");
                            for (int index = 0; index < Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLenght).toList().size(); index++) {
                                argumentsBuilder.append(Arrays.stream(translateMessage(command.getArguments(), event).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLenght).toList().get(index)).append(" **->** *").append(translateMessage(command.getHelp(), event).split("²")[indexList]).append("*\n");
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
        if (informations != null) {
            if (translateMessage(informations, event).length() > 1024) {
                int field = 0;
                StringBuilder informationsBuilder = new StringBuilder();
                for (char character : informations.toCharArray()) {
                    informationsBuilder.append(character);
                    if(character == '²') {
                        field++;
                        syntaxErrorEmbed.addField(field == 1 ? translateMessage("text.commands.syntaxError.informations", event) : "", informationsBuilder.toString(), false);
                    }
                }
            } else
                syntaxErrorEmbed.addField(translateMessage("text.commands.syntaxError.informations", event), translateMessage(informations, event), false);
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

    /**
     * @param key the localization key
     * @param event for getting the guild's ID
     * @return the translated value
     * @throws NullPointerException if the key does not exist in any localization files
     */
    public static String translateMessage(@NotNull String key, @NotNull CommandEvent event) {
        return translateMessage(key, event.getAuthor(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getTextChannel(), event.getGuild());
    }

    public static String translateMessage(@NotNull String key, @NotNull User author, @Nullable User owner, @NotNull Guild guild) {
        return translateMessage(key, author, owner, null, guild);
    }

    private static String translateMessage(@NotNull String key, @NotNull User author, @Nullable User owner, @Nullable TextChannel channel, @NotNull Guild guild) {
        if (Optional.ofNullable(Main.getLocalizations().get(Main.getServerConfig().language().getOrDefault(guild.getId(), "en")).get(key)).isPresent()) return Optional.ofNullable(Main.getLocalizations().get(Main.getServerConfig().language().getOrDefault(guild.getId(), "en")).get(key)).get().getAsString();
        if (Main.getLocalizations().get("en").get(key) == null) {
            long skip = 2;
            if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(f -> f.skip(1).findFirst().orElseThrow()).getMethodName().equalsIgnoreCase("getHelpConsumer"))
                skip++;
            final long _skip = skip;
            EmbedBuilder errorKeyNullEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(author), author.getEffectiveAvatarUrl())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.translateMessage.error", author, owner, channel, guild), key))
                    .addField(MessageHelper.translateMessage("error.translateMessage.key", author, owner, channel, guild), key, false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.class", author, owner, channel, guild), StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.method", author, owner, channel, guild), StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(MessageHelper.translateMessage("error.translateMessage.lineNumber", author, owner, channel, guild), String.valueOf(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false);
            if (channel != null)
                channel.sendMessage(new MessageBuilder(errorKeyNullEmbed.build()).build()).queue();
            if (owner != null)
                owner.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(errorKeyNullEmbed.build()).build()).queue());
            throw new NullPointerException("The key " + key + " does not exist!");
        }
        try {
            return Main.getLocalizations().get("en").get(key).getAsString();
        } catch (NullPointerException ex) {
            return key;
        }
    }

    /**
     * @param key the localization key
     * @return a String list with all the translations of the key
     * @throws NullPointerException if the key does not exist in a language
     */
    public static List<String> translateMessageAllLanguages(@NotNull String key){
        List<String> listMessageTranslated = new ArrayList<>();
        for(String lang : Main.getLangs()){
            if(Main.getLocalizations().get(lang).get(key) == null){
                throw new NullPointerException("The key " + key + " doesn't exist in the language " + lang);
            }
            listMessageTranslated.add(Main.getLocalizations().get(lang).get(key).getAsString());
        }
        return listMessageTranslated;
    }
}