package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;

public class WithoutMutedRoleCommand extends Command {
    public WithoutMutedRoleCommand(){
        this.category = CommandCategories.CONFIG.category;
        this.aliases = new String[]{"wmr","withoutmr","withoutmuter","withoutmrole","wmuterole","wmrole","wmuter"};
        this.name = "withoutmuterole";
        this.arguments = "<true|false>";
        this.help = "Défini si la commande !mute doit nécessiter d'un rôle ou non.\nℹ Nous vous conseillons d'utiliser un rôle, il faudra alors définir les permissions de ce rôle.";
        this.cooldown = 5;
        this.example = "false";
        this.guildOnly = true;
    }
    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this));
            return;
        }
        switch (args[0]){
            case "false":
            case "true":
                Main.getServerConfig().withoutMutedRole.put(event.getGuild().getId(),Boolean.parseBoolean(args[0]));
                event.replySuccess("Le paramètre **withoutMutedRole** a bien été mis sur **"+Boolean.parseBoolean(args[0])+"**.");
                break;
            default:
                event.replyError(MessageHelper.syntaxError(event.getAuthor(),this));
                break;
        }
    }
}
