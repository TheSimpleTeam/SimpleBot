package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.time.Instant;

public class KickCommand extends Command {
    public KickCommand() {
        this.name = "kick";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.kick";
        this.example = "363811352688721930 spam";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.kick";
        this.aliases = new String[]{"exclude"};
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 1) {
            MessageHelper.syntaxError(event, this, "informations.kick");
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed("error.commands.IDNull", event, null, null, null, (Object[]) null).build()).build());
            return;
        }
        Main.getJda().retrieveUserById(args[1].replaceAll("\\D+","")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.kick", event), user.getName(), args.length == 2 ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + " " + event.getArgs().substring(args[0].length() + args[1].length() + 2)));
            event.getGuild().kick(member).queue();
        }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))),
                userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
