package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestCommand extends Command {

    public TestCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "help.test";
        this.cooldown = 5;
        this.name = "test";
        this.hidden = true;
        this.aliases = new String[]{"t", "te", "tes"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        StringBuilder stringBuilder = new StringBuilder();
        /*
        stringBuilder.append("\"");
        for(String part1 : new String[]{"un", "u"}){
            for(String part2 : new String[]{"short", "s"}){
                for(String part3 : new String[]{"url", "u"}){
                    if(!(part1 + part2 + part3).equals("unshorturl")){
                        stringBuilder.append(part1 + part2 + part3).append("\",\"");
                    }
                }
            }
        }*/
        System.out.println("""
                Les arguments disponibles sont :
                
                - **channelmember** :
                Cet argument a des sous-arguments :
                - **join** concerne les messages de bienvenue;
                - **leave** concerne les messages de départ.
                Ces sous-arguments ont des sous-arguments :
                - **reset** réinitialise le salon configuré, les messages de bienvenue/départ seront envoyés dans le salon par défaut;
                - **this** définit le salon où a été envoyé cette commande;
                - **identifiant/mention du salon** définit le salon en fonction de son identifiant ou de sa mention.
                
                - **prohibitword** :
                Cet argument a des sous-arguments :
                - **add** ajoute un mot à la liste des mots interdits;
                - **remove** retire un mot à la liste des mots interdits;
                - **reset** réinitialise la liste entière des mots interdits.
                Les sous-arguments **add** et **remove** ont un sous-argument :
                - **mot** est le mot qui sera ajouté ou retiré de la liste des mots interdits.
                
                - **localization** :
                Cet argument a des sous-arguments :
                - **en** définit la langue du bot du serveur en anglais;
                - **fr** définit la langue du bot du serveur en français.
                
                - **joinrole** :
                Cet argument a des sous-arguments :
                - **reset** réinitialise le rôle configuré;
                - **identifiant/mention du rôle** définit le salon en fonction de son identifiant ou de sa mention.
                
                - **setprefix** :
                Cet argument a des sous-arguments :
                - **reset** réinitialise le préfixe configuré, le préfixe utilisé sera `sb!`;
                - **prefix** définit le préfixe spécifié.
                """);
        System.out.println("""
                The available arguments are:
                
                - **channelmember**:
                This argument has sub-arguments:
                - **join** concerns the welcome messages;
                - **leave** concerns the departure messages.
                These sub-arguments have sub-arguments:
                - **reset** resets the configured room, the welcome/departure messages will be sent in the default room;
                - **this** defines the salon where this order was sent;
                - **identifiant/mention du salon** defines the salon according to its identifier or mention.
                
                - **prohibitword**:
                This argument has sub-arguments:
                - **add** adds a word to the list of forbidden words;
                - **remove** remove a word from the list of forbidden words;
                - **reset** resets the entire list of forbidden words.
                The **add** and **remove** sub-arguments have a sub-argument:
                - **mot** is the word that will be added or removed from the list of forbidden words.
                
                - **localization**:
                This argument has sub-arguments:
                - **en** sets the server bot language to English;
                - **fr** sets the server bot language to French.
                
                - **joinrole**:
                This argument has sub-arguments:
                - **reset** resets the configured role;
                - **identifiant/mention du rôle** defines the salon according to its identifier or mention.
                
                - **setprefix**:
                This argument has sub-arguments:
                - **reset** resets the configured prefix, the prefix used will be `sb!`;
                - **prefix** defines the specified prefix.
                """);
        event.reply(stringBuilder.toString());
    }
}
