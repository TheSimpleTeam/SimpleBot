package net.thesimpleteam.simplebot.listeners;

import com.google.common.base.Throwables;
import com.jagrosh.jdautilities.command.Command;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.config.ServerConfig;
import net.thesimpleteam.simplebot.utils.LevenshteinDistance;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.thesimpleteam.simplebot.utils.UnicodeCharacters;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Listener extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            Listener.saveConfigs();
        } catch (IOException ex) {
            SimpleBot.LOGGER.severe(Throwables.getStackTraceAsString(ex));
        }
        SimpleBot.getExecutorService().schedule(() -> System.exit(0), 3, TimeUnit.SECONDS); //JDA doesn't want to exit the JVM so we do a System.exit()
    }

    public static void saveConfigs() throws IOException {
        if (!new File(new File("config/server-config.json").toPath().toUri()).exists())
            new File(new File("config/server-config.json").toPath().toUri()).createNewFile();
        if (SimpleBot.gson.fromJson(Files.newBufferedReader(new File("config/server-config.json").toPath(), StandardCharsets.UTF_8), ServerConfig.class) == SimpleBot.getServerConfig())
            return;
        Writer writer = Files.newBufferedWriter(new File("config/server-config.json").toPath(), StandardCharsets.UTF_8, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        SimpleBot.gson.toJson(SimpleBot.getServerConfig(), writer);
        writer.close();
        SimpleBot.LOGGER.info("Server config updated");
    }

    //TODO New config for onGuildMemberJoin/Leave : a boolean that active the member join/leave embed into the system channel (Called = systemConfigJoin)
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (SimpleBot.getServerConfig().guildJoinRole().containsKey(event.getGuild().getId())) {
            if (event.getGuild().getRoleById(SimpleBot.getServerConfig().guildJoinRole().get(event.getGuild().getId())) == null) {
                if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                "error.listener.onGuildMemberJoin.joinRoleNull", null, null, null, MessageHelper.getTag(event.getUser()),
                                event.getGuild().getName(), SimpleBot.getPrefix(event.getGuild()), SimpleBot.getPrefix(event.getGuild())).build()).build()).queue());
            } else {
                if (!event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
                    if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel()
                            .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                    "error.listener.onGuildMemberJoin.botCantManageRole", null, null, null, MessageHelper.getTag(event.getUser()),
                                    event.getGuild().getName()).build()).build()).queue());
                } else {
                    try {
                        event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(SimpleBot.getServerConfig().guildJoinRole().get(event.getGuild().getId()))).queue();
                    } catch (HierarchyException e) {
                        if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel()
                                .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                        "error.listener.onGuildMemberJoin.hierarchyRoles", null, null, null, MessageHelper.getTag(event.getUser()),
                                        event.getGuild().getName()).build()).build()).queue());
                    }
                }
            }
        }
        if(SimpleBot.getServerConfig().channelMemberJoin().containsKey(event.getGuild().getId())){
            if(event.getGuild().getTextChannelById(SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId())) == null){
                if(event.getGuild().getOwner() != null && event.getGuild().getOwner().getUser().hasPrivateChannel()) event.getGuild().getOwner().getUser().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                "error.listener.onGuildMemberJoin.channelConfiguredDontExist", null, null, null, MessageHelper.getTag(event.getUser()),
                                event.getGuild().getName(), SimpleBot.getPrefix(event.getGuild()), SimpleBot.getPrefix(event.getGuild()))).build()).queue());
                return;
            }
            try {
                event.getGuild().getTextChannelById(SimpleBot.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.memberJoin", null, null, event.getMember().getUser().getEffectiveAvatarUrl(), event.getMember().getEffectiveName(), event.getGuild().getName())
                        .addField(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.member", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en")).getAsString()), event.getMember().getAsMention(), false)
                        .addField(UnicodeCharacters.HEAVY_PLUS_SIGN + MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.newMember", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en")).getAsString()), String.format(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberJoin.countMember", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en")).getAsString()), event.getGuild().getMemberCount()), false)
                        .build()).build()).queue();
            } catch (InsufficientPermissionException e) {
                if (event.getGuild().getOwner() != null) event.getGuild().getOwner().getUser().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                "error.listener.onGuildMemberJoin.channelMemberJoinHasntPermission", null, null, null, MessageHelper.getTag(event.getUser()),
                                event.getGuild().getName(), SimpleBot.getPrefix(event.getGuild()), SimpleBot.getPrefix(event.getGuild())).build()).build()).queue());
            }
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (SimpleBot.getServerConfig().channelMemberLeave().containsKey(event.getGuild().getId())) {
            if(event.getGuild().getTextChannelById(SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId())) == null){
                if(event.getGuild().getOwner() != null && event.getGuild().getOwner().getUser().hasPrivateChannel()) event.getGuild().getOwner().getUser().openPrivateChannel()
                        .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                "error.listener.onGuildMemberLeave.channelConfiguredDontExist", null, null, null, MessageHelper.getTag(event.getUser()),
                                event.getGuild().getName(), SimpleBot.getPrefix(event.getGuild()), SimpleBot.getPrefix(event.getGuild()))).build()).queue());
                return;
            }
            try {
                event.getGuild().getTextChannelById(SimpleBot.getServerConfig().channelMemberLeave().get(event.getGuild().getId())).sendMessage(new MessageBuilder(
                        MessageHelper.getEmbed(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberLeave.memberLeave", null, null, event.getUser().getEffectiveAvatarUrl(), event.getUser().getName(), event.getGuild().getName())
                        .addField(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "text.listener.member", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en")).getAsString()), event.getMember().getAsMention(), false)
                        .addField(UnicodeCharacters.HEAVY_MINUS_SIGN + MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberLeave.lostMember", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en")).getAsString()), String.format(MessageHelper.translateMessage(event.getUser(), null, event.getGuild(), "success.listener.onGuildMemberLeave.countMember", SimpleBot.getLocalizations().get(SimpleBot.getServerConfig().language().getOrDefault(event.getGuild().getId(), "en")).getAsString()), event.getGuild().getMemberCount()), false)
                        .build()).build()).queue();
            } catch (InsufficientPermissionException e) {
                if (event.getGuild().getOwner() != null) {
                    event.getGuild().getOwner().getUser().openPrivateChannel()
                            .queue(privateChannel -> privateChannel.sendMessage(new MessageBuilder(MessageHelper.getEmbed(event.getGuild().getOwner().getUser(), null, event.getGuild(),
                                    "error.listener.onGuildMemberLeave.channelMemberLeaveHasntPermission", null, null, null, MessageHelper.getTag(event.getUser()),
                                    event.getGuild().getName(), SimpleBot.getPrefix(event.getGuild()), SimpleBot.getPrefix(event.getGuild())).build()).build()).queue());
                }
            }
        }
    }

    private User getUserFromReferencedMessage(Message message) {
        return message == null ? null : message.getAuthor();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().contains(event.getGuild().getSelfMember()) &&
                !Objects.equals(getUserFromReferencedMessage(event.getMessage().getReferencedMessage()), event.getJDA().getSelfUser())) {
            event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event.getAuthor(), event.getMessage(), event.getGuild(), "success.listener.onGuildMessageReceived.prefix", null, null, null, SimpleBot.getPrefix(event.getGuild())).build()).build()).queue();
        }
        if (SimpleBot.getServerConfig().prohibitWords() == null) {
            try {
                Files.delete(Paths.get("config/server-config.json"));
                SimpleBot.setupServerConfig();
            } catch (IOException ex) {
                SimpleBot.LOGGER.severe(ex.getMessage());
            }
        }
        if (event.getMessage().getContentRaw().startsWith(SimpleBot.getPrefix(event.getGuild()))) {
            String[] args = event.getMessage().getContentRaw().substring(SimpleBot.getPrefix(event.getGuild()).length()).strip().split("\\s+");
            if (args.length == 0) return;
            String cmdName = args[0];
            if (SimpleBot.getClient().getCommands().stream().anyMatch(command -> command.getName().equalsIgnoreCase(cmdName) || Arrays.stream(command.getAliases()).anyMatch(cmdName::equalsIgnoreCase)) ||
                    SimpleBot.getClient().getHelpWord().equalsIgnoreCase(cmdName))
                return;
            double highestResult = 0;
            String cmd = null;
            for (String commandName : Stream.concat(SimpleBot.getClient().getCommands().stream().map(Command::getName), Stream.of("help")).toList()) {
                double _highestResult = LevenshteinDistance.getDistance(cmdName, commandName);
                double b = 0;
                if (b > _highestResult) _highestResult = b;
                if (highestResult < _highestResult) {
                    cmd = commandName;
                    highestResult = _highestResult;
                }
            }
            event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event.getAuthor(), event.getMessage(), event.getGuild(), "success.listener.onGuildMessageReceived.didYouMean", null, null, null, SimpleBot.getPrefix(event.getGuild()), cmd).build()).build()).queue();
        }
        if (!SimpleBot.getServerConfig().prohibitWords().containsKey(event.getGuild().getId()) || (SimpleBot.getServerConfig().prohibitWords().containsKey(event.getGuild().getId()) && (event.getMessage().getContentRaw().toLowerCase().startsWith(String.format("%sprohibitword", SimpleBot.getPrefix(event.getGuild()))) || (event.getMember() != null && event.getMember().isOwner())))) return;
        for (String prohibitedWord : SimpleBot.getServerConfig().prohibitWords().get(event.getGuild().getId())) {
            if (event.getMessage().getContentRaw().toLowerCase().contains(prohibitedWord.toLowerCase())) event.getMessage().delete().queue(unused -> event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event.getAuthor(), event.getMessage(), event.getGuild(), "error.listener.onGuildMessageReceived.prohibitedWord", null, null, null, prohibitedWord).build()).build()).queue());
        }
    }
}
