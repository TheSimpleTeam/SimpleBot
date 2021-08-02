package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

public class JoinRoleCommand extends Command {

    public JoinRoleCommand() {
        this.name = "joinrole";
        this.aliases = new String[]{"jr","joinr","jrole"};
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.help = "Défini le rôle que va avoir un utilisateur lors de son arrivée sur un serveur.";
        this.arguments = "<identifiant/mention du role|reset>";
        this.category = CommandCategories.CONFIG.category;
        this.cooldown = 5;
        this.example = "660061218878390272";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 2) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this) + "Les arguments disponibles sont **identifiant/mention du salon** et **reset**.\n" +
                    "- **identifiant/mention du salon** définira le rôle grâce à son indentifiant ou sa mention.\n" +
                    "- **reset** réinitialisera le rôle qui a été configuré.");
            return;
        }
        Role joinRole = event.getGuild().getRoleById(args[0].replace("<@&","").replace(">", ""));
        if (joinRole == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Ce rôle n'existe pas.");
            return;
        } else if (joinRole.isManaged()) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Ce rôle ne peut pas être attribué à un utilisateur.");
            return;
        }
        if(args[0].equalsIgnoreCase("reset")){
            if(event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole.get(event.getGuild().getId())) == null){
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le rôle n'a pas été configuré donc vous ne pouvez pas le réinitialiser.");
                return;
            }
            Main.getServerConfig().guildJoinRole.remove(event.getGuild().getId());
        } else {
            if(event.getGuild().getRoleById(Main.getServerConfig().guildJoinRole.get(event.getGuild().getId())).equals(joinRole)){
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le rôle que vous voulez changer est le même que celui configuré actuellement.");
                return;
            }
            Main.getServerConfig().guildJoinRole.put(event.getGuild().getId(), joinRole.getId());
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "Le rôle " + joinRole.getAsMention() + " à bien été défini.");
        }
    }
}