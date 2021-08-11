package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;

public class TestLocalizationCommand extends Command {
    
    public TestLocalizationCommand() {
        this.name = "testlocalization";
        this.aliases = new String[]{"tlc"};
    }
    
    @Override
    protected void execute(CommandEvent event) {
        event.reply(MessageHelper.sendTranslatedMessage("msg.example", event.getGuild().getId()));
    }
}
