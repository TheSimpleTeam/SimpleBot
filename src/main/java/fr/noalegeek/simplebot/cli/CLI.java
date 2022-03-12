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

package fr.noalegeek.simplebot.cli;

import com.google.common.collect.ImmutableList;
import fr.noalegeek.simplebot.SimpleBot;
import fr.noalegeek.simplebot.cli.commands.CLICommand;
import fr.noalegeek.simplebot.cli.commands.CommandEvent;
import net.dv8tion.jda.api.JDA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;

public class CLI {

    private final ImmutableList<CLICommand> commands;
    private final JDA jda;

    public CLI(JDA jda, CLICommand[] commands) {
        this.commands = ImmutableList.<CLICommand>builder().addAll(Arrays.asList(commands)).build();
        this.jda = jda;
    }

    public void commandsListener() throws InterruptedException, IOException {
        jda.awaitReady();
        do {
            InputStreamReader r = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(r);
            String nextLine = reader.readLine();
            String[] arg = nextLine.split("\\s+");
            Optional<CLICommand> opCommand = commands.stream().filter(cmd -> cmd.name().equalsIgnoreCase(arg[0]) || Arrays.stream(cmd.aliases())
                    .anyMatch(s -> s.equalsIgnoreCase(arg[0]))).findAny();
            if (opCommand.isPresent()) {
                CLICommand command = opCommand.get();
                command.execute(new CommandEvent(Arrays.copyOfRange(arg, 1, arg.length), jda, this));
            } else {
                SimpleBot.LOGGER.warning("This command does not exist !");
            }
        } while (true);
    }

    public ImmutableList<CLICommand> getCommands() {
        return commands;
    }
}
