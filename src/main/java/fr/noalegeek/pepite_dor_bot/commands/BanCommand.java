package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class BanCommand extends Command {
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
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas la permission de faire cette commande.");
            return;
        }
        if (args.length == 1) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Syntaxe de la commande !ban : ``!ban <user ou userID> <temps en jours> [raison]``.\nSi le temps dépasse les 7 jours, \nMettre une raison n'est pas obligatoire.");
            return;
        }
        try{
            User target = event.getMessage().getMentionedUsers().get(0);
            if (target == null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous devez spécifié un membre existant.");
                return;
            }
            if(event.getGuild().getMember(target) != null){
                Member targetMember = event.getMessage().getMentionedMembers().get(0);
                if(!event.getSelfMember().canInteract(targetMember)){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le bot n'a pas les permissions de faire cela.");
                    return;
                }
                if(!event.getMember().canInteract(targetMember)){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas la permission de ban ce membre.");
                    return;
                }
            }
            try{
                int banTime = Integer.parseInt(args[1]);
                if(banTime > 7){
                    banTime = 7;
                    event.replyWarning(MessageHelper.formattedMention(event.getAuthor())+"Le temps de ban a été défini à 7 jours en raison du dépassement de la limite !");
                }
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+target.getName()+" a été bien banni pendant "+banTime+" pour la raison "+args[2]+".");
                event.getGuild().ban(target, banTime).queue();
            } catch (NumberFormatException numberFormatException){
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le temps spécifié n'est pas un nombre.");
            }
        }catch (IndexOutOfBoundsException indexOutOfBoundsException){
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous devez spécifier une personne existante.");
        }
    }
}


