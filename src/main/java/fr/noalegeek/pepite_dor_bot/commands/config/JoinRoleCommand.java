package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

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
        User author = event.getAuthor();
        if(author.isBot()) return;
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 2) {
            event.replyError(MessageHelper.syntaxError(author,this) + "Les arguments disponibles sont **identifiant/mention du salon** et **reset**.\n" +
                    "- **identifiant/mention du salon** définira le rôle grâce à son indentifiant ou sa mention.\n" +
                    "- **reset** réinitialisera le rôle qui a été configuré.");
            return;
        }
        Guild guild = event.getGuild();
        Role joinRole = guild.getRoleById(args[0].replaceAll("\\D+",""));
        if (joinRole == null) {
            event.replyError(MessageHelper.formattedMention(author) + "Ce rôle n'existe pas.");
            return;
        } else if (joinRole.isManaged()) {
            event.replyError(MessageHelper.formattedMention(author) + "Ce rôle ne peut pas être attribué à un utilisateur.");
            return;
        }
        String joinRoleId = Main.getServerConfig().guildJoinRole.get(guild.getId());
        if(args[0].equalsIgnoreCase("reset")){
            if(guild.getRoleById(joinRoleId) == null){
                event.replyError(MessageHelper.formattedMention(author) + "Le rôle n'a pas été configuré donc vous ne pouvez pas le réinitialiser.");
                return;
            }
            Main.getServerConfig().guildJoinRole.remove(guild.getId());
        } else {
            if(guild.getRoleById(joinRoleId).equals(joinRole)){
                event.replyError(MessageHelper.formattedMention(author) + "Le rôle que vous voulez changer est le même que celui configuré actuellement.");
                return;
            }
            Main.getServerConfig().guildJoinRole.put(guild.getId(), joinRole.getId());
            event.replySuccess(MessageHelper.formattedMention(author) + "Le rôle " + joinRole.getAsMention() + " à bien été défini.");
        }
    }
}