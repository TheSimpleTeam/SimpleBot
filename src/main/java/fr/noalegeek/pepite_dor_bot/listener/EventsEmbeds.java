package fr.noalegeek.pepite_dor_bot.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.logging.Level;

import static fr.noalegeek.pepite_dor_bot.Main.*;

public class EventsEmbeds extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                event.getJDA().getPresence().setActivity(Activity.playing(getInfos().activities[new Random().nextInt(getInfos().activities.length)]));
            }
        }, 0, getInfos().timeBetweenStatusChange * 1000);

        Timer autoSave = new Timer();
        autoSave.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    saveConfigs();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage());
                }
            }
        }, 120_000, getInfos().autoSaveDelay * 1000 * 60);
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        try {
            saveConfigs();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage());
        }
    }

    public void saveConfigs() throws IOException {
        Path configPath = new File("server-config.json").toPath();
        Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE);
        gson.toJson(serverConfig, writer);
        writer.close();
        LOGGER.info("Server config updated");
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        EmbedBuilder embedMemberJoin = new EmbedBuilder();
        embedMemberJoin.setThumbnail(event.getMember().getUser().getAvatarUrl());
        embedMemberJoin.setTitle("**" + event.getMember().getEffectiveName() + " a rejoint le serveur __" + event.getGuild().getName()
                + "__ !**");
        embedMemberJoin.addField("Membre", event.getMember().getAsMention(), false);
        embedMemberJoin.addField("[+] Nouveau membre","Nous sommes maintenant " + event.getGuild().getMemberCount()
                + " membres sur le serveur !", false);
        embedMemberJoin.setFooter(String.valueOf(Calendar.getInstance().getTime()));
        embedMemberJoin.setColor(Color.GREEN);
        Objects.requireNonNull(event.getGuild().getDefaultChannel()).sendMessage(embedMemberJoin.build()).queue();
        if(serverConfig.guildJoinRole.containsKey(event.getGuild().getId())) {
            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(serverConfig.guildJoinRole.get(event.getGuild().getId()))))
                    .queue();
        }
        LOGGER.info(event.getUser().getName() + "#" + event.getUser().getDiscriminator() + " joined " + event.getGuild().getName());
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        EmbedBuilder embedMemberRemove = new EmbedBuilder();
        embedMemberRemove.setThumbnail(event.getUser().getAvatarUrl());
        embedMemberRemove.setTitle("**"+(event.getUser()).getName() + " a quitté le serveur __" + event.getGuild().getName() + "__ !**");
        embedMemberRemove.addField("Membre",event.getUser().getAsMention(), false);
        embedMemberRemove.addField("[-] Membre perdu","Nous sommes de nouveau à " + event.getGuild().getMemberCount()
                + " membres sur le serveur...", false);
        embedMemberRemove.setFooter(String.valueOf(Calendar.getInstance().getTime()));
        embedMemberRemove.setColor(Color.RED);
        Objects.requireNonNull(event.getGuild().getDefaultChannel()).sendMessage(embedMemberRemove.build()).queue();
        LOGGER.info(event.getUser().getName() + "#" + event.getUser().getDiscriminator() + " left " + event.getGuild().getName());
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if(event.getAuthor() == event.getJDA().getSelfUser()) return;
        LOGGER.info(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " said : \n" +
                event.getMessage().getContentRaw());
    }
}