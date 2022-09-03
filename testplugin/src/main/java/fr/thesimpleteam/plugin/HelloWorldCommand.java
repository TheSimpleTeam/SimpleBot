/*
 * MIT License
 *
 * Copyright (c) 2022 minemobs
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

package fr.thesimpleteam.plugin;

import net.thesimpleteam.pluginapi.command.Command;
import net.thesimpleteam.pluginapi.command.CommandEvent;
import net.thesimpleteam.pluginapi.command.CommandInfo;
import net.thesimpleteam.pluginapi.plugins.BasePlugin;

@CommandInfo(name = "helloworld", description = "Hello World command", usage = "/helloworld", aliases = {"hw"})
public class HelloWorldCommand extends Command {

    public HelloWorldCommand(BasePlugin pl) {
        super(pl);
    }

    @Override
    public void execute(CommandEvent event) {
        event.reply("Hello World!");
    }
}
