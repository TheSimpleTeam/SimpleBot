package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProhibitCommand extends Command {

    public ProhibitCommand() {
        this.name = "prohibitword";
        this.aliases = new String[]{"prohibitw", "pword"};
        this.example = "add fuck";
        this.arguments = "arguments.prohibit";
        this.help = "help.prohibit";
        this.category = CommandCategories.STAFF.category;
        this.cooldown = 5;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2) {
            MessageHelper.syntaxError(event,this, MessageHelper.translateMessage("syntax.prohibit", event));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add":
                List<String> prohibitWords = Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() :
                        Main.getServerConfig().prohibitWords().get(event.getGuild().getId());
                if(prohibitWords.contains(args[1])){
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.prohibit.wordAlreadyHere", event), args[1]));
                    return;
                }
                prohibitWords.add(args[1]);
                Main.getServerConfig().prohibitWords().clear();
                Main.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.prohibit.wordAdded", event), args[1]));
                break;
            case "remove":
                prohibitWords = Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null ? new ArrayList<>() :
                        Main.getServerConfig().prohibitWords().get(event.getGuild().getId());
                if(!prohibitWords.contains(args[1])){
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.prohibit.wordNotHere", event), args[1]));
                    return;
                }
                prohibitWords.remove(args[1]);
                Main.getServerConfig().prohibitWords().clear();
                Main.getServerConfig().prohibitWords().put(event.getGuild().getId(), prohibitWords);
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.prohibit.wordRemoved", event), args[1]));
                break;
            case "reset":
                if (Main.getServerConfig().prohibitWords().get(event.getGuild().getId()) == null) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.prohibit.listNull", event));
                    return;
                }
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.prohibit.listReseted", event));
                Main.getServerConfig().prohibitWords().remove(event.getGuild().getId());
                break;
            default:
                MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.prohibit", event));
                break;
        }
    }
    /*public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }*/
}
