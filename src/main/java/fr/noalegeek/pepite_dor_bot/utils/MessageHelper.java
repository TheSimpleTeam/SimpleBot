package fr.noalegeek.pepite_dor_bot.utils;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
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
            argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.argumentsNull"));
        else if (!command.getArguments().startsWith("arguments."))
            argumentsBuilder.append(command.getArguments());
        else {
            if (translateMessage(event, command.getArguments()).split("²").length == 1) {
                argumentsBuilder.append(translateMessage(event, command.getArguments()));
            } else {
                int loop = 1;
                for (String arg : Arrays.stream(translateMessage(event, command.getArguments()).split("²")).toList()) {
                    loop = Math.max(loop, arg.split(">").length);
                }
                int indexList = 1;
                for (int length = 1; length <= loop; length++) {
                    int finalLenght = length;
                    if (Arrays.stream(translateMessage(event, command.getArguments()).split("²")).anyMatch(arguments -> arguments.split(">").length == finalLenght)) {
                        if (!Arrays.stream(translateMessage(event, command.getArguments()).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLenght).toList().isEmpty()) {
                            argumentsBuilder.append("__");
                            switch (finalLenght) {
                                case 1 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.oneArgument"));
                                case 2 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.twoArguments"));
                                case 3 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.threeArguments"));
                                case 4 -> argumentsBuilder.append(translateMessage(event, "error.commands.syntaxError.arguments.fourArguments"));
                                default -> argumentsBuilder.append("The devs forgotten to add the syntax with the length of ").append(finalLenght);
                            }
                            argumentsBuilder.append("__").append("\n\n");
                            for (int index = 0; index < Arrays.stream(translateMessage(event, command.getArguments()).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLenght).toList().size(); index++) {
                                argumentsBuilder.append(Arrays.stream(translateMessage(event, command.getArguments()).split("²")).filter(arguments -> arguments != null && arguments.split(">").length == finalLenght).toList().get(index)).append(" **->** *").append(translateMessage(event, command.getHelp()).split("²")[indexList]).append("*\n");
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
            examples = Arrays.toString(Stream.of(translateMessage(event, command.getExample()).split("²")).map(example -> example = Main.getPrefix(event.getGuild()) + command.getName() + " " + example).toArray()).replace("[", "").replace("]", "").replace(",", "");
        else
            examples = Arrays.toString(Stream.of(command.getExample().split("²")).map(example -> example = Main.getPrefix(event.getGuild()) + command.getName() + " " + example).toArray()).replace("[", "").replace("]", "").replace(",", "");
        EmbedBuilder embedBuilder = getEmbed(event, "error.commands.syntaxError.syntaxError", null, null, null, command.getName())
                .addField(command.getArguments().startsWith("arguments.") ? translateMessage(event, command.getArguments()).split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.arguments.argument") : translateMessage(event, "error.commands.syntaxError.arguments.arguments") : command.getArguments().split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.arguments.argument") : translateMessage(event, "error.commands.syntaxError.arguments.arguments"), argumentsBuilder.toString(), false)
                .addField(translateMessage(event, "error.commands.syntaxError.help"), command.getHelp() == null || command.getHelp().isEmpty() ? translateMessage(event, "error.commands.syntaxError.help.helpNull") : translateMessage(event, command.getHelp()).contains("²") ? translateMessage(event, command.getHelp()).split("²")[0] : translateMessage(event, command.getHelp()), false)
                .addField(command.getExample().startsWith("example.") ? translateMessage(event, command.getExample()).split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.examples.example") : translateMessage(event, "error.commands.syntaxError.examples.examples") : command.getExample().split("²").length == 1 ? translateMessage(event, "error.commands.syntaxError.examples.example") : translateMessage(event, "error.commands.syntaxError.examples.examples"), examples, false);
        if (informations != null) {
            if (translateMessage(event, informations).length() > 1024) {
                int field = 0;
                StringBuilder informationsBuilder = new StringBuilder();
                for (char character : informations.toCharArray()) {
                    informationsBuilder.append(character);
                    if(character == '²') {
                        field++;
                        embedBuilder.addField(field == 1 ? translateMessage(event, "error.commands.syntaxError.informations") : "", informationsBuilder.toString(), false);
                    }
                }
            } else
                embedBuilder.addField(translateMessage(event, "error.commands.syntaxError.informations"), translateMessage(event, informations), false);
        }
        //TODO [REMINDER] When all syntaxError of commands are translated, remove the informations lambda thing and add "translateMessage(informations, event)"
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }

    public static void sendError(Exception exception, CommandEvent event, Command command) {
        EmbedBuilder embedBuilder = getEmbed(event, "error.commands.sendError.error", null, null, null, (Object[]) null)
                .addField(translateMessage(event, "error.commands.sendError.sendError"), exception.getMessage(), false)
                .addField(translateMessage(event, "error.commands.sendError.command"), Main.getPrefix(event.getGuild()) + command.getName(), false);
        if (command.getArguments() == null || command.getArguments().isEmpty()) embedBuilder.addField(translateMessage(event, "error.commands.sendError.arguments"), event.getArgs(), false);
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }

    public static EmbedBuilder getEmbed(CommandEvent event, String title, @Nullable Color color, @Nullable String description, @Nullable String thumbnail, @Nullable Object... formatArgs){
        return getEmbed(event.getAuthor(), event.getTextChannel(), event.getGuild(), title, color, description, thumbnail, formatArgs);
    }

    public static EmbedBuilder getEmbed(@NotNull User author, @Nullable TextChannel channel, @NotNull Guild guild, @NotNull String title, @Nullable Color color, @Nullable String description, @Nullable String thumbnail, @Nullable Object... formatArgs){
        EmbedBuilder embedBuilder = new EmbedBuilder().setTimestamp(Instant.now()).setFooter(getTag(author), author.getEffectiveAvatarUrl());
        if(title.startsWith("success.")){
            embedBuilder.setColor(Color.GREEN).setTitle(new StringBuilder().append(UnicodeCharacters.whiteHeavyCheckMarkEmoji).append(" ").append(formatArgs != null ? String.format(translateMessage(author, channel, guild, title), formatArgs) : translateMessage(author, channel, guild, title)).toString());
        } else if(title.startsWith("error.")){
            embedBuilder.setColor(Color.RED).setTitle(new StringBuilder().append(UnicodeCharacters.crossMarkEmoji).append(" ").append(formatArgs != null ? String.format(translateMessage(author, channel, guild, title), formatArgs) : translateMessage(author, channel, guild, title)).toString());
        } else if(title.startsWith("warning.")){
            embedBuilder.setColor(0xff7f00).setTitle(new StringBuilder().append(UnicodeCharacters.warningSignEmoji).append(" ").append(formatArgs != null ? String.format(translateMessage(author, channel, guild, title), formatArgs) : translateMessage(author, channel, guild, title)).toString());
        }
        if(color != null) embedBuilder.setColor(color);
        if(description != null && description.length() <= 4096) embedBuilder.setDescription(description);
        if(thumbnail != null) embedBuilder.setThumbnail(thumbnail);
        return embedBuilder;
    }

    public static String formatShortDate(OffsetDateTime date) {
        return date.getDayOfMonth() + "/" + (date.getMonthValue() < 10 ? "0" + date.getMonthValue(): date.getMonthValue()) + "/" + date.getYear();
    }

    public static boolean cantInteract(Member member, Member bot, Member target, CommandEvent event) {
        if (member.canInteract(target) && bot.canInteract(target)) return false;
        event.reply(new MessageBuilder(getEmbed(event, !member.canInteract(target) ? "error.commands.cantInteract.member" : "error.commands.cantInteract.bot", null, null, null, (Object[]) null).build()).build());
        return true;
    }

    /**
     * @param key the localization key
     * @param event for getting the guild's ID
     * @return the translated value
     * @throws NullPointerException if the key does not exist in any localization files
     */
    public static String translateMessage(@NotNull CommandEvent event, @NotNull String key) {
        return translateMessage(event.getAuthor(), event.getTextChannel(), event.getGuild(), key);
    }

    public static String translateMessage(@NotNull User author, @Nullable TextChannel channel, @NotNull Guild guild, @NotNull String key) {
        if (Optional.ofNullable(Main.getLocalizations().get(Main.getServerConfig().language().getOrDefault(guild.getId(), "en")).get(key)).isPresent()) return Optional.ofNullable(Main.getLocalizations().get(Main.getServerConfig().language().getOrDefault(guild.getId(), "en")).get(key)).get().getAsString();
        if (Main.getLocalizations().get("en").get(key) == null) {
            long skip = 2;
            if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(f -> f.skip(1).findFirst().orElseThrow()).getMethodName().equalsIgnoreCase("getHelpConsumer"))
                skip++;
            final long _skip = skip;
            EmbedBuilder embedBuilder = getEmbed(author, channel, guild, "error.translateMessage.error", null, null, null, key)
                    .addField(translateMessage(author, channel, guild, "error.translateMessage.key"), key, false)
                    .addField(translateMessage(author, channel, guild, "error.translateMessage.class"), StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getDeclaringClass().getSimpleName(), false)
                    .addField(translateMessage(author, channel, guild, "error.translateMessage.method"), StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getMethodName(), false)
                    .addField(translateMessage(author, channel, guild, "error.translateMessage.lineNumber"), String.valueOf(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(stackFrameStream -> stackFrameStream.skip(_skip).findFirst().orElseThrow()).getLineNumber()), false);
            if (channel != null)
                channel.sendMessage(new MessageBuilder(embedBuilder.build()).build()).queue();
            if (guild.getOwner() != null)
                guild.getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(embedBuilder.build()).build()).queue());
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