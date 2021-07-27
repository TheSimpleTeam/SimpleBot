package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class SayCommand extends Command {

    public SayCommand() {
        this.name = "say";
        this.cooldown = 5;
        this.arguments = "<texte>";
        this.aliases = new String[]{"s","sa"};
        this.help = "Envoie le un message avec le texte défini après la commande sans supprimer le message d'origine.";
        this.example = "Hey, je suis un robot !";
        this.category = CommandCategories.STAFF.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Si vous n'avez pas les permissions de gérer les messages, le bot va vour mentionner.");
            return;
        }
        if (!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + event.getArgs());
        } else {
            event.replySuccess(event.getArgs());
        }
        event.getMessage().delete().queue();
    }
}
