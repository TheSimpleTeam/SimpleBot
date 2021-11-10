package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class KickCommand extends Command {
    public KickCommand() {
        this.name = "kick";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.kick";
        this.example = "363811352688721930";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.kick";
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 1 && args.length != 2) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        Main.getJda().retrieveUserById(args[1].replaceAll("\\D+","")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.kick", event), user.getName(), MessageHelper.setReason(args[1], event)));
            event.getGuild().kick(member).queue();
        }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))),
                userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
