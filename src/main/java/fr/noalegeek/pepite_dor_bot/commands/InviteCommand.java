package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;

import java.util.Locale;

public class InviteCommand extends Command {

    public InviteCommand() {
        this.name = "invite";
        this.aliases = new String[]{"inv", "i"};
        this.guildOnly = true;
        this.arguments = "[create] - [bot]";
        this.help = "Crée une invitation du serveur.";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s");
        if(event.getArgs().length() == 0 /* && !args[0].equalsIgnoreCase("create")*/) {
            //event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Syntaxe de la commande !invite : ``!invite create``.");
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create":
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "Voici ton lien d'invitation du serveur " + event.getGuild().getName() +
                        ", n'hésite pas à faire venir plein de personnes !\n" + event.getTextChannel().createInvite().complete().getUrl());
                break;
            case "bot":
                String discordInviteURL = "https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=8589934591";
                event.replySuccess(String.format(discordInviteURL, event.getJDA().getSelfUser().getId()));
                break;
            default:
                event.replyError("Cet argument n'existe pas.");
                break;
        }
    }
}
