/*
 * MIT License
 *
 * Copyright (c) 2021 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.noalegeek.pepite_dor_bot.cli;

import com.google.common.collect.ImmutableList;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.JDA;
import net.thesimpleteam.simplebotplugin.commands.CLICommand;
import net.thesimpleteam.simplebotplugin.commands.CLICommandEvent;
import net.thesimpleteam.simplebotplugin.commands.ICLI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CLI implements ICLI {

    private final ImmutableList<CLICommand> commands;
    private final List<CLICommand> pluginCommands;
    private final JDA jda;

    public CLI(JDA jda, CLICommand[] commands) {
        this.commands = ImmutableList.<CLICommand>builder().addAll(Arrays.asList(commands)).build();
        this.jda = jda;
        this.pluginCommands = new ArrayList<>();
    }

    @Override
    public void commandsListener() throws InterruptedException, IOException {
        jda.awaitReady();
        while (true) {
            InputStreamReader r = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(r);
            String nextLine = reader.readLine();
            String[] arg = nextLine.split("\\s+");
            Optional<CLICommand> opCommand = commands.stream().filter(cmd -> cmd.name().equalsIgnoreCase(arg[0]) || Arrays.stream(cmd.aliases())
                    .anyMatch(s -> s.equalsIgnoreCase(arg[0]))).findAny();
            opCommand.ifPresentOrElse(cmd -> cmd.execute(new CLICommandEvent(Arrays.copyOfRange(arg, 1, arg.length), this)), () -> {
                var plCommands = pluginCommands.stream()
                        .filter(cmd -> cmd.name().equalsIgnoreCase(arg[0]) || Arrays.stream(cmd.aliases()).anyMatch(s -> s.equalsIgnoreCase(arg[0]))).findFirst();
                if (plCommands.isPresent()) {
                    plCommands.get().execute(new CLICommandEvent(Arrays.copyOfRange(arg, 1, arg.length), this));
                } else {
                    Main.LOGGER.warning("This command does not exist !");
                    Main.LOGGER.warning("List of plugins commands :%n%s".formatted(Arrays.toString(pluginCommands.stream().map(CLICommand::name).toArray())));
                }
            });
        }
    }

    @Override
    public ImmutableList<CLICommand> getCommands() {
        return commands;
    }

    @Override
    public List<CLICommand> getPluginCommands() {
        return pluginCommands;
    }
}
