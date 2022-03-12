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

package fr.noalegeek.simplebot.cli.commands;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

import static fr.noalegeek.simplebot.enums.Color.*;

public class HelpCommand implements CLICommand {

    @NotNull
    @Override
    public String name() {
        return "help";
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public void execute(CommandEvent event) {
        if(event.args().length == 0) {
            for (CLICommand command : event.cli().getCommands()) {
                System.out.println();
                System.out.println(getHelp(command));
            }
            return;
        }
        Optional<CLICommand> command = event.cli().getCommands().stream().filter(cmd -> cmd.name().equalsIgnoreCase(event.args()[0]) || Arrays.stream(cmd.aliases())
                .anyMatch(s -> s.equalsIgnoreCase(event.args()[0]))).findAny();
        if(command.isEmpty()) {
            System.out.println("This command does not exist !");
            return;
        }
        System.out.println();
        System.out.println(getHelp(command.get()));
    }

    private String getHelp(CLICommand command) {
        return String.format("%s              %s", colorize(command.name(), RED),
                command.help() == null ? colorize("No help available", ANSI_ITALIC, BLACK_BOLD_BRIGHT) : colorize(command.help(), ANSI_ITALIC, BLACK_BOLD_BRIGHT));
    }


}
