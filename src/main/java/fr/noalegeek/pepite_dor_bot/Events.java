package fr.noalegeek.pepite_dor_bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Calendar;
import java.util.Objects;

public class Events extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        //TODO Faire la commande !perfectnumber
        if(event.getAuthor().isBot()) return;
        if (event.getChannel().getId().equalsIgnoreCase("846351256176033842")) {
            switch(event.getMessage().getContentRaw()){
                case "!e":
                    event.getChannel().sendMessage("E").queue();
                    break;
                case "!perfectnumber":
                    if(args.length == 2) {
                        try {
                            int chooseNumber = Integer.parseInt(args[1]);
                            event.getMessage().reply("okay").queue();
                        } catch (NumberFormatException numberFormatException){
                            event.getMessage().reply("**[**"+event.getAuthor().getAsMention()+"**]** Le nombre spécifié n'est pas un nombre entier.").queue();
                        }
                    } else if(args.length < 2){
                        event.getMessage().reply("**[**"+event.getAuthor().getAsMention()+"**]** Syntaxe de la commande !perfectnumber : ``!perfectnumber <nombre>``. Le nombre spécifié doit être un nombre entier.").queue();
                    } else {
                        event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]**").queue();
                    }
                    break;
                case "!test":

                    break;
            }
        }
    }
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        EmbedBuilder embedMemberJoin = new EmbedBuilder();
        embedMemberJoin.setThumbnail(event.getMember().getUser().getAvatarUrl());
        embedMemberJoin.setTitle("**"+event.getMember().getEffectiveName()+" a rejoint le serveur __"+event.getGuild().getName()+"__ !**");
        embedMemberJoin.addField("Membre",event.getMember().getAsMention(), false);
        embedMemberJoin.addField("[+] Nouveau membre","Nous sommes maintenant "+String.valueOf(event.getGuild().getMemberCount())+" membres sur le serveur !",false);
        embedMemberJoin.setFooter(String.valueOf(Calendar.getInstance().getTime()));
        embedMemberJoin.setColor(Color.GREEN);
        Objects.requireNonNull(event.getGuild().getTextChannelById("846712559696609290")).sendMessage(embedMemberJoin.build()).queue();
        event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById("846715377760731156"))).queue();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        EmbedBuilder embedMemberRemove = new EmbedBuilder();
        embedMemberRemove.setThumbnail(event.getUser().getAvatarUrl());
        embedMemberRemove.setTitle("**"+(event.getUser()).getName()+" a quitté le serveur __"+event.getGuild().getName()+"__ !**");
        embedMemberRemove.addField("Membre",event.getUser().getAsMention(),false);
        embedMemberRemove.addField("[-] Membre perdu","Nous sommes de nouveau à "+String.valueOf(event.getGuild().getMemberCount())+" membres sur le serveur...",false);
        embedMemberRemove.setFooter(String.valueOf(Calendar.getInstance().getTime()));
        embedMemberRemove.setColor(Color.RED);
        Objects.requireNonNull(event.getGuild().getTextChannelById("846712559696609290")).sendMessage(embedMemberRemove.build()).queue();
    }
}
