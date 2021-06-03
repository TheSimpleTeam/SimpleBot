package fr.noalegeek.pepite_dor_bot.commands.slashcommand;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class TestSlashCommand extends SlashCommand{

    public TestSlashCommand() {
        super(new CommandData("test", "no Desc"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply("Hello, World").setEphemeral(true).queue();
    }
}
