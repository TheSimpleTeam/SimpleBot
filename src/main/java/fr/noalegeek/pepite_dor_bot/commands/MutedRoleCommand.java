package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.entities.Role;

public class MutedRoleCommand extends Command {
    public MutedRoleCommand(){
        this.category = CommandCategories.CONFIG.category;
        this.aliases = new String[]{"mr","muter","mrole"};
        this.name = "muterole";
        this.arguments = "<identifiant du rôle>";
        this.help = "Défini le rôle que nécessite la commande !mute pour fonctionner lorsque le serveur a choisi d'utiliser une rôle.";
        this.cooldown = 5;
        this.example = "@NoaLeGeek spam";
    }
    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this));
            return;
        }
        Role mutedRole = event.getGuild().getRoleById(args[0]);
        if (mutedRole == null) {
            event.replyError("Ce rôle n'existe pas.");
            return;
        }
        Main.getServerConfig().mutedRole.put(event.getGuild().getId(), mutedRole.getId());
        event.replySuccess("Le rôle "+mutedRole.getAsMention()+" à bien été défini.");
    }
}
