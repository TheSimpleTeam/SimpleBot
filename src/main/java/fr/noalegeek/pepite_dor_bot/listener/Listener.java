package fr.noalegeek.pepite_dor_bot.listener;

import com.jagrosh.jdautilities.command.Command;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.config.ServerConfig;
import fr.noalegeek.pepite_dor_bot.utils.LevenshteinDistance;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

import static fr.noalegeek.pepite_dor_bot.Main.*;

public class Listener extends ListenerAdapter {

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            saveConfigs();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }

    public static void saveConfigs() throws IOException {
        Path configPath = new File("config/server-config.json").toPath();
        if (!new File(configPath.toUri()).exists()) {
            new File(configPath.toUri()).createNewFile();
        }
        Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8);
        if (gson.fromJson(reader, ServerConfig.class) == getServerConfig()) {
            System.out.println("It is");
            return;
        }
        Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        gson.toJson(getServerConfig(), writer);
        writer.close();
        LOGGER.info("Server config updated");
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        EmbedBuilder embedMemberJoin = new EmbedBuilder()
                .setThumbnail(event.getMember().getUser().getAvatarUrl())
                .setTitle("**" + event.getMember().getEffectiveName() + " a rejoint le serveur __" + event.getGuild().getName() + "__ !**")
                .addField("Membre", event.getMember().getAsMention(), false)
                .addField("➕ Nouveau membre", "Nous sommes maintenant " + event.getGuild().getMemberCount() + " membres sur le serveur !", false)
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN);
        if (!getServerConfig().channelMemberJoin().containsKey(event.getGuild().getId())) {
            try {
                event.getGuild().getDefaultChannel().sendMessage(new MessageBuilder(embedMemberJoin).build()).queue();
            } catch (InsufficientPermissionException ex) {
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage(MessageHelper.formattedMention(event.getGuild().getOwner().getUser()) + MessageHelper.getTag(event.getUser()) +
                                " a rejoint votre serveur **" + event.getGuild().getName() +
                                "** mais je n'ai pas pu envoyer le message de bienvenue car je n'ai pas accès au salon mis par défaut.\n" +
                        "(Vous n'avez pas configurer le salon des messages de bienvenue, c'est pour cela que j'ai choisi le salon par défaut. " +
                        "Vous pouvez changer tout cela en faisant `" + getInfos().prefix() + "channelmember remove <identifiant du salon>`)"));
            }
            return;
        }
        //TODO verif si le salon existe
        try {
            event.getGuild().getTextChannelById(getServerConfig().channelMemberJoin().get(event.getGuild().getId())).sendMessage(new MessageBuilder(embedMemberJoin).build()).queue();
        } catch (InsufficientPermissionException ex) {
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage(MessageHelper.formattedMention(event.getGuild().getOwner().getUser()) + MessageHelper.getTag(event.getUser()) +
                            " a rejoint votre serveur **" + event.getGuild().getName() +
                            "** mais je n'ai pas pu envoyer le message de bienvenue car je n'ai pas accès au salon configuré.\n" +
                    "(Vous avez configurer le salon des messages de bienvenue, c'est pour cela que j'ai choisi le salon configuré. Vous pouvez changer tout cela en faisant `" +
                    getInfos().prefix() + "channelmember join reset`)"));
        }
        if (getServerConfig().guildJoinRole().containsKey(event.getGuild().getId())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole()
                    .get(event.getGuild().getId())))).queue();
        }
        LOGGER.info(event.getUser().getName() + "#" + event.getUser().getDiscriminator() + " a rejoint le serveur " + event.getGuild().getName() + ".");
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
        if (!getServerConfig().channelMemberLeave().containsKey(event.getGuild().getId())) {
            try {
                event.getGuild().getDefaultChannel().sendMessage(new MessageBuilder(embedMemberLeave).build()).queue();
            } catch (InsufficientPermissionException ex) {
                event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage(MessageHelper.formattedMention(event.getGuild().getOwner().getUser()) + MessageHelper.getTag(event.getUser()) +
                                " a quitté votre serveur **" + event.getGuild().getName() +
                                "** mais je n'ai pas pu envoyer le message de départ car je n'ai pas accès au salon mis par défaut.\n" +
                        "(Vous n'avez pas configurer le salon des messages de départs, c'est pour cela que j'ai choisi le salon par défaut. Vous pouvez changer tout cela en faisant `"
                                + getInfos().prefix() + "channelmember remove <identifiant du salon>`)"));
            }
            return;
        }
        try {
            event.getGuild().getTextChannelById(getServerConfig().channelMemberLeave().get(event.getGuild().getId())).sendMessage(new MessageBuilder(embedMemberLeave).build()).queue();
        } catch (InsufficientPermissionException ex) {
            event.getGuild().getOwner().getUser().openPrivateChannel().queue(privateChannel -> privateChannel.
                    sendMessage(MessageHelper.formattedMention(event.getGuild().getOwner().getUser()) + MessageHelper.getTag(event.getUser()) +
                            " a quitté votre serveur **" + event.getGuild().getName() + "** mais je n'ai pas pu envoyer le message de départ car je n'ai pas accès au salon configuré.\n" +
                    "(Vous avez configurer le salon des messages de départ, c'est pour cela que j'ai choisi le salon configuré. Vous pouvez changer tout cela en faisant `" +
                    getInfos().prefix() + "channelmember remove reset`)"));
        }
        LOGGER.info(event.getUser().getName() + "#" + event.getUser().getDiscriminator() + " a quitté le serveur " + event.getGuild().getName() + ".");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if(event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser())) {
            if(Main.getServerConfig().prefix().containsKey(event.getGuild().getId())) {
                event.getMessage().reply("My prefix is **" + Main.getServerConfig().prefix().get(event.getGuild().getId()) + "**").queue();
                return;
            }
            event.getMessage().reply("My prefix is **" + Main.getClient().getPrefix() + "**").queue();
            return;
        }
        String message = event.getMessage().getContentRaw();
        LOGGER.info(MessageHelper.getTag(event.getAuthor()) + " a dit :\n" + message);
        if (getServerConfig().prohibitWords() == null) {
            new File("config/server-config.json").delete();
            try {
                setupServerConfig();
            } catch (IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
            return;
        }
        if(message.startsWith(Main.getPrefix(event.getGuild()))) {
            String[] args = message.substring(Main.getPrefix(event.getGuild()).length()).split("\\s+");
            if(args.length == 0) return;
            String cmdName = args[0];
            if(Main.getClient().getCommands().stream().anyMatch(command -> command.getName().equalsIgnoreCase(cmdName) ||
                    Arrays.stream(command.getAliases()).anyMatch(cmdName::equalsIgnoreCase))) return;
            double highestResult = 0;
            String cmd = null;
            for (Command command : getClient().getCommands()) {
                double _highestResult = LevenshteinDistance.getDistance(cmdName, command.getName());
                double b = 0;
                String _alias = command.getName();

                if(b > _highestResult) {
                    _highestResult = b;
                }
                if(highestResult < _highestResult) {
                    cmd = _alias;
                    highestResult = _highestResult;
                }
            }
            event.getChannel().sendMessage("Did you meant " + cmd).mention(event.getAuthor()).complete();
        }
        if (!getServerConfig().prohibitWords().containsKey(event.getGuild().getId())) return;
        for (String s : getServerConfig().prohibitWords().get(event.getGuild().getId())) {
            for(String alias : new String[]{"prohibitw","prohitbitwrd","pw","pwrd","pword"}){
                if(message.toLowerCase().startsWith(alias)){
                    return;
                }
            }
            if (message.toLowerCase().contains(s.toLowerCase())) {
                event.getMessage().delete().queue(unused -> event.getMessage().reply(MessageHelper.formattedMention(event.getAuthor()) + "Le mot `" + s + "` fait parti de la liste des mots interdits.").queue());
            }
        }
    }
}