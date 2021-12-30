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
        if (Main.getServerConfig().channelMemberJoin().containsKey(event.getGuild().getId()) && event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())) != null) { // Verifies if the server configured the channelMemberJoin config and if the configured channel, if all is true it verifies if the configured channel exists, if false it verifies if the configured (on Discord) system channel exists.
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
                                .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, String.format(MessageHelper.translateMessage("error.listener.onGuildMemberJoin.channelMemberJoinHasntPermission", event.getUser(), event.getGuild().getOwner() == null ? null : event.getGuild().getOwner().getUser(), event.getGuild()), MessageHelper.getTag(event.getUser()), event.getGuild().getName())))
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
        EmbedBuilder embedMemberLeave = new EmbedBuilder()
                .setThumbnail(event.getUser().getAvatarUrl())
                .setTitle("**" + (event.getUser()).getName() + " a quitté le serveur __" + event.getGuild().getName() + "__ !**")
                .addField("Membre", event.getUser().getAsMention(), false)
                .addField("➖ Membre perdu", "Nous sommes de nouveau à " + event.getGuild().getMemberCount() + " membres sur le serveur...", false)
                .setTimestamp(Instant.now())
                .setColor(Color.RED);
        if (!Main.getServerConfig().channelMemberLeave().containsKey(event.getGuild().getId())) {
            try {
                event.getGuild().getDefaultChannel().sendMessage(new MessageBuilder(embedMemberLeave).build()).queue();
            } catch (InsufficientPermissionException ex) {
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage(MessageHelper.formattedMention(event.getGuild().getOwner().getUser()) + MessageHelper.getTag(event.getUser()) +
                                " a quitté votre serveur **" + event.getGuild().getName() +
                                "** mais je n'ai pas pu envoyer le message de départ car je n'ai pas accès au salon mis par défaut.\n" +
                                "(Vous n'avez pas configurer le salon des messages de départs, c'est pour cela que j'ai choisi le salon par défaut. Vous pouvez changer tout cela en faisant `"
                                + Main.getInfos().prefix() + "config channelmember remove <identifiant du salon>`)"));
            }
            return;
        }
        try {
            event.getGuild().getTextChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())).sendMessage(new MessageBuilder(embedMemberLeave).build()).queue();
        } catch (InsufficientPermissionException ex) {
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.
                    sendMessage(MessageHelper.formattedMention(event.getGuild().getOwner().getUser()) + MessageHelper.getTag(event.getUser()) +
                            " a quitté votre serveur **" + event.getGuild().getName() + "** mais je n'ai pas pu envoyer le message de départ car je n'ai pas accès au salon configuré.\n" +
                            "(Vous avez configurer le salon des messages de départ, c'est pour cela que j'ai choisi le salon configuré. Vous pouvez changer tout cela en faisant `" + Main.getInfos().prefix() + "config channelmember remove reset`)"));
        }
        Main.LOGGER.info(event.getUser().getName() + "#" + event.getUser().getDiscriminator() + " a quitté le serveur " + event.getGuild().getName() + ".");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())) {
            if (Main.getServerConfig().prefix().containsKey(event.getGuild().getId())) {
                event.getMessage().reply("My prefix is **" + Main.getServerConfig().prefix().get(event.getGuild().getId()) + "**").queue();
                return;
            }
            event.getMessage().reply("My prefix is **" + Main.getClient().getPrefix() + "**").queue();
            return;
        }
        String message = event.getMessage().getContentRaw();
        Main.LOGGER.info(String.format("%s %s:%n %s", MessageHelper.getTag(event.getAuthor()), "a dit", message));
        if (Main.getServerConfig().prohibitWords() == null) {
            new File("config/server-config.json").delete();
            try {
                Main.setupServerConfig();
            } catch (IOException ex) {
                Main.LOGGER.severe(ex.getMessage());
            }
            return;
        }
        if (message.startsWith(Main.getPrefix(event.getGuild()))) {
            String[] args = message.substring(Main.getPrefix(event.getGuild()).length()).split("\\s+");
            if (args.length == 0) return;
            String cmdName = args[0];
            if (Main.getClient().getCommands().stream().anyMatch(command -> command.getName().equalsIgnoreCase(cmdName) ||
                    Arrays.stream(command.getAliases()).anyMatch(cmdName::equalsIgnoreCase)) || Main.getClient().getHelpWord().equalsIgnoreCase(cmdName))
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
            event.getChannel().sendMessage("Did you meant " + cmd).mention(event.getAuthor()).complete();
        }
        if (!Main.getServerConfig().prohibitWords().containsKey(event.getGuild().getId())) return;
        for (String s : Main.getServerConfig().prohibitWords().get(event.getGuild().getId())) {
            for (String alias : new String[]{"prohibitw", "prohitbitwrd", "pw", "pwrd", "pword"}) {
                if (message.toLowerCase().startsWith(alias)) {
                    return;
                }
            }
            if (message.toLowerCase().contains(s.toLowerCase())) {
                event.getMessage().delete().queue(unused -> event.getMessage().reply(MessageHelper.formattedMention(event.getAuthor()) + "Le mot `" + s + "` fait parti de la liste des mots interdits.").queue());
            }
        }
    }
}