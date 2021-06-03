package fr.noalegeek.pepite_dor_bot.commands.slashcommand;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Set;

public class SlashCommandListener extends ListenerAdapter {

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getGuild() == null)
            return;
        Reflections reflections = new Reflections("fr.noalegeek.pepite_dor_bot.commands.slashcommand");
        Set<Class<? extends SlashCommand>> commands = reflections.getSubTypesOf(SlashCommand.class);
        for (Class<? extends SlashCommand> command : commands) {
            try {
                SlashCommand slashCommand = command.newInstance();
                if(event.getName().equalsIgnoreCase(slashCommand.getData().getName())) slashCommand.execute(event);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
