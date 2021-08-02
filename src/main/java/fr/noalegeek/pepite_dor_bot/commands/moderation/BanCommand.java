package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.aliases = new String[]{"b","ba"};
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "<identifiant/mention du membre> <temps> <raison>";
        this.example = "363811352688721930";
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.category = CommandCategories.STAFF.category;
        this.help = "Banni les membres définitivement du serveur. Le nombre à spécifier correspond aux messages qui vont être supprimés en fonction du temps.";
    }
    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if (args.length == 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this) + "La limite du temps à spécifier est à 7 jours.\n" +
                    "Si le temps spécifié dépasse les 7 jours, celui-ci sera redéféni à 7 jours.\n" +
                    "Le rôle de ce temps à spécifier correspond aux messages qui vont être supprimés en fonction du temps.\n" +
                    "Mettre une raison n'est pas obligatoire.");
            return;
        }
        try{
            User target = event.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", "")).getUser();
            if (target == null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifié une personne.");
                return;
            }
            if(event.getGuild().getMember(target) != null){
                Member targetMember = event.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", ""));
                if(!event.getSelfMember().canInteract(targetMember)){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le bot n'a pas les permissions de faire cela.");
                    return;
                }
                if(!event.getMember().canInteract(targetMember)){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous n'avez pas la permission de ban ce membre.");
                    return;
                }
            }
            if(event.getGuild().retrieveBanList().complete().contains(target)){ // Unban
                event.getGuild().unban(target).queue(unused -> event.replySuccess("L'utilisateur " + target.getName() + " à bien été débanni."));
            } else { // Ban
                try {
                    if (args[1] == null || args[1].isEmpty()) args[1] = "7";
                    if (args[2] == null || args[2].isEmpty()) args[2] = "aucune raison";
                    int banTime = Integer.parseInt(args[1]);
                    if (banTime > 7) {
                        banTime = 7;
                        event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + ":warning: Le temps de ban a été défini à 7 jours en raison du dépassement de la limite !");
                    }
                    event.getGuild().ban(target, banTime).queue();
                    event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + target.getName() + " a été bien banni pendant " + banTime + " pour la raison " + args[2] + ".");
                } catch (NumberFormatException ex) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le temps spécifié n'est pas un nombre.");
                }
            }
        } catch (IndexOutOfBoundsException ex){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez spécifier une personne existante.");
        }
    }
}


