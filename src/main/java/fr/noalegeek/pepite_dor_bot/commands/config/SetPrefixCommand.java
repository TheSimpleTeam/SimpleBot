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

package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import net.dv8tion.jda.api.Permission;

import java.io.File;
import java.io.IOException;

public class SetPrefixCommand extends Command {

    public SetPrefixCommand() {
        this.name = "setprefix";
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            event.replyError("You need at least one argument");
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if(Main.getServerConfig().prefix() == null) {
            try {
                new File("config/server-config.json").delete();
                Main.setupServerConfig();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if(Main.getInfos().prefix().equalsIgnoreCase(args[0]) && Main.getServerConfig().prefix().containsKey(event.getGuild().getId())) {
            Main.getServerConfig().prefix().remove(event.getGuild().getId());
            event.reply("This prefix has been reset.");
            return;
        }

        Main.getServerConfig().prefix().put(event.getGuild().getId(), args[0]);
        event.replySuccess("My new prefix is **" + args[0] + "**");
    }

}
