package fr.noalegeek.pepite_dor_bot.commands.slashcommand;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public abstract class SlashCommand {

    private final CommandData data;

    public SlashCommand(@NotNull CommandData data) {
        this.data = data;
    }

    public CommandData getData() {
        return data;
    }

    protected abstract void execute(SlashCommandEvent event);

}
