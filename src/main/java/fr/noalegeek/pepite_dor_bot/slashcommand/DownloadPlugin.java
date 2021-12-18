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

package fr.noalegeek.pepite_dor_bot.slashcommand;

import com.jagrosh.jdautilities.command.SlashCommand;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadPlugin extends SlashCommand {

    public DownloadPlugin() {
        this.name = "downloadplugin";
        this.help = "Download plugin";
        this.guildOnly = true;
        this.ownerCommand = true;
        this.options.add(new OptionData(OptionType.STRING, "jarlink", "The link of the plugin"));
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        if (event.getOption("jar link") != null) {
            //Regex pattern to check if the link is a valid link
            try {
                URL url = new URL(event.getOption("jarlink").getAsString());
                byte[] outBytes = new byte[52_428_800];
                InputStream stream = url.openStream();

                IOUtils.read(stream, outBytes, 0, 52_428_800);

                if (stream.read() != -1) {
                    event.reply("The file is too big").setEphemeral(true).queue();
                }

                event.reply("Downloading plugin...").setEphemeral(true).queue(hook -> {
                    try {
                        IOUtils.write(outBytes, new FileOutputStream("plugins/" + FilenameUtils.getName(url.getPath())));
                    } catch (IOException e) {
                        MessageHelper.sendError(e, event, this);
                    }
                    hook.editOriginal("Plugin downloaded.\n Please restart the bot to load the plugin.").queue();
                });
            } catch (MalformedURLException e) {
                event.reply("The link is not valid").setEphemeral(true).queue();
            } catch (IOException e) {
                MessageHelper.sendError(e, event, this);
            }
        }
    }
}
