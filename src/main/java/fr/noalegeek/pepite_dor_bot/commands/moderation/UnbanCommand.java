package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.name = "unban";
        this.arguments = "arguments.unban";
        this.aliases = new String[]{"pardon"};
        this.category = CommandCategories.STAFF.category;
        this.help = "help.unban";
        this.guildOnly = true;
        this.example = "285829396009451522 wrong person";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 2) {
            MessageHelper.syntaxError(event, this, "informations.unban");
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> {
            if (event.getGuild().retrieveBanList().complete().stream().anyMatch(ban -> ban.getUser() == user)) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.unban", event), user.getName(), event.getArgs() == null ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + event.getArgs().substring(args[0].length() + 1)));
                event.getGuild().unban(user).queue();
                return;
            }
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.unban", event), user.getName()));
        }, userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
