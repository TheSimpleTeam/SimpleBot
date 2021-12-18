package fr.noalegeek.pepite_dor_bot.cli;

import net.thesimpleteam.simplebotplugin.commands.CLICommand;
import net.dv8tion.jda.api.JDA;

public class CLIBuilder {

    private final JDA jda;
    private CLICommand[] commands;

    public CLIBuilder(JDA jda) {
        this.jda = jda;
    }

    public CLIBuilder addCommand(CLICommand... commands) {
        this.commands = commands;
        return this;
    }

    public CLI build() {
        return new CLI(jda, commands);
    }

}
