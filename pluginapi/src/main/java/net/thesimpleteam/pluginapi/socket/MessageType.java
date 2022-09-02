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

package net.thesimpleteam.pluginapi.socket;

import java.io.Serializable;
import java.util.Arrays;

public enum MessageType implements Serializable {

    // Server (Simple Bot) -> Client (Plugin Loader)

    PING("PONG"),
    GET_PLUGINS("SEND_PLUGINS"),
    TRIGGER_EVENTS("RECEIVED"),
    SHUTDOWN("RECEIVED"),

    // Client (Plugin Loader) -> Server (Simple Bot)

    PONG("RECEIVED"),
    SEND_PLUGINS("RECEIVED"),
    SEND_REGISTERED_COMMANDS("RECEIVED"),
    REPLY("RECEIVED"),

    // Both
    RECEIVED("RECEIVED"),
    ;

    public static final long serialVersionUID = -112410L;
    private final String[] reply;

    MessageType(String... reply) {
        this.reply = reply;
    }

    public MessageType[] getReplies() {
        return Arrays.stream(reply).map(MessageType::valueOf).toArray(MessageType[]::new);
    }

    public MessageType getReply() {
        return getReplies()[0];
    }
}
