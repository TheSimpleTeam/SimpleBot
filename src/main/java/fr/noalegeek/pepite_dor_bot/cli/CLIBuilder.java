package fr.noalegeek.pepite_dor_bot.cli;

import fr.noalegeek.pepite_dor_bot.cli.commands.Command;
import net.dv8tion.jda.api.JDA;

public class CLIBuilder {

    private final JDA jda;
    private Command[] commands;

    public CLIBuilder(JDA jda) {
        this.jda = jda;
    }

    public CLIBuilder addCommand(Command... commands) {
        this.commands = commands;
        return this;
    }

    public CLI build() {
        return new CLI(jda, commands);
    }

}
