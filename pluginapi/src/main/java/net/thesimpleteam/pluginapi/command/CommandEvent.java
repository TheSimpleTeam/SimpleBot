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

import net.thesimpleteam.pluginapi.message.Message;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class CommandEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = -44454884058L;

    private final CommandInfo info;
    private final Command command;
    private final Message message;

    public CommandEvent(Command command, Message message) {
        this.command = command;
        this.info = command.getInfo();
        this.message = message;
    }

    public CommandEvent(CommandInfo info, Message message) {
        this.info = info;
        this.command = null;
        this.message = message;
    }

    public void reply(String message) {
        command.getPlugin().getLoader().reply(message, this.message.getChannelID());
    }

    public Command getCommand() {
        return command;
    }

    public Message getMessage() {
        return message;
    }

    public CommandInfo getCommandInfo() {
        return info;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CommandEvent) obj;
        return Objects.equals(this.command, that.command) &&
                Objects.equals(this.message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, message);
    }

    @Override
    public String toString() {
        return "CommandEvent[" +
                "command=" + command + ", " +
                "message=" + message + ']';
    }
}