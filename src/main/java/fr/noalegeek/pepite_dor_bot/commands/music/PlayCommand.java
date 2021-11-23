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

package fr.noalegeek.pepite_dor_bot.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class PlayCommand extends MusicCommand {

    public PlayCommand() {
        super();
        this.name = "play";
        this.cooldown = 5;
        this.help = "Play a song";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel() == null) {
            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            if(channel == null) {
                event.reply("You must be in a voice channel to use this command");
                return;
            }
            event.getGuild().getAudioManager().openAudioConnection(channel);
        }
        manager.loadTrack(event.getTextChannel(), event.getArgs());
    }
}
