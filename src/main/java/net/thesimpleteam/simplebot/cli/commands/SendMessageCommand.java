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

package net.thesimpleteam.simplebot.cli.commands;

import net.thesimpleteam.simplebot.SimpleBot;
import org.jetbrains.annotations.NotNull;

public class SendMessageCommand implements CLICommand {

    @Override
    public @NotNull String name() {
        return "msg";
    }

    @Override
    public String help() {
        return null;
    }

    @Override
    public void execute(CommandEvent event) {
        String msg = "Hello from CLI Commands";
        if(event.args().length == 0) {
            SimpleBot.LOGGER.warning("You should use arguments !");
        } else {
            msg = String.join(" ", event.args());
        }
        event.jda().getTextChannelById(754418833841717350L).sendMessage(msg).queue();
    }
}
