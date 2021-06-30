package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;

public class JoinRoleCommand extends Command {

    public JoinRoleCommand() {
        this.name = "joinrole";
        this.aliases = new String[]{"jr","joinr","jrole"};
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.help = "Défini le rôle (avec son identifiant) que va avoir un utilisateur lors de son arrivée sur un serveur.";
        this.arguments = "<identifiant du role>";
        this.category = CommandCategories.CONFIG.category;
        this.example = "660061218878390272";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this));
            return;
        }
        Role joinRole = event.getGuild().getRoleById(args[0]);
        if (joinRole == null) {
            event.replyError("Ce rôle n'existe pas.");
            return;
        } else if (joinRole.isManaged()) {
            event.replyError("Ce rôle ne peut pas être attribué à un utilisateur.");
            return;
        }
        Main.getServerConfig().guildJoinRole.put(event.getGuild().getId(), joinRole.getId());
        event.replySuccess("Le rôle "+joinRole.getAsMention()+" à bien été défini.");
    }
}