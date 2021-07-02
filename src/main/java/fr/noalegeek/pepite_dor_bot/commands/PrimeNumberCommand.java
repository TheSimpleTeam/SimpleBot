package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

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
        if(args.length != 2){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Le nombre à spécifier a pour limite "+Long.MAX_VALUE+".");
        }
        try {
            long number = Long.parseLong(args[1])/2;
            int verifyPrimeNumber = 0;
            switch (args[0]){
                case "nombre":
                    for(long i = 2; i < number; i++){
                        if (number % i == 0) {
                            verifyPrimeNumber = 1;
                            break;
                        }
                    }
                    if(verifyPrimeNumber == 0){ // It's a prime number
                        event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+Long.parseLong(args[1])+" est un nombre premier.");
                    } else { // It's not a prime number
                        event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+Long.parseLong(args[1])+" n'est pas un nombre premier.");
                    }
                    break;
                case "liste":
                    StringBuilder list = new StringBuilder();
                    for(long i = 1; i < number; i++) {
                        for (long j = 2; j < i; j++) {
                            if (number % j != 0) {
                                if (list.toString().length() > 1024) {
                                    event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + "La liste étant trop longue est stoppée aux 1024 caractères.");
                                    break;
                                }
                                list.append(i).append("\n");
                            }
                        }
                    }
                    event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+"Liste de tous les nombres premiers jusqu'à "+Long.parseLong(args[1])+" :\n\n"+list);
                    break;
                default:
                    event.replyError(MessageHelper.syntaxError(event.getAuthor(),this));
                    break;
            }
        } catch (NumberFormatException e){
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le nombre spécifié est trop grand.");
        }
    }
}
