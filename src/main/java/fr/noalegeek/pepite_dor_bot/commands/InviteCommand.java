package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;

public class InviteCommand extends BotCommand {

    public InviteCommand() {
        this.name = "invite";
        this.aliases = new String[]{"inv", "i"};
        this.guildOnly = true;
        this.arguments = "<create>";
        this.help = "Crée une invitation du serveur.";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if(event.getArgs().length() != 1 && !args[0].equalsIgnoreCase("create")) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Syntaxe de la commande !invite : ``!invite create``.");
            return;
        }
        event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+"Voici ton lien d'invitation du serveur "+event.getGuild().getName()+", n'hésite pas à faire venir plein de personnes !\n"+event.getTextChannel().createInvite().complete().getUrl());
    }
}
