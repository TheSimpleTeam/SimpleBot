package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        this.name = "config";
        this.cooldown = 5;
        this.help = "help.config";
        this.example = "example.config";
        this.aliases = new String[]{"cf", "parameter", "par"};
        this.arguments = "arguments.config";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMember().isOwner()){
            event.reply(new MessageBuilder(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.notOwner", event)))
                    .build()).build());
            return;
        }
        for(Map config : getManualConfigs()){
            if(config == null){
                try {
                    new File("config/server-config.json").delete();
                    Main.setupServerConfig();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                break;
            }
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, "syntax.config");
            return;
        }
        switch (args.length) {
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "joinrole" -> {
                        if (args[1].equalsIgnoreCase("reset")) {
                            if (Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null) {
                                EmbedBuilder errorNotConfiguredEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.notConfigured", event)));
                                event.reply(new MessageBuilder(errorNotConfiguredEmbed.build()).build());
                                return;
                            }
                            Main.getServerConfig().guildJoinRole().remove(event.getGuild().getId());
                            EmbedBuilder successResetEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.config.joinRole.reset", event)));
                            event.reply(new MessageBuilder(successResetEmbed.build()).build());
                        } else {
                            if (args[1].replaceAll("\\D+", "").isEmpty()) {
                                EmbedBuilder errorIDIsInvalidEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.IDIsInvalid", event)));
                                event.reply(new MessageBuilder(errorIDIsInvalidEmbed.build()).build());
                                return;
                            }
                            if (event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")) == null) {
                                EmbedBuilder errorRoleNullEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.roleNull", event)));
                                event.reply(new MessageBuilder(errorRoleNullEmbed.build()).build());
                                return;
                            }
                            if (event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).isManaged()) {
                                EmbedBuilder errorRoleManagedEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.roleManaged", event)));
                                event.reply(new MessageBuilder(errorRoleManagedEmbed.build()).build());
                                return;
                            }
                            if (Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null || !Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()).equals(event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).getId())) {
                                Main.getServerConfig().guildJoinRole().put(event.getGuild().getId(), event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).getId());
                                EmbedBuilder successConfiguredEmbed = new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.config.joinRole.configured", event)));
                                event.reply(new MessageBuilder(successConfiguredEmbed.build()).build());
                                return;
                            }
                            EmbedBuilder errorSameAsConfiguredEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.sameAsConfigured", event)));
                            event.reply(new MessageBuilder(errorSameAsConfiguredEmbed.build()).build());
                        }
                    }
                    case "localization" -> {
                        if (Arrays.stream(Main.getLangs()).noneMatch(lang -> lang.equalsIgnoreCase(args[1]))) {
                            EmbedBuilder errorLanguageDontExistEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.localization.languageDontExist", event)));
                            event.reply(new MessageBuilder(errorLanguageDontExistEmbed.build()).build());
                            return;
                        }
                        if (args[1].equals(Main.getServerConfig().language().get(event.getGuild().getId()))) {
                            EmbedBuilder errorSameAsConfiguredEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.localization.sameAsConfig", event)));
                            event.reply(new MessageBuilder(errorSameAsConfiguredEmbed.build()).build());
                            return;
                        }
                        Main.getServerConfig().language().put(event.getGuild().getId(), args[1]);
                        EmbedBuilder successEmbed = new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, String.format(MessageHelper.translateMessage("success.config.localization.configured", event))), String.format("%s%s", ":flag_" + args[0].replace("en", "us: / :flag_gb"), ':'));
                        event.reply(new MessageBuilder(successEmbed.build()).build());
                    }
                    case "setprefix" -> {
                        if (event.getArgs().split(" setprefix ")[1].isEmpty()) {
                            EmbedBuilder errorPrefixIsEmptyEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.setPrefix.prefixIsEmpty", event)));
                            event.reply(new MessageBuilder(errorPrefixIsEmptyEmbed.build()).build());
                            return;
                        }
                        if (args[1].equalsIgnoreCase("reset")) {
                            if (!Main.getServerConfig().prefix().containsKey(event.getGuild().getId())) {
                                EmbedBuilder errorPrefixNullEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setTimestamp(Instant.now())
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.setPrefix.notConfigured", event));
                                event.reply(new MessageBuilder(errorPrefixNullEmbed.build()).build());
                                return;
                            }
                            Main.getServerConfig().prefix().remove(event.getGuild().getId());
                            EmbedBuilder successResetEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.config.setPrefix.reset", event)));
                            event.reply(new MessageBuilder(successResetEmbed.build()).build());
                        } else {
                            if (args[1].equals(Main.getServerConfig().prefix().get(event.getGuild().getId()))) {
                                EmbedBuilder errorSameAsConfiguredEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setTimestamp(Instant.now())
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.setPrefix.sameAsConfigured", event));
                                event.reply(new MessageBuilder(errorSameAsConfiguredEmbed.build()).build());
                                return;
                            }
                            Main.getServerConfig().prefix().put(event.getGuild().getId(), args[1]);
                            EmbedBuilder successConfiguredEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTimestamp(Instant.now())
                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.setPrefix.configured", event), args[1]));
                            event.reply(new MessageBuilder(successConfiguredEmbed.build()).build());
                        }
                    }
                }
            }
            case 3 -> {
                switch(args[0].toLowerCase(Locale.ROOT)){
                    case "channelmember" -> {
                        switch (args[1].toLowerCase(Locale.ROOT)){
                            case "join" -> {
                                switch (args[2].toLowerCase(Locale.ROOT)){
                                    case "reset" -> {
                                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null) {
                                            EmbedBuilder errorJoinNotConfiguredEmbed = new EmbedBuilder()
                                                    .setColor(Color.RED)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.join.notConfigured", event));
                                            event.reply(new MessageBuilder(errorJoinNotConfiguredEmbed.build()).build());
                                            return;
                                        }
                                        Main.getServerConfig().channelMemberJoin().remove(event.getGuild().getId());
                                        EmbedBuilder successJoinResetEmbed = new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setTimestamp(Instant.now())
                                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.channelMember.join.reset", event), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getAsMention()));
                                        event.reply(new MessageBuilder(successJoinResetEmbed.build()).build());
                                    }
                                    case "this" -> {
                                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !event.getChannel().getId().equals(event.getChannel().getId())) {
                                            Main.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), event.getChannel().getId());
                                            EmbedBuilder successJoinConfiguredEmbed = new EmbedBuilder()
                                                    .setColor(Color.GREEN)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.channelMember.join.configured", event), ((GuildChannel) event.getChannel()).getAsMention()));
                                            event.reply(new MessageBuilder(successJoinConfiguredEmbed.build()).build());
                                            return;
                                        }
                                        EmbedBuilder errorJoinSameAsConfiguredEmbed = new EmbedBuilder()
                                                .setColor(Color.RED)
                                                .setTimestamp(Instant.now())
                                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.join.sameAsConfigured", event));
                                        event.reply(new MessageBuilder(errorJoinSameAsConfiguredEmbed.build()).build());
                                    }
                                    default -> {
                                        if (args[2].replaceAll("\\D+", "").isEmpty()) {
                                            EmbedBuilder errorJoinIDIsInvalidEmbed = new EmbedBuilder()
                                                    .setColor(Color.RED)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.join.IDIsInvalid", event));
                                            event.reply(new MessageBuilder(errorJoinIDIsInvalidEmbed.build()).build());
                                            return;
                                        }
                                        if (event.getGuild().getGuildChannelById(args[2].replaceAll("\\D+", "")) == null) {
                                            EmbedBuilder errorJoinChannelNullEmbed = new EmbedBuilder()
                                                    .setColor(Color.RED)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.join.channelNull", event));
                                            event.reply(new MessageBuilder(errorJoinChannelNullEmbed.build()).build());
                                            return;
                                        }
                                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()).equals(args[2].replaceAll("\\D+", ""))) {
                                            Main.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), args[2].replaceAll("\\D+", ""));
                                            EmbedBuilder successJoinConfiguredEmbed = new EmbedBuilder()
                                                    .setColor(Color.GREEN)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.channelMember.join.configured", event), ((GuildChannel) event.getChannel()).getAsMention()));
                                            event.reply(new MessageBuilder(successJoinConfiguredEmbed.build()).build());
                                            return;
                                        }
                                        EmbedBuilder errorJoinSameAsConfiguredEmbed = new EmbedBuilder()
                                                .setColor(Color.RED)
                                                .setTimestamp(Instant.now())
                                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.join.sameAsConfigured", event));
                                        event.reply(new MessageBuilder(errorJoinSameAsConfiguredEmbed.build()).build());
                                    }
                                }
                            }
                            case "leave" -> {
                                switch (args[2].toLowerCase(Locale.ROOT)){
                                    case "reset" -> {
                                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null) {
                                            EmbedBuilder errorLeaveNotConfiguredEmbed = new EmbedBuilder()
                                                    .setColor(Color.RED)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.leave.notConfigured", event));
                                            event.reply(new MessageBuilder(errorLeaveNotConfiguredEmbed.build()).build());
                                            return;
                                        }
                                        Main.getServerConfig().channelMemberLeave().remove(event.getGuild().getId());
                                        EmbedBuilder successLeaveResetEmbed = new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setTimestamp(Instant.now())
                                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.config.channelMember.leave.reset", event));
                                        event.reply(new MessageBuilder(successLeaveResetEmbed.build()).build());
                                    }
                                    case "this" -> {
                                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(event.getChannel().getId())) {
                                            Main.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), event.getChannel().getId());
                                            EmbedBuilder successLeaveConfiguredEmbed = new EmbedBuilder()
                                                    .setColor(Color.GREEN)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.channelMember.leave.reset", event), String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), ((GuildChannel) event.getChannel()).getAsMention())));
                                            event.reply(new MessageBuilder(successLeaveConfiguredEmbed.build()).build());
                                            return;
                                        }
                                        EmbedBuilder errorLeaveSameAsConfiguredEmbed = new EmbedBuilder()
                                                .setColor(Color.RED)
                                                .setTimestamp(Instant.now())
                                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.leave.sameAsConfigured", event));
                                        event.reply(new MessageBuilder(errorLeaveSameAsConfiguredEmbed.build()).build());
                                    }
                                    default -> {
                                        if (args[2].replaceAll("\\D+", "").isEmpty()) {
                                            EmbedBuilder errorLeaveIDIsInvalidEmbed = new EmbedBuilder()
                                                    .setColor(Color.RED)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.leave.IDIsInvalid", event));
                                            event.reply(new MessageBuilder(errorLeaveIDIsInvalidEmbed.build()).build());
                                            return;
                                        }
                                        if (event.getGuild().getGuildChannelById(args[2].replaceAll("\\D+", "")) == null) {
                                            EmbedBuilder errorLeaveChannelNullEmbed = new EmbedBuilder()
                                                    .setColor(Color.RED)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.leave.channelNull", event));
                                            event.reply(new MessageBuilder(errorLeaveChannelNullEmbed.build()).build());
                                            return;
                                        }
                                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(args[2].replaceAll("\\D+", ""))) {
                                            Main.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), args[2].replaceAll("\\D+", ""));
                                            EmbedBuilder successLeaveConfiguredEmbed = new EmbedBuilder()
                                                    .setColor(Color.GREEN)
                                                    .setTimestamp(Instant.now())
                                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.channelMember.leave.configured", event), ((GuildChannel) event.getChannel()).getAsMention()));
                                            event.reply(new MessageBuilder(successLeaveConfiguredEmbed.build()).build());
                                            return;
                                        }
                                        EmbedBuilder errorLeaveSameAsConfiguredEmbed = new EmbedBuilder()
                                                .setColor(Color.RED)
                                                .setTimestamp(Instant.now())
                                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                                .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.channelMember.leave.sameAsConfigured", event));
                                        event.reply(new MessageBuilder(errorLeaveSameAsConfiguredEmbed.build()).build());
                                    }
                                }
                            }
                        }
                    }
                    case "prohibitword" -> {
                        switch (args[1].toLowerCase(Locale.ROOT)) {
                            case "add":
                                List<String> prohibitWords = Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords().get(event.getGuild().getId());
                                if(prohibitWords.contains(args[1])){
                                    EmbedBuilder errorWordAlreadyHereEmbed = new EmbedBuilder()
                                            .setColor(Color.RED)
                                            .setTimestamp(Instant.now())
                                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                            .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.config.prohibitWord.wordAlreadyHere", event), args[2]));
                                    event.reply(new MessageBuilder(errorWordAlreadyHereEmbed.build()).build());
                                    return;
                                }
                                prohibitWords.add(args[2]);
                                Main.getServerConfig().prohibitWords().clear();
                                Main.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                                EmbedBuilder successWordAddedEmbed = new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setTimestamp(Instant.now())
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.prohibitWord.wordAdded", event), args[2]));
                                event.reply(new MessageBuilder(successWordAddedEmbed.build()).build());
                                break;
                            case "remove":
                                prohibitWords = Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords().get(event.getGuild().getId());
                                if(!prohibitWords.contains(args[1])){
                                    EmbedBuilder errorWordNotHereEmbed = new EmbedBuilder()
                                            .setColor(Color.RED)
                                            .setTimestamp(Instant.now())
                                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                            .setTitle(UnicodeCharacters.crossMarkEmoji + " " + String.format(MessageHelper.translateMessage("error.config.prohibitWord.wordNotHere", event), args[2]));
                                    event.reply(new MessageBuilder(errorWordNotHereEmbed.build()).build());
                                    return;
                                }
                                prohibitWords.remove(args[2]);
                                Main.getServerConfig().prohibitWords().clear();
                                Main.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                                EmbedBuilder successWordRemovedEmbed = new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setTimestamp(Instant.now())
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.config.prohibitWord.wordRemoved", event), args[2]));
                                event.reply(new MessageBuilder(successWordRemovedEmbed.build()).build());
                                break;
                            case "reset":
                                if (Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null) {
                                    EmbedBuilder errorListNullEmbed = new EmbedBuilder()
                                            .setColor(Color.RED)
                                            .setTimestamp(Instant.now())
                                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                            .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.config.prohibitWord.listNull", event));
                                    event.reply(new MessageBuilder(errorListNullEmbed.build()).build());
                                    return;
                                }
                                Main.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                                EmbedBuilder successListResetedEmbed = new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setTimestamp(Instant.now())
                                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.config.prohibitWord.listReseted", event));
                                event.reply(new MessageBuilder(successListResetedEmbed.build()).build());
                                break;
                            default:
                                MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.prohibit", event));
                                break;
                        }
                    }
                }
            }
        }
    }

    private static Map[] getManualConfigs(){ // Returns an array of maps that are configurations that can only be changed by command and by the server's owner
        return new Map[]{Main.getServerConfig().channelMemberJoin(), Main.getServerConfig().channelMemberLeave(), Main.getServerConfig().language(), Main.getServerConfig().prefix(), Main.getServerConfig().guildJoinRole(), Main.getServerConfig().prohibitWords()};
    }
}
