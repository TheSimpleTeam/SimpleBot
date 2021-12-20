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

package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class SetPrefixCommand extends Command {

    public SetPrefixCommand() {
        this.name = "setprefix";
        this.arguments = "arguments.setPrefix";
        this.example = "@";
        this.aliases = new String[]{"sprefix", "sp", "setp"};
        this.help = "help.setPrefix";
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if(Main.getServerConfig().prefix() == null) {
            try {
                new File("config/server-config.json").delete();
                Main.setupServerConfig();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if(args[0].equalsIgnoreCase("reset")){
            if(!Main.getServerConfig().prefix().containsKey(event.getGuild().getId())){
                EmbedBuilder errorPrefixNullEmbed = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTimestamp(Instant.now())
                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                        .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.setPrefix.notConfigured", event));
                event.reply(new MessageBuilder(errorPrefixNullEmbed.build()).build());
                return;
            }
            Main.getServerConfig().prefix().remove(event.getGuild().getId());
            return;
        }
        if(args[0].equals(Main.getServerConfig().prefix().get(event.getGuild().getId()))){
            EmbedBuilder errorSameAsConfiguredEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.setPrefix.sameAsConfigured", event));
            event.reply(new MessageBuilder(errorSameAsConfiguredEmbed.build()).build());
            return;
        }
        Main.getServerConfig().prefix().put(event.getGuild().getId(), args[0]);
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + String.format(MessageHelper.translateMessage("success.setPrefix.configured", event), args[0]));
        event.reply(new MessageBuilder(successEmbed.build()).build());
    }

}
