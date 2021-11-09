package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

public class PrimeNumberCommand extends Command {

    public PrimeNumberCommand() {
        this.category = CommandCategories.FUN.category;
        this.aliases = new String[]{"primen","prnumber","pnum","primenum","prn"};
        this.name = "primenumber";
        this.arguments = "<nombre|liste> <nombre>";
        this.help = "Vérifie si un nombre est premier ou donne la liste de tous les nombres premiers à partir d'un nombre donné.";
        this.cooldown = 5;
        this.example = "nombre 87";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");

        if(args.length != 2) {
            MessageHelper.syntaxError(event,this, "Le nombre à spécifier a pour limite " + Long.MAX_VALUE  + ".");
            return;
        }

        try {
            long number = Long.parseLong(args[1]);
            switch (args[0]) {
                case "nombre":
                    if(isPrime(number)) {
                        event.reply("Le nombre " + number + " est un nombre premier.");
                    } else {
                        event.reply("Le nombre " + number + " n'est pas un nombre premier.");
                    }
                    break;

                case "liste":
                    StringBuilder list = new StringBuilder();
                    for (long i = 1; i < number; i++) {
                        if(list.length() == 1024 || (list.toString() + i + "\n").length() >= 1024) {
                            event.replyWarning("La limite des 1024 caractères a été atteinte.");
                            break;
                        }
                        if(isPrime(i)) {
                            list.append(i).append("\n");
                        }
                    }
                    event.reply(String.format("%s %s %d :%n %s", MessageHelper.formattedMention(event.getAuthor()), "Voici la liste des nombres premiers jusqu'à ", number, list));
                    break;

                default:
                    MessageHelper.syntaxError(event, this, null);
                    break;
            }
        } catch(NumberFormatException ex) {
            //TODO: Traduire ça
            event.reply("Le second argument ne peut contenir des lettres.");
            MessageHelper.syntaxError(event, this, null);
        }
    }

    private boolean isPrime(long num) {
        for (long i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
