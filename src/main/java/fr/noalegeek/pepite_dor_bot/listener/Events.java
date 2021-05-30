package fr.noalegeek.pepite_dor_bot.listener;

import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

import static fr.noalegeek.pepite_dor_bot.Main.LOGGER;

public class Events extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                event.getJDA().getPresence().setActivity(Activity.playing(Main.getInfos().activities[
                        new Random().nextInt(Main.getInfos().activities.length)]));
            }
        }, 0, Main.getInfos().timeBetweenStatusChange * 1000);
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
        event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(Main.getInfos().defaultRoleID)))
                .queue();
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
        LOGGER.info(event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + " said : \n" +
                event.getMessage().getContentRaw());
    }
}
