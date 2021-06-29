package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class ProhibitCommand extends Command {

    public ProhibitCommand() {
        this.name = "prohibitword";
        this.aliases = new String[]{"prw", "prohibitw"};
        this.example = "add prout";
        this.arguments = "<add/remove> <word>";
        this.category = CommandCategories.STAFF.category;
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.cooldown = 5;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2) {
            MessageHelper.syntaxError(this, event);
            return;
        }
        String option = args[0].toLowerCase();
        switch (option) {
            case "add":
                String word = args[1];
                List<String> words = Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords.get(
                        event.getGuild().getId());
                words.add(word);
                Main.getServerConfig().prohibitWords.clear();
                Main.getServerConfig().prohibitWords.put(event.getGuild().getId(), words);
                event.replySuccess("Le mot à bien été ajouté à la liste des mots interdits");
                break;
            //Todo: add more options
            default:
                MessageHelper.syntaxError(this, event);
                break;
        }
    }

    public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
