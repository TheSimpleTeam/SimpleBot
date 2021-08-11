package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;

public class PrimeNumberCommand extends Command {
    public PrimeNumberCommand(){
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
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this) + "Le nombre à spécifier a pour limite " + Long.MAX_VALUE  + ".");
            return;
        }
        try {
            long number = Long.parseLong(args[1]);
            switch (args[0]) {
                case "nombre":
                    if(isPrime(number)) {
                        event.replySuccess("Le nombre " + number + " est un nombre premier.");
                    } else {
                        event.replySuccess("Le nombre " + number + " n'est pas un nombre premier.");
                    }
                    break;
                case "liste":
                    MessageBuilder list = new MessageBuilder();
                    for (long i = 1; i < number; i++) {
                        if(list.toString().length() == 1024 || (list.toString() + i + "\n").length() >= 1024) {
                            event.replyWarning("La limite des 1024 caractères à été atteinte.");
                            break;
                        }
                        if(isPrime(i)) {
                            list.append(i).append("\n");
                        }
                    }
                    event.reply(MessageHelper.formattedMention(event.getAuthor())+"Voici la liste des nombres premiers jusqu'à "+number+" :\n"+list.build());
                    break;
                default:
                    MessageHelper.syntaxError(event.getAuthor(), this);
                    break;
            }
        } catch(NumberFormatException ex) {
            event.replyError("Le second argument ne peut contenir des lettres.\n " + MessageHelper.syntaxError(event.getAuthor(), this));
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
