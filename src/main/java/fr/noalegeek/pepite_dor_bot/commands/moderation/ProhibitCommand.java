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
        this.arguments = "arguments.prohibit";
        this.help = "help.prohibit";
        this.category = CommandCategories.STAFF.category;
        this.cooldown = 5;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        if(!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event.getGuild().getId()), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event.getGuild().getId()), Permission.MESSAGE_MANAGE.getName()));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2) {
            event.replyError(MessageHelper.syntaxError(event,this) + MessageHelper.translateMessage("syntax.prohibit", event.getGuild().getId()));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add":
                List<String> prohibitWords = Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords.get(event.getGuild().getId());
                if(prohibitWords.contains(args[1])){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.prohibit.wordAlreadyHere", event.getGuild().getId()), args[1]));
                    return;
                }
                prohibitWords.add(args[1]);
                Main.getServerConfig().prohibitWords.clear();
                Main.getServerConfig().prohibitWords.put(event.getGuild().getId(), prohibitWords);
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.prohibit.wordAdded", event.getGuild().getId()), args[1]));
                break;
            case "rem":
                prohibitWords = Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null ? new ArrayList<>() : Main.getServerConfig().prohibitWords.get(event.getGuild().getId());
                if(!prohibitWords.contains(args[1])){
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.prohibit.wordNotHere", event.getGuild().getId()), args[1]));
                    return;
                }
                prohibitWords.remove(args[1]);
                Main.getServerConfig().prohibitWords.clear();
                Main.getServerConfig().prohibitWords.put(event.getGuild().getId(), prohibitWords);
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.prohibit.wordRemoved", event.getGuild().getId()), args[1]));
                break;
            case "reset":
                if (Main.getServerConfig().prohibitWords.get(event.getGuild().getId()) == null) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.prohibit.listNull", event.getGuild().getId()));
                    return;
                }
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.prohibit.listReseted", event.getGuild().getId()));
                Main.getServerConfig().prohibitWords.remove(event.getGuild().getId());
                break;
            default:
                event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.prohibit", event.getGuild().getId()));
                break;
        }
    }

    public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
