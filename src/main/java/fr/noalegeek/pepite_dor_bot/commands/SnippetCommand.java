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

package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.DiscordFormatUtils;
import net.dv8tion.jda.api.entities.Message;
import sh.stein.carbon.CarbonService;
import sh.stein.carbon.ImageOptions;
import sh.stein.carbon.PlaywrightCarbonService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SnippetCommand extends Command {

    private final CarbonService carbon = new PlaywrightCarbonService();

    public SnippetCommand() {
        this.name = "snippet";
        this.aliases = new String[]{"paste", "snip", "gist", "gists", "carbon"};
        this.cooldown = 5;
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        ImageOptions.Language language = ImageOptions.Language.Auto;
        if(args.startsWith(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format)) {
            if(getLanguage(args.split("\n")[0].replaceAll(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format, "")) != ImageOptions.Language.Auto) {
                language = getLanguage(args.split("\n")[0].replaceAll(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format, ""));
            }
        }
        List<String> list = new LinkedList<>(Arrays.asList(args.split("\n")));
        list.remove(0);
        list.remove(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format);
        args = String.join("\n", list);
        final ImageOptions options = new ImageOptions.ImageOptionsBuilder()
                .language(language)
                .fontFamily(ImageOptions.FontFamily.JetBrainsMono)
                .theme(ImageOptions.Theme.NightOwl)
                .build();
        AtomicReference<Message> m = new AtomicReference<>();
        event.getMessage().reply("It might take time to execute the command !").queue(m::set);
        event.getMessage().reply(carbon.getImage(args, options), "code.png").mentionRepliedUser(false).queue(unused -> m.get().delete().queue());
    }

    private ImageOptions.Language getLanguage(String language) {
        for (ImageOptions.Language value : ImageOptions.Language.values()) {
            switch (language.toLowerCase()) {
                case "javascript", "js" -> {
                    return ImageOptions.Language.JavaScript;
                }
                case "python", "py" -> {
                    return ImageOptions.Language.Python;
                }
                case "typescript", "ts" -> {
                    return ImageOptions.Language.TypeScript;
                }
                case "kotlin", "kt" -> {
                    return ImageOptions.Language.Kotlin;
                }
                default -> {
                    if(language.equalsIgnoreCase(value.name())) return value;
                }
            }
        }
        return ImageOptions.Language.Auto;
    }
}