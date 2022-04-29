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

package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.enums.DiscordFormatUtils;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import sh.stein.carbon.CarbonService;
import sh.stein.carbon.ImageOptions;
import sh.stein.carbon.PlaywrightCarbonService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SnippetCommand extends Command {

    private final CarbonService carbon = new PlaywrightCarbonService();

    public SnippetCommand() {
        this.name = "snippet";
        this.aliases = new String[]{"gist", "paste", "carbon", "sn"};
        this.cooldown = 5;
        this.help = "help.snippet";
        this.example = """
                ```public static void main(String[] args){
                  System.out.println("Hello World");
                }```""";
        this.arguments = "arguments.snippet";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        ImageOptions.Language language = ImageOptions.Language.Auto;
        if(event.getArgs().startsWith(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format) && getLanguage(event.getArgs().split("\n")[0].replaceAll(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format, "")) != ImageOptions.Language.Auto) language = getLanguage(event.getArgs().split("\n")[0].replaceAll(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format, ""));
        List<String> list = new LinkedList<>(Arrays.asList(event.getArgs().split("\n")));
        list.remove(0);
        list.remove(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format);
        final ImageOptions options = new ImageOptions.ImageOptionsBuilder()
                .language(language)
                .fontFamily(ImageOptions.FontFamily.JetBrainsMono)
                .theme(ImageOptions.Theme.NightOwl)
                .build();
        event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event, "warning.commands.takeTime", null, null, null).build()).build()).queue(warningTakeTimeMessage -> event.getMessage().reply(carbon.getImage(String.join("\n", list), options), "code.png").mentionRepliedUser(true).queue(unused -> warningTakeTimeMessage.delete().queue()));
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