package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.entities.Role;

public class JoinRoleCommand extends Command {

    public JoinRoleCommand() {
        this.name = "joinrole";
        this.aliases = new String[]{"jr","joinr","jrole"};
        this.help = "help.joinRole";
        this.arguments = "arguments.joinRole";
        this.category = CommandCategories.CONFIG.category;
        this.cooldown = 5;
        this.example = "660083059089080321";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        /*if(!event.getMember().isOwner()){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.notOwner", event));
            return;
        }*/
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event,this) + MessageHelper.translateMessage("syntax.joinRole", event));
            return;
        }
        Role joinRole = event.getGuild().getRoleById(args[0].replaceAll("\\D+",""));
        if (joinRole == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.roleNull", event));
            return;
        } else if (joinRole.isManaged()) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.roleManaged", event));
            return;
        }
        String joinRoleId = Main.getServerConfig().guildJoinRole.get(event.getGuild().getId());
        if(args[0].equalsIgnoreCase("reset")){
            if(joinRoleId == null){
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.notConfigured", event));
                return;
            }
            Main.getServerConfig().guildJoinRole.remove(event.getGuild().getId());
        } else {
            if(joinRoleId == null || joinRoleId.equals(joinRole.getId())){
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.sameAsConfigured", event));
                return;
            }
            Main.getServerConfig().guildJoinRole.put(event.getGuild().getId(), joinRole.getId());
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.joinRole.configured", event));
        }
    }
}