package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * @author NoaLeGeek
 */

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.aliases = new String[]{"b"};
        this.guildOnly = true;
        this.arguments = "<mention> <temps> <raison>";
        this.example = "363811352688721930";
        this.category = CommandCategories.STAFF.category;
        this.help = "Banni les gens définitivement du serveur.\nLe nombre à spécifier correspond aux messages qui vont être supprimés en fonction du temps.";
    }
    @Override
    protected void execute(CommandEvent event) {
        //TODO minebos faut que tu fasses le systeme pour que ça suppr les messages en fonction du temps spécifié
        String[] args = event.getArgs().split("\\s+");
        if (event.getAuthor().isBot()) return;

        if (!event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas la permission de faire cette commande.");
            return;
        }

        if (args.length == 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"\nLa limite du temps à spécifier est à 7 jours.\nSi le temps spécifié dépasse les 7 jours, celui-ci sera redéféni à 7 jours.\nLe rôle de ce temps à spécifier correspond aux messages qui vont être supprimés en fonction du temps.\nMettre une raison n'est pas obligatoire.");
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
                if(args[1] == null || args[1].isEmpty()){
                    args[1] = "7";
                }
                if(args[2] == null || args[2].isEmpty()){
                    args[2] = "aucune raison";
                }
                int banTime = Integer.parseInt(args[1]);
                if(banTime > 7){
                    banTime = 7;
                    event.replyWarning(MessageHelper.formattedMention(event.getAuthor())+"Le temps de ban a été défini à 7 jours en raison du dépassement de la limite !");
                }
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+target.getName()+" a été bien banni pendant "+banTime+" pour la raison "+args[2]+".");
                event.getGuild().ban(target, banTime).queue();
            } catch (NumberFormatException ex){
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le temps spécifié n'est pas un nombre.");
            }
        }catch (IndexOutOfBoundsException ex){
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous devez spécifier une personne existante.");
        }
    }
}


