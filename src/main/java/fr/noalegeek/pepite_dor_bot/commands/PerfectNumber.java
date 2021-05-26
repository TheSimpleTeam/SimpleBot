package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PerfectNumber extends Command {
    public PerfectNumber(){
        this.aliases = new String[]{"pn","perfectn","pnumber"};
        this.name = "perfectnumber";
        this.arguments = "<nombre entier>";
        this.help = "Vérifie si un nombre est premier.";
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if(event.getArgs().length() == 2) {
            try {
                int addNumbers = 0;
                int chooseNumber = Integer.parseInt(args[1]);
                event.getMessage().reply("**[**"+event.getAuthor().getAsMention()+"**]** Vérification en cours...").queue();
                for(int i = 1;i < chooseNumber;i++){
                    if(chooseNumber % i == 0){
                        addNumbers += i;
                    }
                }
                if(addNumbers == chooseNumber){
                    event.getChannel().sendMessage("**[**"+event.getAuthor().getAsMention()+"**]** "+chooseNumber+" est un nombre parfait.").queue();
                } else {
                    event.getChannel().sendMessage("**[**"+event.getAuthor().getAsMention()+"**]** "+chooseNumber+" n'est pas un nombre parfait.").queue();
                }
            } catch (NumberFormatException numberFormatException){
                event.getMessage().reply("**[**"+event.getAuthor().getAsMention()+"**]** Le nombre spécifié n'est pas un nombre entier.").queue();
            }
        } else if(event.getArgs().length() < 2){
            event.getMessage().reply("**[**"+event.getAuthor().getAsMention()+"**]** Syntaxe de la commande !perfectnumber : ``!pn <nombre>``. Le nombre spécifié doit être un nombre entier.").queue();
        } else {
            event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Vous devez mettre seulement un nombre entier.").queue();
        }
    }
}
