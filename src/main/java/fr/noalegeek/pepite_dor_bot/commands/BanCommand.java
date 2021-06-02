package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class BanCommand extends Command {

    /**
     * Alors noa ton objectif sera de faire cette commande si tu as besoin d'un indice je te conseille de regarder <br/> <br/> {@link net.dv8tion.jda.api.entities.Guild#ban(User, int, String)}
     */

    public BanCommand() {
        this.name = "ban";
        this.aliases = new String[]{"b"};
        this.guildOnly = true;
        this.arguments = "<user ou userID> <temps> <raison>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if (event.getAuthor().isBot()) return;
        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Vous n'avez pas la permission de faire cette commande.");
            return;
        }
        if (event.getArgs().length() == 1) {
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Syntaxe de la commande !ban : ``!ban <user ou userID> <temps> [raison]``. ");
            return;
        }
        if(event.getMessage().getMentionedMembers().get(0) == null){
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Vous devez spécifier une personne existante.");
            return;
        }
        if(!event.getSelfMember().canInteract(event.getMessage().getMentionedMembers().get(0))){
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Le bot n'a pas les permissions de faire cela.");
            return;
        }
        if(!event.getMember().canInteract(event.getMessage().getMentionedMembers().get(0))){
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Vous n'avez pas la permission de ban ce membre.");
            return;
        }
        try{
            int banTime = Integer.parseInt(args[2]);
        } catch (NumberFormatException numberFormatException){
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Le temps spécifié n'est pas un nombre.");
        }
    }
}


