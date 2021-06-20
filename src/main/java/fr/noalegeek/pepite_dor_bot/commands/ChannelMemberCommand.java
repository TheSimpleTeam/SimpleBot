package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.util.Locale;

public class ChannelMemberCommand extends Command {

    public ChannelMemberCommand() {
        this.name = "channelmember";
        this.cooldown = 5;
        this.help = "Défini le salon où les annonces des personnes qui rejoignent ou qui quittent le serveur avec son identifiant.";
        this.example = "join 657966618353074206";
        this.aliases = new String[]{"channelm","cmember","cm"};
        this.arguments = "<join|remove> <identifiant du salon>";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+
                    "Les arguments disponibles sont **join** et **remove**.\n" +
                    "L'argument **join** définira le salon où les annonces de bienvenues apparaîtront.\n" +
                    "L'argument **remove** définira le salon où les annonces de départs apparaîtront.");
            return;
        }
        GuildChannel channelMember = event.getGuild().getGuildChannelById(args[1]);
        if(channelMember == null){
            event.replyError("Ce salon n'existe pas.");
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)){
            case "join":
                Main.getServerConfig().
                        channelMemberJoin.put(event.getGuild().getId(),channelMember.getId());
                break;
            case "remove":
                Main.getServerConfig().channelMemberRemove.put(event.getGuild().getId(),channelMember.getId());
                break;
            default:
                event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"" +
                        "Les arguments disponibles sont **join** et **remove**.\n" +
                        "L'argument **join** définira le salon où les annonces de bienvenues apparaîtront.\n" +
                        "L'argument **remove** définira le salon où les annonces de départs apparaîtront.");
        }
        event.replySuccess("Le salon "+channelMember.getName()+" a bien été défini.");
    }
}
