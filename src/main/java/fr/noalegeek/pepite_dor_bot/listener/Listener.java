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
        if (Main.getServerConfig().guildJoinRole().containsKey(event.getGuild().getId())) {
            if (event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId())) == null) {
                if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.joinRoleNull", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild())).build()).build()).queue());
            } else {
                if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                    if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.botCantManageRole", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName()).build()).build()).queue());
                } else {
                    try {
                        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()))).queue();
                    } catch (HierarchyException exception) {
                        if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.hierarchyRoles", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName()).build()).build()).queue());
                    }
                }
            }
        }
        if(Main.getServerConfig().channelMemberJoin().containsKey(event.getGuild().getId())){
            if(event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())) == null){
                if(event.getGuild().getOwner() != null && event.getGuild().getOwner().getUser().hasPrivateChannel()) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.channelConfiguredDontExist", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))).build()).queue());
                return;
            }
            try {
                event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.memberJoin", null, null, event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getEffectiveName(), event.getGuild().getName())
                        .addField(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.member"), event.getMember().getAsMention(), false)
                        .addField(new StringBuilder().append(UnicodeCharacters.heavyPlusSign).append(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.newMember")).toString(), String.format(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.countMember"), event.getGuild().getMemberCount()), false)
                        .build()).build()).queue();
            } catch (InsufficientPermissionException exception) {
                if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.channelMemberJoinHasntPermission", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild())).build()).build()).queue());
            }
        }
        /* else if(Main.getServerConfig().systemChannelMemberJoin().contains(event.getGuild().getId())){
        if (event.getGuild().getSystemChannel() == null)
            return;
        try {
            event.getGuild().getSystemChannel().sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.memberJoin", null, null, event.getMember().getUser().getAvatarUrl(), event.getMember().getEffectiveName(), event.getGuild().getName())
                    .addField(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "text.listener.member"), event.getMember().getAsMention(), false)
                    .addField(new StringBuilder().append(UnicodeCharacters.heavyPlusSign).append(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.newMember")).toString(), String.format(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.countMember"), event.getGuild().getMemberCount()), false)
                    .build()).build()).queue();
        } catch (InsufficientPermissionException exception) {
            if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.systemChannelHasntPermission", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild())).build()).build()).queue());
        }
        }*/
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (Main.getServerConfig().channelMemberLeave().containsKey(event.getGuild().getId())) {
            if(event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())) == null){
                if(event.getGuild().getOwner() != null && event.getGuild().getOwner().getUser().hasPrivateChannel()) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberLeave.channelConfiguredDontExist", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild()))).build()).queue());
                return;
            }
            try {
                event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())).sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberLeave.memberLeave", null, null, event.getUser().getEffectiveAvatarUrl(), event.getUser().getName(), event.getGuild().getName())
                        .addField(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "text.listener.member"), event.getMember().getAsMention(), false)
                        .addField(new StringBuilder().append(UnicodeCharacters.heavyMinusSign).append(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberLeave.lostMember")).toString(), String.format(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberLeave.countMember"), event.getGuild().getMemberCount()), false)
                        .build()).build()).queue();
            } catch (InsufficientPermissionException exception) {
                if (event.getGuild().getOwner() != null)
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberLeave.channelMemberLeaveHasntPermission", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild())).build()).build()).queue());
            }
        }
        /*else if(Main.getServerConfig().systemChannelMemberJoin().get(event.getGuild().getId())){
        if (event.getGuild().getSystemChannel() == null)
            return;
        try {
            event.getGuild().getSystemChannel().sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.memberJoin", null, null, event.getMember().getUser().getAvatarUrl(), event.getMember().getEffectiveName(), event.getGuild().getName())
                    .addField(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.member"), event.getMember().getAsMention(), false)
                    .addField(new StringBuilder().append(UnicodeCharacters.heavyPlusSign).append(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.newMember")).toString(), String.format(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.countMember"), event.getGuild().getMemberCount()), false)
                    .build()).build()).queue();
        } catch (InsufficientPermissionException exception) {
            if (event.getGuild().getOwner() == null) event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(), "error.listener.onGuildMemberJoin.systemChannelHasntPermission", null, null, null, MessageHelper.getTag(event.getUser()), event.getGuild().getName(), Main.getPrefix(event.getGuild()), Main.getPrefix(event.getGuild())).build()).build()).queue());
        }
        }*/
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().contains(event.getGuild().getSelfMember())) {
            event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event.getAuthor(), event.getChannel(), event.getGuild(), "success.listener.onGuildMessageReceived.prefix", null, null, null, Main.getPrefix(event.getGuild())).build()).build()).queue();
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
            event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event.getAuthor(), event.getChannel(), event.getGuild(), "success.listener.onGuildMessageReceived.didYouMean", null, null, null, Main.getPrefix(event.getGuild()), cmd).build()).build()).queue();
        }
        if (!Main.getServerConfig().prohibitWords().containsKey(event.getGuild().getId())) return;
        for (String prohibitedWord : Main.getServerConfig().prohibitWords().get(event.getGuild().getId())) {
            if (event.getMessage().getContentRaw().toLowerCase().startsWith(String.format("%sprohibitword", Main.getPrefix(event.getGuild()))) && event.getMember() != null && event.getMember().isOwner()) return;
            if (event.getMessage().getContentRaw().toLowerCase().contains(prohibitedWord.toLowerCase())) event.getMessage().delete().queue(unused -> event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event.getAuthor(), event.getChannel(), event.getGuild(), "success.listener.onGuildMessageReceived.prohibitedWord", null, null, null, prohibitedWord).build()).build()).queue());
        }
    }
}
