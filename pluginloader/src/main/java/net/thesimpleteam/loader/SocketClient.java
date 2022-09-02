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

package net.thesimpleteam.loader;

import net.thesimpleteam.pluginapi.socket.MessageType;
import net.thesimpleteam.pluginapi.socket.SocketMessage;
import net.thesimpleteam.pluginapi.socket.SocketMessageListener;
import net.thesimpleteam.pluginapi.utils.SerializationUtils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class SocketClient {

    private final List<SocketMessageListener> listeners = new ArrayList<>();
    private final Socket client;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    SocketClient(PluginLoader loader) {
        try {
            client = new Socket("localhost", 10909);
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
            this.addListener(loader);
            listenForMessages();
        } catch (Exception e) {
            PluginLoader.LOGGER.log(Level.SEVERE, "Error while connecting to socket server", e);
            throw new RuntimeException(e);
        }
    }

    public void addListener(SocketMessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SocketMessageListener listener) {
        listeners.remove(listener);
    }

    public void listenForMessages() {
        PluginLoader.EXECUTOR_SERVICE.execute(() -> {
            while (true) {
                try {
                    if(in.readObject() instanceof SocketMessage message) {
                        listeners.forEach(listener -> listener.onMessage(message));
                    }
                } catch (EOFException ignored) {} catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Optional<SocketMessage> sendMessage(SocketMessage message) {
        try {
            out.writeObject(SerializationUtils.serialize(message));
            out.flush();
            if(message.getMessageType().getReply() == MessageType.RECEIVED) return Optional.empty();
            return Optional.of((SocketMessage) in.readObject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<SocketMessage> sendMessage(MessageType type) {
        return sendMessage(new SocketMessage(type));
    }

    public Optional<SocketMessage> sendMessage(MessageType type, Serializable object) {
        return sendMessage(new SocketMessage(type, object));
    }

    void close() {
        try {
            client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
