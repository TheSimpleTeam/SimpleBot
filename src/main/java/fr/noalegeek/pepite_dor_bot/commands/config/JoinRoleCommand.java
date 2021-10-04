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
        if(!event.getMember().isOwner()){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.notOwner", event));
            return;
        }
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event,this) + MessageHelper.translateMessage("syntax.joinRole", event));
            return;
        }
        if (event.getGuild().getRoleById(args[0].replaceAll("\\D+","")) == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.roleNull", event));
            return;
        } else if (event.getGuild().getRoleById(args[0].replaceAll("\\D+","")).isManaged()) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.roleManaged", event));
            return;
        }
        if(args[0].equalsIgnoreCase("reset")){
            if(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null){
                event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.notConfigured", event));
                return;
            }
            Main.getServerConfig().guildJoinRole().remove(event.getGuild().getId());
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.joinRole.reset", event));
        } else {
            if(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null || !Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()).equals(event.getGuild().getRoleById(args[0].replaceAll("\\D+","")).getId())){
                Main.getServerConfig().guildJoinRole().put(event.getGuild().getId(), event.getGuild().getRoleById(args[0].replaceAll("\\D+","")).getId());
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.joinRole.configured", event));
                return;
            }
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.sameAsConfigured", event));
        }
    }
}