package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

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
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            MessageHelper.syntaxError(event,this, MessageHelper.translateMessage("syntax.joinRole", event));
            return;
        }
        if (event.getGuild().getRoleById(args[0].replaceAll("\\D+","")) == null) {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.roleNull", event));
            return;
        }
        if (event.getGuild().getRoleById(args[0].replaceAll("\\D+","")).isManaged()) {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.roleManaged", event));
            return;
        }
        if(args[0].equalsIgnoreCase("reset")){
            if(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null){
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.notConfigured", event));
                return;
            }
            Main.getServerConfig().guildJoinRole().remove(event.getGuild().getId());
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.joinRole.reset", event));
        } else {
            if(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null || !Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()).equals(event.getGuild().getRoleById(args[0].replaceAll("\\D+","")).getId())){
                Main.getServerConfig().guildJoinRole().put(event.getGuild().getId(), event.getGuild().getRoleById(args[0].replaceAll("\\D+","")).getId());
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("success.joinRole.configured", event));
                return;
            }
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.joinRole.sameAsConfigured", event));
        }
    }
}