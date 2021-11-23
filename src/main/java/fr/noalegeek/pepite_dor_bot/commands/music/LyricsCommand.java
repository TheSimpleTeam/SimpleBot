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
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;

public class LyricsCommand extends MusicCommand {

    public LyricsCommand() {
        super();
        this.name = "lyrics";
        this.help = "Affiche les paroles de la chanson en cours";
        this.cooldown = 10;
        this.category = CommandCategories.MUSIC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel() == null) {
            event.reply("Le player n'a pas de piste en cours.");
            return;
        }
        String title = manager.getPlayer(event.getGuild()).getPlayer().getPlayingTrack().getInfo().title;

        title = title.startsWith(manager.getPlayer(event.getGuild()).getPlayer().getPlayingTrack().getInfo().author) ?
                title.replace(manager.getPlayer(event.getGuild()).getPlayer().getPlayingTrack().getInfo().author, "")
                        .replaceAll("\\(.*?\\)|\\[.*?\\]", "")
                        .replaceAll("[^a-zA-Z0-9’' ]", "").trim() : title;

        try {
            client.getLyrics(title).thenAccept(lyrics -> {
                if(lyrics == null) {
                    event.reply("Euh tu me poses une colle là. Fais une recherche google prsk je sais pas là.");
                } else {
                    event.reply(lyrics.getContent());
                }
            }).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
