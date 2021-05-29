package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class InviteCommand extends Command {

    public InviteCommand() {
        this.name = "invite";
        this.aliases = new String[]{"inv", "i"};
        this.guildOnly = true;
        this.arguments = "<create>";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if(args.length != 1 && !args[0].equalsIgnoreCase("create")) {
            event.replyError("**[**" + event.getAuthor().getAsMention() + "**]** Syntaxe de la commande !invite : ``!invite create``.");
            return;
        }
        event.replySuccess("**[**" + event.getAuthor().getAsMention() + "**]** Voici ton lien d'invitation du serveur " + event.getGuild().getName() + ", n'hésite pas à faire venir plein de personnes ! \n" +
                event.getTextChannel().createInvite().complete().getUrl());
    }
}
