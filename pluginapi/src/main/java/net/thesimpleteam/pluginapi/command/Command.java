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

package net.thesimpleteam.pluginapi.command;

import net.thesimpleteam.pluginapi.plugins.BasePlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Command {

    private final CommandInfo info;
    private final BasePlugin plugin;

    protected Command(@NotNull BasePlugin plugin) {
        this.info = this.getClass().getAnnotation(CommandInfo.class);
        if(this.info == null) throw new IllegalArgumentException("CommandInfo annotation is required!");
        this.plugin = Objects.requireNonNull(plugin);
    }

    public abstract void execute(CommandEvent event);

    public CommandInfo getInfo() {
        return info;
    }

    public BasePlugin getPlugin() {
        return plugin;
    }

    public String getName() {
        return this.info.name();
    }

    public String getDescription() {
        return this.info.description();
    }

    public String getUsage() {
        return this.info.usage();
    }

    public String[] getAliases() {
        return this.info.aliases();
    }
}
