package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProhibitCommand extends Command {

    public ProhibitCommand() {
        this.name = "prohibitword";
        this.aliases = new String[]{"prohibitw","prohitbitwrd","pw","pwrd","pword"};
        this.example = "add prout";
        this.arguments = "<add|rem|reset> <mot>";
        this.help = "Ajoute ou enlève un mot dans la liste de mots interdits. Peut même réinitialiser la liste complète des mots interdits.";
        this.category = CommandCategories.STAFF.category;
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.cooldown = 5;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2) {
            event.replyError(MessageHelper.syntaxError(event,this) + "Les arguments disponibles sont **add**, **rem** et **reset**.\n" +
                    "- **add** ajoutera un mot à la liste des mots interdits.\n" +
                    "- **rem** enlèvera un mot présent dans la liste des mots interdits.\n" +
                    "- **reset** réinitialisera la liste des mots interdits au complet.\n" +
                    "Après les arguments **add** et **rem**, vous devez mettre l'argument **mot** qui correspondra au mot qui va être ajouter ou enlever.");
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add":
                List<String> prohibitWords = Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords.get(event.getGuild().getId());
                if(prohibitWords.contains(args[1])){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le mot `" + args[1] + "` est déjà présente dans la liste des mots interdits donc vous ne pouvez pas l'ajouter dans la liste.");
                    return;
                }
                prohibitWords.add(args[1]);
                Main.getServerConfig().prohibitWords.clear();
                Main.getServerConfig().prohibitWords.put(event.getGuild().getId(), prohibitWords);
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "Le mot `" + args[1] + "` a bien été ajouté à la liste des mots interdits.");
                break;
            case "rem":
                prohibitWords = Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords.get(event.getGuild().getId());
                if(!prohibitWords.contains(args[1])){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le mot `" + args[1] + "` n'est pas présente dans la liste des mots interdits donc vous ne pouvez pas l'enlever de la liste.");
                    return;
                }
                prohibitWords.remove(args[1]);
                Main.getServerConfig().prohibitWords.clear();
                Main.getServerConfig().prohibitWords.put(event.getGuild().getId(), prohibitWords);
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + "Le mot `" + args[1] + "` a bien été enlevé à la liste des mots interdits.");
                break;
            case "reset":
                if (Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous ne pouvez pas réinitialiser la liste des mots interdits car elle est déjà vide.");
                    return;
                }
                Main.getServerConfig().prohibitWords.remove(event.getGuild().getId());
                break;
            default:
                event.replyError(MessageHelper.syntaxError(event, this) + "");
                break;
        }
    }

    public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
