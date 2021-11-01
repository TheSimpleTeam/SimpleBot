package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;

public class TestCommand extends Command {

    public TestCommand() {
        this.category = CommandCategories.MISC.category;
        this.help = "help.test";
        this.cooldown = 5;
        this.name = "test";
        this.hidden = true;
        this.aliases = new String[]{"t", "te", "tes"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        StackWalker stackWalker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        event.reply(stackWalker.walk(stackFrameStream -> stackFrameStream.skip(3).findFirst().orElseThrow()).getMethodName());
    }
}
