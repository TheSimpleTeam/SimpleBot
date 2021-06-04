package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;

public class PerfectNumber extends BotCommand {

    public PerfectNumber(){
        this.category = CommandCategories.FUN.category;
        this.aliases = new String[]{"pn","perfectn","pnumber"};
        this.name = "perfectnumber";
        this.arguments = "`<nombre entier>`";
        this.help = "Vérifie si un nombre est premier.";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" ");
        if(event.getArgs().length() == 2) {
            try {
                int addNumbers = 0;
                int chooseNumber = Integer.parseInt(args[1]);
                event.getMessage().reply(MessageHelper.formattedMention(event.getAuthor()) + "Vérification en cours...").queue();
                for(int i = 1;i < chooseNumber;i++){
                    if(chooseNumber % i == 0){
                        addNumbers += i;
                    }
                }
                if(addNumbers == chooseNumber){
                    event.getChannel().sendMessage(MessageHelper.formattedMention(event.getAuthor()) + chooseNumber+" est un nombre parfait.").queue();
                } else {
                    event.getChannel().sendMessage(MessageHelper.formattedMention(event.getAuthor()) + chooseNumber+" n'est pas un nombre parfait.").queue();
                }
            } catch (NumberFormatException numberFormatException){
                event.getMessage().reply(MessageHelper.formattedMention(event.getAuthor()) + "Le nombre spécifié n'est pas un nombre entier.").queue();
            }
        } else if(event.getArgs().length() < 2){
            event.getMessage().reply(MessageHelper.formattedMention(event.getAuthor()) + "Syntaxe de la commande !perfectnumber : ``!pn <nombre>``. Le nombre spécifié doit être un nombre entier.").queue();
        } else {
            event.getMessage().reply(MessageHelper.formattedMention(event.getAuthor()) + "Vous devez mettre seulement un nombre entier.").queue();
        }
    }
}
