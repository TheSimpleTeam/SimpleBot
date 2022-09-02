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

package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.thesimpleteam.pluginapi.plugins.Plugin;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.plugins.PluginService;
import net.thesimpleteam.simplebot.utils.MessageHelper;

import java.awt.Color;

public class PluginsCommand extends Command {

    public PluginsCommand() {
        this.name = "plugins";
        this.aliases = new String[]{"plugin", "pl"};
        this.help = "help.plugins";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder embed = MessageHelper.getEmbed(event, "Plugins", Color.magenta, "List of plugins loaded", null);
        for (Plugin plugin : PluginService.getPlugins()) {
            embed.addField(plugin.name(), "Description: " + plugin.description() + "\n" + "Version: " + plugin.version() + "\n" + "Author: " + plugin.author(), false);
        }
        event.reply(embed.build());
    }
}
