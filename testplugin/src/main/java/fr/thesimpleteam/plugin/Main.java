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

import fr.thesimpleteam.pluginapi.BasePlugin;
import fr.thesimpleteam.pluginapi.Plugin;
import fr.thesimpleteam.pluginapi.event.EventHandler;
import fr.thesimpleteam.pluginapi.event.Listener;
import fr.thesimpleteam.pluginapi.event.MessageReceiveEvent;

@Plugin(name = "SimpleTeam", version = "1.0", author = "minemobs", description = "SimpleTeam's test plugin")
public class Main extends BasePlugin implements Listener {

    @EventHandler
    public void onMessage(MessageReceiveEvent event) {
        event.reply("Receieved message from " + event.getAuthorName() + " : " + event.getMessage());
    }

    @Override
    public void onEnable() {
        System.out.println("Enabled");
        getLoader().addListener(this, this);
    }

    @Override
    public void onDisable() {
        System.out.println("Disabled");
    }
}