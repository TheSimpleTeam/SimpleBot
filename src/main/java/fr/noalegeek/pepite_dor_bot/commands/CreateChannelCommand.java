package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class CreateChannelCommand extends Command {

    public CreateChannelCommand() {
        this.name = "createchannel";
        this.cooldown = 30;
        this.arguments = "arguments.createChannel";
        this.help = "help.createChannel";
        this.example = "text général 846048803554852905";
        this.category = CommandCategories.STAFF.category;
        this.guildOnly = true;
        this.aliases = new String[]{"createchannel","createchanne","createchann","createchan","createcha","createch","createc","creatchannel","creatchanne","creatchann","creatchan","creatcha","creatch","creatc","creachannel","creachanne","creachann","creachan","creacha","creach","creac","crechannel","crechanne","crechann","crechan","crecha","crech","crec","crchannel","crchanne","crchann","crchan","crcha","crch","crc","cchannel","cchanne","cchann","cchan","ccha","cch","cc"};
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3){
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.createChannel", event));
            return;
        }
        if (args[1].toCharArray().length > 100) {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.createChannel.tooManyCharacters", event));
            return;
        }
        switch (args[0]) {
            case "text" -> event.getGuild().createTextChannel(args[1], event.getGuild().getCategoryById(args[2])).queue();
            case "voice" -> event.getGuild().createVoiceChannel(args[1], event.getGuild().getCategoryById(args[2])).queue();
            default -> MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.createChannel", event));
        }
    }
}
