package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.config.ServerConfig;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.listener.Listener;
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
        Arrays.stream(ServerConfig.class.getDeclaredFields()).filter(Objects::isNull).forEach(config -> {
            try {
                new File("config/server-config.json").delete();
                Main.setupServerConfig();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
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
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.notConfigured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            Main.getServerConfig().guildJoinRole().remove(event.getGuild().getId());
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.joinRole.reset", null, null, null, (Object[]) null).build()).build());
                        } else {
                            if (args[1].replaceAll("\\D+", "").isEmpty()) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.IDIsInvalid", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            if (event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")) == null) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.roleNull", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            if (event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).isManaged()) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.roleManaged", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            if (Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null || !Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()).equals(event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).getId())) {
                                Main.getServerConfig().guildJoinRole().put(event.getGuild().getId(), event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).getId());
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.joinRole.configured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                        }
                    }
                    case "localization" -> {
                        if (Arrays.stream(Main.getLangs()).noneMatch(lang -> lang.equalsIgnoreCase(args[1]))) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.localization.languageDontExist", null, null, null, (Object[]) null).build()).build());
                            return;
                        }
                        if (args[1].equals(Main.getServerConfig().language().get(event.getGuild().getId()))) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.localization.sameAsConfig", null, null, null, (Object[]) null).build()).build());
                            return;
                        }
                        Main.getServerConfig().language().put(event.getGuild().getId(), args[1]);
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.localization.configured", null, null, null, new StringBuilder().append(":flag_").append(args[0].replace("en", "us: / :flag_gb")).append(":").toString()).build()).build());
                    }
                    case "setprefix" -> {
                        if (event.getArgs().split(" setprefix ")[1].isEmpty()) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.setPrefix.prefixIsEmpty", null, null, null, (Object[]) null).build()).build());
                            return;
                        }
                        if (args[1].equalsIgnoreCase("reset")) {
                            if (!Main.getServerConfig().prefix().containsKey(event.getGuild().getId())) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.setPrefix.notConfigured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            Main.getServerConfig().prefix().remove(event.getGuild().getId());
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.setPrefix.reset", null, null, null, (Object[]) null).build()).build());
                        } else {
                            if (args[1].equals(Main.getServerConfig().prefix().get(event.getGuild().getId()))) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.setPrefix.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            Main.getServerConfig().prefix().put(event.getGuild().getId(), args[1]);
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.setPrefix.configured", null, null, null, args[1]).build()).build());
                        }
                    }
                    default -> MessageHelper.syntaxError(event, this, "syntax.config");
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
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.join.notConfigured", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        Main.getServerConfig().channelMemberJoin().remove(event.getGuild().getId());
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.join.reset", null, null, null, event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getAsMention()).build()).build());
                                    }
                                    case "this" -> {
                                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !event.getChannel().getId().equals(event.getChannel().getId())) {
                                            Main.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), event.getChannel().getId());
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.join.configured", null, null, null, ((GuildChannel) event.getChannel()).getAsMention()).build()).build());
                                            return;
                                        }
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.join.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                    }
                                    default -> {
                                        if (args[2].replaceAll("\\D+", "").isEmpty()) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.join.IDIsInvalid", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        if (event.getGuild().getGuildChannelById(args[2].replaceAll("\\D+", "")) == null) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.join.channelNull", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()).equals(args[2].replaceAll("\\D+", ""))) {
                                            Main.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), args[2].replaceAll("\\D+", ""));
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.join.configured", null, null, null, ((GuildChannel) event.getChannel()).getAsMention()).build()).build());
                                            return;
                                        }
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.join.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                    }
                                }
                            }
                            case "leave" -> {
                                switch (args[2].toLowerCase(Locale.ROOT)){
                                    case "reset" -> {
                                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.notAsConfigured", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        Main.getServerConfig().channelMemberLeave().remove(event.getGuild().getId());
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.leave.reset", null, null, null, (Object[]) null).build()).build());
                                    }
                                    case "this" -> {
                                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(event.getChannel().getId())) {
                                            Main.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), event.getChannel().getId());
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.leave.configured", null, null, null, ((GuildChannel) event.getChannel()).getAsMention()).build()).build());
                                            return;
                                        }
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                    }
                                    default -> {
                                        if (args[2].replaceAll("\\D+", "").isEmpty()) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.IDIsInvalid", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        if (event.getGuild().getGuildChannelById(args[2].replaceAll("\\D+", "")) == null) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.channelNull", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(args[2].replaceAll("\\D+", ""))) {
                                            Main.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), args[2].replaceAll("\\D+", ""));
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.leave.configured", null, null, null, ((GuildChannel) event.getChannel()).getAsMention()).build()).build());
                                            return;
                                        }
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                    }
                                }
                            }
                            default -> MessageHelper.syntaxError(event, this, "syntax.config");
                        }
                    }
                    case "prohibitword" -> {
                        switch (args[1].toLowerCase(Locale.ROOT)) {
                            case "add" -> {
                                List<String> prohibitWords = Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords().get(event.getGuild().getId());
                                if (prohibitWords.contains(args[1])) {
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.prohibitWord.wordAlreadyHere", null, null, null, args[2]).build()).build());
                                    return;
                                }
                                prohibitWords.add(args[2]);
                                Main.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                                Main.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.prohibitWord.wordAdded", null, null, null, args[2]).build()).build());
                            }
                            case "remove" -> {
                                List<String> prohibitWords = Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords().get(event.getGuild().getId());
                                if (!prohibitWords.contains(args[1])) {
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.prohibitWord.wordNotHere", null, null, null, args[2]).build()).build());
                                    return;
                                }
                                prohibitWords.remove(args[2]);
                                Main.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                                Main.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.prohibitWord.wordRemoved", null, null, null, args[2]).build()).build());
                            }
                            case "reset" -> {
                                if (Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null) {
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.prohibitWord.listNull", null, null, null, (Object[]) null).build()).build());
                                    return;
                                }
                                Main.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.prohibitWord.listReseted", null, null, null, (Object[]) null).build()).build());
                            }
                            default -> MessageHelper.syntaxError(event, this, "syntax.config");
                        }
                    }
                    default -> MessageHelper.syntaxError(event, this, "syntax.config");
                }
            }
        }
    }
}
