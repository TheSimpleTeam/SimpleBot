package fr.noalegeek.pepite_dor_bot.listener;

import com.google.common.base.Throwables;
import com.jagrosh.jdautilities.command.Command;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.config.ServerConfig;
import fr.noalegeek.pepite_dor_bot.utils.LevenshteinDistance;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Listener extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            Listener.saveConfigs();
        } catch (IOException ex) {
            Main.LOGGER.severe(Throwables.getStackTraceAsString(ex));
        }
        Main.getExecutorService().schedule(() -> System.exit(0), 3, TimeUnit.SECONDS); //JDA doesn't want to exit the JVM so we do a System.exit()
    }

    public static void saveConfigs() throws IOException {
        if (!new File(new File("config/server-config.json").toPath().toUri()).exists())
            new File(new File("config/server-config.json").toPath().toUri()).createNewFile();
        if (Main.gson.fromJson(Files.newBufferedReader(new File("config/server-config.json").toPath(), StandardCharsets.UTF_8), ServerConfig.class) == Main.getServerConfig())
            return;
        Writer writer = Files.newBufferedWriter(new File("config/server-config.json").toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Main.gson.toJson(Main.getServerConfig(), writer);
        writer.close();
        Main.LOGGER.info("Server config updated");
    }

    //TODO New config for onGuildMemberJoin/Leave : a boolean that active the member join/leave embed into the system channel (Called = systemConfigJoin)
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (Main.getServerConfig().channelMemberJoin().containsKey(event.getGuild().getId()) && event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())) != null) { // Verifies if the server's owner configured the channelMemberJoin config and if the configured channel exists, if all is true it try catch the InsufficientPermissionException to verifies if the bot hasn't the permission to write in the configured channel, if false it verifies if the configured (on Discord) system channel exists.
            try { // Try catch the InsufficientPermissionException to verifies if the bot hasn't the permission to write in the configured channel, if it catches it verifies if the owner is null.
                event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).sendMessage(new MessageBuilder(new EmbedBuilder() // Sends a memberJoin embed in the configured channel
                        .setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl())
                        .setTitle(String.format(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.memberJoin", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getEffectiveName(), event.getGuild().getName()))
                        .addField(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.member", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getAsMention(), false)
                        .addField(String.format("%s %s", UnicodeCharacters.heavyPlusSign, MessageHelper.translateMessage("success.listener.onGuildMemberJoin.newMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild())), String.format(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.countMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getGuild().getMemberCount()), false)
                        .setTimestamp(Instant.now())
                        .setColor(Color.GREEN)
                        .build()).build()).queue();
            } catch (InsufficientPermissionException exception) {
                if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the channelMemberJoinHasntPermission embed in the server's owner's private channel.
                    return;
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the channelMemberJoinHasntPermission embed in the server's owner's private channel.
                        privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.channelMemberJoinHasntPermission", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))))
                                .build()).build()).queue());
            }
            return;
        }
        if (event.getGuild().getSystemChannel() == null) // Verify if the configured (on Discord) system channel exists, if true it sends a memberJoin embed in the configured (on Discord) system channel, if false it does nothing.
            return;
        /*if(Main.getServerConfig().systemChannelMemberJoin().get(event.getGuild().getId())){*/
        try {
            event.getGuild().getSystemChannel().sendMessage(new MessageBuilder(new EmbedBuilder() // Sends a memberJoin embed in the configured (on Discord) system channel
                    .setThumbnail(event.getMember().getUser().getAvatarUrl())
                    .setTitle(String.format(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.memberJoin", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getEffectiveName(), event.getGuild().getName()))
                    .addField(MessageHelper.translateMessage("text.listener.member", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getAsMention(), false)
                    .addField(String.format("%s %s", UnicodeCharacters.heavyPlusSign, MessageHelper.translateMessage("success.listener.onGuildMemberJoin.newMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild())), String.format(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.countMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getGuild().getMemberCount()), false)
                    .setTimestamp(Instant.now())
                    .setColor(Color.GREEN)
                    .build()).build()).queue();
        } catch (InsufficientPermissionException exception) {
            if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the systemChannelHasntPermission embed in the server's owner's private channel.
                return;
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the systemChannelHasntPermission embed in the server's owner's private channel.
                    privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                            .setTimestamp(Instant.now())
                            .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.systemChannelHasntPermission", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))))
                            .build()).build()).queue());
        }
        /*}*/
        if (Main.getServerConfig().guildJoinRole().containsKey(event.getGuild().getId())) {
            if (event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId())) == null) {
                if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the joinRoleNull embed in the server's owner's private channel.
                    return;
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the joinRoleNull embed in the server's owner's private channel.
                        privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.joinRoleNull", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))))
                                .build()).build()).queue());
                return;
            }
            if(!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the botCantManageRole embed in the server's owner's private channel.
                    return;
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the botCantManageRole embed in the server's owner's private channel.
                        privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.botCantManageRole", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName())))
                                .build()).build()).queue());
                return;
            }
            try {
                event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()))).queue();
            } catch (HierarchyException exception){
                if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the hierarchyRoles embed in the server's owner's private channel.
                    return;
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the joinRoleNull hierarchyRoles embed in the server's owner's private channel.
                        privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.hierarchyRoles", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName())))
                                .build()).build()).queue());
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (Main.getServerConfig().channelMemberLeave().containsKey(event.getGuild().getId()) && event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())) != null) { // Verifies if the server's owner configured the channelMemberLeave config and if the configured channel exists, if all is true it try catch the InsufficientPermissionException to verifies if the bot hasn't the permission to write in the configured channel, if false it verifies if the configured (on Discord) system channel exists.
            try { // Try catch the InsufficientPermissionException to verifies if the bot hasn't the permission to write in the configured channel, if it catches it verifies if the owner is null.
                event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())).sendMessage(new MessageBuilder(new EmbedBuilder() // Sends a memberJoin embed in the configured channel
                        .setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl())
                        .setTitle(String.format(MessageHelper.translateMessage("success.listener.onGuildMemberLeave.memberLeave", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getEffectiveName(), event.getGuild().getName()))
                        .addField(MessageHelper.translateMessage("text.listener.member", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getAsMention(), false)
                        .addField(String.format("%s %s", UnicodeCharacters.heavyMinusSign, MessageHelper.translateMessage("success.listener.onGuildMemberLeave.lostMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild())), String.format(MessageHelper.translateMessage("success.listener.onGuildMemberLeave.countMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getGuild().getMemberCount()), false)
                        .setTimestamp(Instant.now())
                        .setColor(Color.GREEN)
                        .build()).build()).queue();
            } catch (InsufficientPermissionException exception) {
                if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the channelMemberJoinHasntPermission embed in the server's owner's private channel.
                    return;
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the channelMemberJoinHasntPermission embed in the server's owner's private channel.
                        privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberLeave.channelMemberLeaveHasntPermission", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))))
                                .build()).build()).queue());
            }
            return;
        }
        if (event.getGuild().getSystemChannel() == null) // Verify if the configured (on Discord) system channel exists, if true it sends a memberJoin embed in the configured (on Discord) system channel, if false it does nothing.
            return;
        /*if(Main.getServerConfig().systemChannelMemberJoin().get(event.getGuild().getId())){*/
        try {
            event.getGuild().getSystemChannel().sendMessage(new MessageBuilder(new EmbedBuilder() // Sends a memberJoin embed in the configured (on Discord) system channel
                    .setThumbnail(event.getMember().getUser().getAvatarUrl())
                    .setTitle(String.format(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.memberJoin", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getEffectiveName(), event.getGuild().getName()))
                    .addField(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.member", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getMember().getAsMention(), false)
                    .addField(String.format("%s %s", UnicodeCharacters.heavyPlusSign, MessageHelper.translateMessage("success.listener.onGuildMemberJoin.newMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild())), String.format(MessageHelper.translateMessage("success.listener.onGuildMemberJoin.countMember", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), event.getGuild().getMemberCount()), false)
                    .setTimestamp(Instant.now())
                    .setColor(Color.GREEN)
                    .build()).build()).queue();
        } catch (InsufficientPermissionException exception) {
            if (event.getGuild().getOwner() == null) // Verifies if the server's owner is null, if true it does nothing, if false it sends the systemChannelHasntPermission embed in the server's owner's private channel.
                return;
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> // Sends the systemChannelHasntPermission embed in the server's owner's private channel.
                    privateChannel.sendMessage(new MessageBuilder(new EmbedBuilder()
                            .setColor(Color.RED)
                            .setFooter(event.getGuild().getOwner().getUser().getName(), event.getGuild().getOwner().getUser().getEffectiveAvatarUrl())
                            .setTimestamp(Instant.now())
                            .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.systemChannelHasntPermission", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))))
                            .build()).build()).queue());
        }
        /*}*/
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().contains(event.getGuild().getSelfMember())) {
            event.getMessage().reply(new MessageBuilder(new EmbedBuilder()
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                    .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, String.format(MessageHelper.translateMessage("success.listener.onGuildMessageReceived.prefix", event.getAuthor(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), Main.getPrefix(event.getGuild()))))
                    .setTimestamp(Instant.now())
                    .setColor(Color.GREEN)
                    .build()).build()).queue();
        }
        if (Main.getServerConfig().prohibitWords() == null) {
            new File("config/server-config.json").delete();
            try {
                Main.setupServerConfig();
            } catch (IOException ex) {
                Main.LOGGER.severe(ex.getMessage());
            }
        }
        if (event.getMessage().getContentRaw().startsWith(Main.getPrefix(event.getGuild()))) {
            String[] args = event.getMessage().getContentRaw().substring(Main.getPrefix(event.getGuild()).length()).strip().split("\\s+");
            if (args.length == 0) return;
            String cmdName = args[0];
            if (Main.getClient().getCommands().stream().anyMatch(command -> command.getName().equalsIgnoreCase(cmdName) || Arrays.stream(command.getAliases()).anyMatch(cmdName::equalsIgnoreCase)) || Main.getClient().getHelpWord().equalsIgnoreCase(cmdName))
                return;
            double highestResult = 0;
            String cmd = null;
            for (Command command : Main.getClient().getCommands()) {
                double _highestResult = LevenshteinDistance.getDistance(cmdName, command.getName());
                double b = 0;
                String _alias = command.getName();
                if (b > _highestResult) {
                    _highestResult = b;
                }
                if (highestResult < _highestResult) {
                    cmd = _alias;
                    highestResult = _highestResult;
                }
            }
            event.getMessage().reply(new MessageBuilder(new EmbedBuilder()
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                    .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, String.format(MessageHelper.translateMessage("success.listener.onGuildMessageReceived.didYouMean", event.getAuthor(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), Main.getPrefix(event.getGuild()), cmd)))
                    .setTimestamp(Instant.now())
                    .setColor(Color.GREEN)
                    .build()).build()).queue();
        }
        if (!Main.getServerConfig().prohibitWords().containsKey(event.getGuild().getId())) return;
        for (String prohibitedWord : Main.getServerConfig().prohibitWords().get(event.getGuild().getId())) {
            if (event.getMessage().getContentRaw().toLowerCase().startsWith(String.format("%sprohibitword", Main.getPrefix(event.getGuild()))) && event.getMember() != null && event.getMember().isOwner()) return;
            if (event.getMessage().getContentRaw().toLowerCase().contains(prohibitedWord.toLowerCase())) event.getMessage().delete().queue(unused -> event.getMessage().reply(new MessageBuilder(new EmbedBuilder()
                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("success.listener.onGuildMessageReceived.prohibitedWord", event.getAuthor(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), prohibitedWord)))
                    .setTimestamp(Instant.now())
                    .setColor(Color.RED)
                    .build()).build()).queue());
        }
    }
}
