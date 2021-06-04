package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

import static fr.noalegeek.pepite_dor_bot.Main.serverConfig;

public class JoinRoleCommand extends BotCommand {

    public JoinRoleCommand() {
        this.name = "joinrole";
        this.aliases = new String[]{"jr"};
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
        this.help = "Défini le rôle (avec son identifiant) que va avoir un utilisateur lors de son arrivée sur un serveur.";
        this.arguments = "`<identifiant du role>`";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if (args.length != 1) {
            event.replyError("Vous devez avoir obligatoirement 1 argument. Exemple ```" + Main.getInfos().prefix + "joinrole 846715377760731156```");
            return;
        }

        Role role = event.getGuild().getRoleById(args[0]);

        if (role == null) {
            event.replyError("Ce rôle n'existe pas.");
            return;
        }else if (role.isManaged()) {
            event.replyError("Ce rôle ne peut être attribué à un utilisateur.");
            return;
        }

        serverConfig.guildJoinRole.put(event.getGuild().getId(), role.getId());
        event.replySuccess("Le rôle à bien été ajouté");
    }
}