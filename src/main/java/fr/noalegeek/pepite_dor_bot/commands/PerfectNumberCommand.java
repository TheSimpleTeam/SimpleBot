package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

public class PerfectNumberCommand extends Command {

    public PerfectNumberCommand(){
        this.category = CommandCategories.FUN.category;
        this.aliases = new String[]{"perfectn"};
        this.name = "perfectnumber";
        this.arguments = "<nombre entier>";
        this.help = "help.perfectnumber";
        this.cooldown = 5;
        this.example = "14";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split(" \\s+");
        if(event.getArgs().length() == 2) {
            try {
                int addNumbers = 0;

                int chooseNumber = Integer.parseInt(args[1]);

                event.reply(MessageHelper.formattedMention(event.getAuthor()) + "Vérification en cours...");

                for(int i = 1; i < chooseNumber; i++) {
                    if(chooseNumber % i == 0) addNumbers += i;
                }

                if(addNumbers == chooseNumber) {
                    event.reply(String.format("%s %d %s", MessageHelper.formattedMention(event.getAuthor()), chooseNumber, " est un nombre parfait."));
                } else {
                    event.reply(String.format("%s %d %s", MessageHelper.formattedMention(event.getAuthor()), chooseNumber, "n'est pas un nombre parfait."));
                }

            } catch (NumberFormatException numberFormatException) {
                event.reply(String.format("%s %s", MessageHelper.formattedMention(event.getAuthor()), "Le nombre spécifié n'est pas un nombre entier."));
            }
            //TODO: Translate this :D
        } else if(event.getArgs().length() < 2) {
            event.reply(String.format("%s %s", MessageHelper.formattedMention(event.getAuthor()), "Syntaxe de la commande !perfectnumber : ``!pn <nombre>``. Le nombre spécifié doit être un nombre entier."));
        } else {
            event.reply(String.format("%s %s", MessageHelper.formattedMention(event.getAuthor()), "Vous devez mettre seulement un nombre entier."));
        }
    }
}
