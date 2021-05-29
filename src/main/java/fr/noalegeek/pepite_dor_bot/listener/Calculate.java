package fr.noalegeek.pepite_dor_bot.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Calculate extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (event.getAuthor().isBot()) return;
        //TODO faire en sorte que c'est que dans le channel command bot avec l'ID
        switch (args[0]) {
            case "!calculate":
            case "!c":
            case "!calc":
                if(args.length <= 3){
                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Syntaxe de la commande !calculate : ``!calculate <premier nombre> <+|-|*|/> <second nombre>``.\nLes nombre spécifiés doivent être des nombres entiers ou décimaux.").queue();
                } else if(args.length == 4){
                    try {
                        double number1 = Double.parseDouble(args[1]);
                        double number2 = Double.parseDouble(args[3]);
                        switch (args[2]){
                            case "+":
                                try{
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Addition en cours...").queue();
                                    double addition = number1 + number2;
                                    event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat de l'addition est "+addition+".").queue();
                                }catch (NumberFormatException numberFormatException){
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est trop grand.").queue();
                                }
                                break;
                            case "-":
                                try{
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Soustraction en cours...").queue();
                                    double soustraction = number1 - number2;
                                    event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat de la soustraction est "+soustraction+".").queue();
                                }catch (NumberFormatException numberFormatException){
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est trop grand.").queue();
                                }
                                break;
                            case "*":
                                try{
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Multiplication en cours...").queue();
                                    double multiplication = number1 * number2;
                                    event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat de la multiplication est "+multiplication+".").queue();
                                }catch (NumberFormatException numberFormatException){
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est trop grand.").queue();
                                }
                                break;
                            case "/":
                                try{
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Division en cours...").queue();
                                    double division;
                                    try {
                                         division = number1 / number2;
                                        event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat de la division est "+division+".").queue();
                                    } catch (ArithmeticException arithmeticException){
                                        event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** Il est impossible de diviser par 0.").queue();
                                    }
                                }catch (NumberFormatException numberFormatException){
                                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Le résultat est trop grand.").queue();
                                }
                                break;
                        }
                    }catch (NumberFormatException numberFormatException) {
                        event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Un argument invalide a été détecté ou un des nombres est trop grand.").queue();
                    }
                } else {
                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Vous n'avez respecter la syntaxe de la commande. Veuillez regarder la syntaxe en faisant !calculate.").queue();
                }
                break;
        }
    }
}
