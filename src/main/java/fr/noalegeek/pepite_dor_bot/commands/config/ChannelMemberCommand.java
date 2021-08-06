package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Locale;

public class ChannelMemberCommand extends Command {

    public ChannelMemberCommand() {
        this.name = "channelmember";
        this.cooldown = 5;
        this.help = "Défini le salon où les annonces des personnes qui rejoignent ou qui quittent le serveur avec son identifiant.";
        this.example = "join 657966618353074206";
        this.aliases = new String[]{"channelm","cmember","cm"};
        this.arguments = "<join|remove> <identifiant/mention du salon|reset|this>";
        this.category = CommandCategories.CONFIG.category;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        User author = event.getAuthor();
        if(author.isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        String syntaxError = "Les arguments disponibles sont **join** et **remove**.\n" +
                "- **join** définira le salon où les annonces de bienvenues apparaîtront.\n" +
                "- **remove** définira le salon où les annonces de départs apparaîtront.\n" +
                "Après ces arguments, les arguments disponibles sont **identifiant/mention du salon**, **reset** et **this**.\n" +
                "- **identifiant/mention du salon** définira le salon grâce à son indentifiant ou sa mention.\n" +
                "- **reset** réinitialisera le salon qui a été configuré\n" +
                "- **this** définira le salon où a été fait la commande.";
        if(args.length != 2){
            event.replyError(MessageHelper.syntaxError(author,this) + syntaxError);
            return;
        }
        Guild guild = event.getGuild();
        GuildChannel channelMember = guild.getGuildChannelById(args[1].replaceAll("\\D+", ""));
        if(channelMember == null){
            event.replyError("Ce salon n'existe pas.");
            return;
        }
        
        switch (args[0].toLowerCase(Locale.ROOT)){
            case "join":
                String channelMemberId = Main.getServerConfig().channelMemberJoin.get(guild.getId());
                switch (args[1].toLowerCase(Locale.ROOT)){
                    case "reset":
                        if(guild.getGuildChannelById(channelMemberId) == null){
                            event.replyError(MessageHelper.formattedMention(author) + "Le salon n'a pas été configuré donc vous ne pouvez pas le réinitialiser.");
                            return;
                        }
                        Main.getServerConfig().channelMemberJoin.remove(guild.getId());
                        break;
                    case "this":
                        if(guild.getGuildChannelById(channelMemberId).equals(guild.getGuildChannelById(event.getChannel().getId()))){
                            event.replyError(MessageHelper.formattedMention(author) + "Le salon que vous voulez changer pour les messages de bienvenue est le même que celui configuré actuellement.");
                            return;
                        }
                        Main.getServerConfig().channelMemberJoin.put(guild.getId(),channelMember.getId());
                        break;
                    default:
                        if(guild.getGuildChannelById(channelMemberId) == null && guild.getGuildChannelById(channelMemberId).equals(channelMember)){
                            event.replyError(MessageHelper.formattedMention(author) + "Le salon que vous voulez changer pour les messages de bienvenue est le même que celui configuré actuellement.");
                            return;
                        }
                        Main.getServerConfig().channelMemberJoin.put(guild.getId(),channelMember.getId());
                        break;
                }
                break;
            case "remove":
                channelMemberId = Main.getServerConfig().channelMemberRemove.get(guild.getId());
                switch (args[1].toLowerCase(Locale.ROOT)){
                    case "reset":
                        if(guild.getGuildChannelById(channelMemberId) == null){
                            event.replyError(MessageHelper.formattedMention(author) + "Le salon n'a pas été configuré donc vous ne pouvez pas le réinitialiser.");
                            return;
                        }
                        Main.getServerConfig().channelMemberRemove.remove(guild.getId());
                        break;
                    case "this":
                        if(guild.getGuildChannelById(channelMemberId).equals(guild.getGuildChannelById(event.getChannel().getId()))){
                            event.replyError(MessageHelper.formattedMention(author) + "Le salon que vous voulez changer pour les messages de bienvenue est le même que celui configuré actuellement.");
                            return;
                        }
                        Main.getServerConfig().channelMemberRemove.put(guild.getId(),channelMember.getId());
                        break;
                    default:
                        if(guild.getGuildChannelById(channelMemberId).equals(channelMember)){
                            event.replyError(MessageHelper.formattedMention(author) + "Le salon que vous voulez changer pour les messages de bienvenue est le même que celui configuré actuellement.");
                            return;
                        }
                        Main.getServerConfig().channelMemberRemove.put(guild.getId(),channelMember.getId());
                        break;
                }
                break;
            default:
                event.replyError(MessageHelper.syntaxError(author,this) + syntaxError);
        }
        event.replySuccess("Le salon " + channelMember.getAsMention() + " a bien été défini.");
    }
}
