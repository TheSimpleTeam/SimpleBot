package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.config.ServerConfig;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

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
            MessageHelper.syntaxError(event, this, null);
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
                                EmbedBuilder errorIDIsEmptyEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.IDIsEmpty", event)));
                                event.reply(new MessageBuilder(errorIDIsEmptyEmbed.build()).build());
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
                                        .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.setPrefix.sameAsConfigured", event));
                                event.reply(new MessageBuilder(errorSameAsConfiguredEmbed.build()).build());
                                return;
                            }
                            Main.getServerConfig().prefix().put(event.getGuild().getId(), args[1]);
                            EmbedBuilder successConfiguredEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setTimestamp(Instant.now())
                                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.setPrefix.configured", event), args[1]));
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
                                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.notConfigured", event));
                                            return;
                                        }
                                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.join.reset", event), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getAsMention()));
                                        Main.getServerConfig().channelMemberJoin().remove(event.getGuild().getId());
                                    }
                                }
                            }
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
