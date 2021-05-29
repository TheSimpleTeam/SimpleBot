package fr.noalegeek.pepite_dor_bot.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PerfectNumber extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (event.getAuthor().isBot()) return;
        //TODO faire en sorte que c'est que dans le channel command bot avec l'ID
        switch (args[0]) {
            case "!perfectnumber":
            case "!pn":
            case "!perfectn":
            case "!pnumber":
                if (args.length == 2) {
                    try {
                        int addNumbers = 0;
                        int chooseNumber = Integer.parseInt(args[1]);
                        event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Vérification en cours...").queue();
                        for (int i = 1; i < chooseNumber; i++) {
                            if (chooseNumber % i == 0) {
                                addNumbers += i;
                            }
                        }
                        if (addNumbers == chooseNumber) {
                            event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** " + chooseNumber + " est un nombre parfait.").queue();
                        } else {
                            event.getChannel().sendMessage("**[**" + event.getAuthor().getAsMention() + "**]** " + chooseNumber + " n'est pas un nombre parfait.").queue();
                        }
                    } catch (NumberFormatException numberFormatException) {
                        event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Le nombre spécifié n'est pas un nombre entier.").queue();
                    }
                } else if (args.length == 1) {
                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Syntaxe de la commande !perfectnumber : ``!perfectnumber <nombre>``.\nLe nombre spécifié doit être un nombre entier.").queue();
                } else {
                    event.getMessage().reply("**[**" + event.getAuthor().getAsMention() + "**]** Vous n'avez pas respecter la syntaxe de la commande. Veuillez regarder la syntaxe en faisant !perfectnumber.").queue();
                }
                break;
        }
    }
}
