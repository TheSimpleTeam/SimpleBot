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
        this.aliases = new String[]{"createc", "cc", "ccommand"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMember().hasPermission(Permission.MANAGE_CHANNEL)){
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.userHasNotPermission", event),
                    Permission.MANAGE_CHANNEL.getName()));
            return;
        }
        if(!event.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)){
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.commands.botHasNotPermission", event),
                    Permission.MANAGE_CHANNEL.getName()));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3){
            event.reply(MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.createChannel", event)));
            return;
        }
        if (args[1].toCharArray().length > 100) {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.createChannel.tooManyCharacters", event));
            return;
        }
        switch (args[0]) {
            case "text" -> event.getGuild().createTextChannel(args[2], event.getGuild().getCategoryById(args[2])).queue();
            case "voice" -> event.getGuild().createVoiceChannel(args[2], event.getGuild().getCategoryById(args[2])).queue();
            default -> event.reply(MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.createChannel", event)));
        }
    }
}
