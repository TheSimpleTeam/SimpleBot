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
import fr.noalegeek.pepite_dor_bot.utils.RegexPattern;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class TrackCommand extends MusicCommand {

    public TrackCommand() {
        super();
        this.name = "track";
        this.help = "Affiche le titre de la chanson en cours";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel() == null) {
            event.reply("Le player n'a pas de piste en cours.");
            return;
        }

        var player = manager.getPlayer(event.getGuild());
        var audioPlayer = player.getPlayer();

        if(player.getListener().getTrackCount() == 1) {
            event.reply("Piste en cours : " + audioPlayer.getPlayingTrack().getInfo().title);
        } else {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Pistes en cours")
                    .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                    .setColor(new Color(204, 93, 54))
                    .addField(audioPlayer.getPlayingTrack().getInfo().title, "Durée : " + TimeUnit.MILLISECONDS.toSeconds(audioPlayer.getPlayingTrack().getInfo().length)
                            + " secondes.", false)
                    .setTimestamp(Instant.now());
            Matcher matcher = RegexPattern.YOUTUBE_URL.matcher(audioPlayer.getPlayingTrack().getInfo().uri);
            if(matcher.matches()) {
                builder.setThumbnail("https://img.youtube.com/vi/" + matcher.group(5) + "/default.jpg");
            }
            player.getListener().getTracks().forEach(track ->
                    builder.addField(track.getInfo().title, "Durée : " + TimeUnit.MILLISECONDS.toSeconds(track.getInfo().length) + " secondes.\n" +
                            "Dans: " + TimeUnit.MILLISECONDS.toSeconds(track.getPosition()) + " secondes", false));
            event.reply(builder.build());
        }
    }
}
