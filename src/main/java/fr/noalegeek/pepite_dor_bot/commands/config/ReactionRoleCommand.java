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
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RegexPattern;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReactionRoleCommand extends Command {

    public ReactionRoleCommand() {
        this.name = "reactionrole";
        this.aliases = new String[]{"rr"};
    }

    @Override
    protected void execute(CommandEvent event) {
        //syntaxe: reactionrole <channel> <message> <reaction> <role>
        if(event.getArgs().isEmpty()) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if(args.length < 1) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        if(!args[0].matches(RegexPattern.CHANNEL_MENTION.pattern()) ||
                event.getGuild().getGuildChannelById(args[0].replaceAll(RegexPattern.CHANNEL_MENTION.pattern(), "")) == null) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        if(!(event.getGuild().getGuildChannelById(args[0].replaceAll(RegexPattern.CHANNEL_MENTION.pattern(), "")) instanceof TextChannel channel)) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        if(!args[1].matches("\\d{18}")) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        if(!args[2].matches(RegexPattern.CUSTOM_EMOJI.pattern()) || !Emoji.fromMarkdown(args[2]).isUnicode()) {
            MessageHelper.syntaxError(event, this);
            return;
        }
    }
}
