package net.thesimpleteam.simplebot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.config.ServerConfig;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.listeners.Listener;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;

import javax.management.ReflectionException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
        this.category = CommandCategories.UTILITY.category;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
    }

    private enum JsonType {
        ARRAY,
        STRING,
        BOOLEAN,
        INT,
        OBJECT;

        private static JsonType getFromType(Class<?> type) {
            try {
                if(type.isAssignableFrom(List.class)) {
                    return ARRAY;
                } else if(type == String.class) {
                    return STRING;
                } else if(type.isAssignableFrom(Number.class)) {
                    return INT;
                }else if(type == Boolean.class) {
                    return BOOLEAN;
                } else {
                    return OBJECT;
                }
            } catch (Exception e) {
                return OBJECT;
            }
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        /**
         * @author minemobs
         * bad code btw
         */
        List<Field> config = Arrays.stream(ServerConfig.class.getDeclaredFields()).filter(f -> {
            try {
                f.trySetAccessible();
                return f.get(SimpleBot.getServerConfig()) == null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).toList();
        if (!config.isEmpty()) {
            Path p = Path.of("config/server-config.json");
            try(BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
                JsonObject o = SimpleBot.gson.fromJson(SimpleBot.gson.toJson(SimpleBot.getServerConfig()), JsonObject.class);
                config.forEach(c -> {
                    Object obj = switch (JsonType.getFromType(c.getType())) {
                        case ARRAY -> new Object[0];
                        case STRING -> "something";
                        case INT -> Byte.MAX_VALUE;
                        case OBJECT -> new Object();
                        case BOOLEAN -> true;
                    };
                    o.add(c.getName(), SimpleBot.gson.fromJson(SimpleBot.gson.toJson(obj), JsonObject.class));
                });
                System.out.println(o.toString());
                bw.write(SimpleBot.gson.toJson(o));
                try {
                    var sc = SimpleBot.class.getDeclaredField("serverConfig");
                    sc.trySetAccessible();
                    sc.set(null, SimpleBot.gson.fromJson(o, ServerConfig.class));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                e.printStackTrace();
                MessageHelper.sendError(e, event, this);
                return;
            }
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, "information.config");
            return;
        }
        switch (args.length) {
            case 2 -> {
                switch (args[0].toLowerCase(Locale.ROOT)) {
                    case "joinrole" -> {
                        if (args[1].equalsIgnoreCase("reset")) {
                            if (SimpleBot.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.notConfigured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            SimpleBot.getServerConfig().guildJoinRole().remove(event.getGuild().getId());
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
                            if (SimpleBot.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null || !SimpleBot.getServerConfig().guildJoinRole().get(event.getGuild().getId()).equals(event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).getId())) {
                                SimpleBot.getServerConfig().guildJoinRole().put(event.getGuild().getId(), event.getGuild().getRoleById(args[1].replaceAll("\\D+", "")).getId());
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.joinRole.configured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.joinRole.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                        }
                    }
                    case "localization" -> {
                        if (Arrays.stream(SimpleBot.getLangs()).noneMatch(lang -> lang.equalsIgnoreCase(args[1]))) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.localization.languageDontExist", null, null, null, (Object[]) null).build()).build());
                            return;
                        }
                        if (args[1].equals(SimpleBot.getServerConfig().language().get(event.getGuild().getId()))) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.localization.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                            return;
                        }
                        SimpleBot.getServerConfig().language().put(event.getGuild().getId(), args[1]);
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.localization.configured", null, null, null, ":flag_" + args[1].replace("en", "us: / :flag_gb") + ":").build()).build());
                    }
                    case "setprefix" -> {
                        if (event.getArgs().split(" setprefix ")[0].isEmpty()) {
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.setPrefix.prefixIsEmpty", null, null, null, (Object[]) null).build()).build());
                            return;
                        }
                        if (args[1].equalsIgnoreCase("reset")) {
                            if (!SimpleBot.getServerConfig().prefix().containsKey(event.getGuild().getId())) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.setPrefix.notConfigured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            SimpleBot.getServerConfig().prefix().remove(event.getGuild().getId());
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.setPrefix.reset", null, null, null, (Object[]) null).build()).build());
                        } else {
                            if (args[1].equals(SimpleBot.getServerConfig().prefix().get(event.getGuild().getId()))) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.setPrefix.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            SimpleBot.getServerConfig().prefix().put(event.getGuild().getId(), args[1]);
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.setPrefix.configured", null, null, null, args[1]).build()).build());
                        }
                    }
                    case "prohibitword" -> {
                        //I fixed his issue, bc he's dumb like he has 2 braincells left.
                        // - Minemobs
                        if(args[1].equalsIgnoreCase("reset")) {
                            if (SimpleBot.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null) {
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.prohibitWord.listNull", null, null, null, (Object[]) null).build()).build());
                                return;
                            }
                            SimpleBot.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.prohibitWord.listReseted", null, null, null, (Object[]) null).build()).build());
                            break;
                        }
                        MessageHelper.syntaxError(event, this, "information.config");
                    }
                    default -> MessageHelper.syntaxError(event, this, "information.config");
                }
            }
            case 3 -> {
                switch(args[0].toLowerCase(Locale.ROOT)){
                    case "channelmember" -> {
                        switch (args[1].toLowerCase(Locale.ROOT)){
                            case "join" -> {
                                switch (args[2].toLowerCase(Locale.ROOT)){
                                    case "reset" -> {
                                        if (SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.join.notConfigured", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        SimpleBot.getServerConfig().channelMemberJoin().remove(event.getGuild().getId());
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.join.reset", null, null, null, event.getGuild().getGuildChannelById(SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getAsMention()).build()).build());
                                    }
                                    case "this" -> {
                                        if (SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !event.getChannel().getId().equals(event.getChannel().getId())) {
                                            SimpleBot.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), event.getChannel().getId());
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
                                        if (SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId()).equals(args[2].replaceAll("\\D+", ""))) {
                                            SimpleBot.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), args[2].replaceAll("\\D+", ""));
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
                                        if (SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null) {
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.notAsConfigured", null, null, null, (Object[]) null).build()).build());
                                            return;
                                        }
                                        SimpleBot.getServerConfig().channelMemberLeave().remove(event.getGuild().getId());
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.leave.reset", null, null, null, (Object[]) null).build()).build());
                                    }
                                    case "this" -> {
                                        if (SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(event.getChannel().getId())) {
                                            SimpleBot.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), event.getChannel().getId());
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
                                        if (SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(args[2].replaceAll("\\D+", ""))) {
                                            SimpleBot.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), args[2].replaceAll("\\D+", ""));
                                            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.channelMember.leave.configured", null, null, null, ((GuildChannel) event.getChannel()).getAsMention()).build()).build());
                                            return;
                                        }
                                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.channelMember.leave.sameAsConfigured", null, null, null, (Object[]) null).build()).build());
                                    }
                                }
                            }
                            default -> MessageHelper.syntaxError(event, this, "information.config");
                        }
                    }
                    case "prohibitword" -> {
                        switch (args[1].toLowerCase(Locale.ROOT)) {
                            case "add" -> {
                                List<String> prohibitWords = SimpleBot.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() : SimpleBot.getServerConfig().prohibitWords().get(event.getGuild().getId());
                                if (prohibitWords.contains(args[2])) {
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.prohibitWord.wordAlreadyHere", null, null, null, args[2]).build()).build());
                                    return;
                                }
                                prohibitWords.add(args[2]);
                                SimpleBot.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                                SimpleBot.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.prohibitWord.wordAdded", null, null, null, args[2]).build()).build());
                            }
                            case "remove" -> {
                                List<String> prohibitWords = SimpleBot.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() : SimpleBot.getServerConfig().prohibitWords().get(event.getGuild().getId());
                                if (!prohibitWords.contains(args[2])) {
                                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.config.prohibitWord.wordNotHere", null, null, null, args[2]).build()).build());
                                    return;
                                }
                                prohibitWords.remove(args[2]);
                                SimpleBot.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                                SimpleBot.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.config.prohibitWord.wordRemoved", null, null, null, args[2]).build()).build());
                            }
                            default -> MessageHelper.syntaxError(event, this, "information.config");
                        }
                    }
                    default -> MessageHelper.syntaxError(event, this, "information.config");
                }
            }
        }
        try {
            Listener.saveConfigs();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
