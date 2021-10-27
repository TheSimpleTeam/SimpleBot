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

package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.DiscordRegexPattern;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.entities.Member;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class TempbanCommand extends Command {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    public TempbanCommand() {
        this.name = "tempban";
        this.arguments = "<mention> <time> [Reason]";
        this.help = "help.tempban";
    }

    private enum Dates {
        SECONDS("s"),
        MINUTES("min"),
        HOURS("h"),
        DAYS("d"),
        WEEKS("w"),
        MONTHS("m"),
        YEARS("y");

        private final String s;

        Dates(String s) {
            this.s = s;
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        //TODO: Fix syntax error message.
        if(event.getArgs().isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }

        String[] args = event.getArgs().split("\\s+");
        if(args.length < 2 || !DiscordRegexPattern.USER_MENTION.matcher(args[0]).matches() || event.getMessage().getMentionedMembers().isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }

        String date = args[1].replaceAll("\\d+", "");
        Optional<Dates> opDate = Arrays.stream(Dates.values()).filter(dates -> dates.name().equalsIgnoreCase(date) || dates.s.equalsIgnoreCase(date)).findFirst();
        if (opDate.isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        Dates dates = opDate.get();
        int time;
        try {
            time = Integer.parseInt(args[1].replaceAll("\\D+", ""));
        } catch (NumberFormatException ex) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        String reason = "No Reason";
        if(args.length >= 3) {
            reason = args[2];
        }
        Member m = event.getMessage().getMentionedMembers().get(0);
        m.ban(7, reason).queue(unused -> {
            try {
                Main.getServerConfig().tempBan().put(m.getId() + "-" + event.getGuild().getId(),
                        ((LocalDateTime) LocalDateTime.class.getDeclaredMethod("plus" + uppercaseFirstLetter(dates), long.class).invoke(LocalDateTime.now(), time))
                                .format(formatter));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                MessageHelper.sendError(e, event, this);
            }
        });
    }

    private String uppercaseFirstLetter(Dates date) {
        StringBuilder b = new StringBuilder();
        char[] charArray = date.name().toLowerCase().toCharArray();
        for (int i = 0; i < date.name().toCharArray().length; i++) {
            if(i == 0) {
                b.append(String.valueOf(charArray[0]).toUpperCase());
            } else {
                b.append(charArray[i]);
            }
        }
        return b.toString();
    }
}
