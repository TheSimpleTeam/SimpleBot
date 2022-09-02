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

package net.thesimpleteam.pluginapi.message;

import net.thesimpleteam.pluginapi.plugins.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 */
public final class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = -44454884057L;
    private final String message;
    private final String channelID;
    private final String authorID;
    private final String authorName;
    private Plugin fromPlugin;

    /**
     * @param message The content of the message
     * @param channelID The channel ID in which the message was sent or will be sent
     * @param authorID  The author ID of the message or null if the message is sent by the bot
     * @param authorName The author name of the message or null if the message is sent by the bot
     */
    public Message(@NotNull String message, @NotNull String channelID, @Nullable String authorID, @Nullable String authorName) {
        this.message = Objects.requireNonNull(message);
        this.channelID = Objects.requireNonNull(channelID);
        this.authorID = authorID;
        this.authorName = authorName;
    }

    public String getMessageContent() {
        return message;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getAuthorName() {
        return authorName;
    }

    /**
     * @return The plugin that sent the message or null if it wasn't sent by a plugin
     */
    public Plugin getFromPlugin() {
        return fromPlugin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Message) obj;
        return Objects.equals(this.message, that.message) &&
                Objects.equals(this.channelID, that.channelID) &&
                Objects.equals(this.authorID, that.authorID) &&
                Objects.equals(this.authorName, that.authorName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, channelID, authorID, authorName);
    }

    @Override
    public String toString() {
        return "Message[" +
                "message=" + message + ", " +
                "channelID=" + channelID + ", " +
                "authorID=" + authorID + ", " +
                "authorName=" + authorName + ']';
    }
}