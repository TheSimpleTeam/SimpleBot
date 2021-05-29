package fr.noalegeek.pepite_dor_bot.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InviteCommandOld extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (event.getAuthor().isBot()) return;
        //TODO faire en sorte que c'est que dans le channel command bot avec l'ID
        //Non...
        switch (args[0]){
            case "!invite":
            case "!inv":
            case "!i":
                if(args.length == 1){
                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Syntaxe de la commande !invite : ``!invite create``.").queue();
                } else if(args.length == 2 && args[1].equalsIgnoreCase("create")){
                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Voici ton lien d'invitation du serveur "+event.getGuild().getName()+", n'hésite pas à faire venir plein de personnes !").queue();
                    event.getChannel().sendMessage(event.getChannel().createInvite().complete().getUrl()).queue();
                }
                break;
        }
    }
}
