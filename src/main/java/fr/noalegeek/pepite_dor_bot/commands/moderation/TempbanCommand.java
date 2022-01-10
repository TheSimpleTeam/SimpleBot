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
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.enums.Date;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

public class TempbanCommand extends Command {
    public TempbanCommand() {
        this.name = "tempban";
        this.arguments = "arguments.tempban";
        this.help = "help.tempban";
        this.category = CommandCategories.STAFF.category;
        this.cooldown = 5;
        this.example = "363811352688721930 1d flood";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 3 && args.length != 4) {
            MessageHelper.syntaxError(event, this, "syntax.tempban");
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            if(MessageHelper.cantInteract(event.getMember(), event.getSelfMember(), member, event)) return;
            try {
                int days  = Integer.parseInt(args[1]);
                if (days > 7) {
                    days = 7;
                    event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.commands.commandsBan", event));
                }
                if (Arrays.stream(Date.values()).filter(dates -> dates.name().equalsIgnoreCase(args[1].replaceAll("\\d+", "")) || dates.getSymbol().equalsIgnoreCase(args[1].replaceAll("\\d+", ""))).findFirst().isEmpty()) {
                    MessageHelper.syntaxError(event, this, "syntax.tempban");
                    return;
                }
                try {
                    member.ban(days, event.getArgs() == null ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + event.getArgs().substring(args[0].length() + args[1].length() + args[2].length() + 3)).queue(unused -> {
                        try {
                            Main.getServerConfig().tempBan().put(member.getId() + "-" + event.getGuild().getId(), ((LocalDateTime) LocalDateTime.class.getDeclaredMethod("plus" + StringUtils.capitalize(Arrays.stream(Date.values()).filter(dates -> dates.name().equalsIgnoreCase(args[1].replaceAll("\\d+", "")) || dates.getSymbol().equalsIgnoreCase(args[1].replaceAll("\\d+", ""))).findFirst().get().name().toLowerCase(Locale.ROOT)), long.class).invoke(LocalDateTime.now(), Integer.parseInt(args[2].replaceAll("\\D+", "")))).format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")));
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                            MessageHelper.sendError(exception, event, this);
                        }
                    });
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.ban", event), user.getName(), event.getArgs() == null ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + event.getArgs().substring(args[0].length() + args[1].length() + 2)));
                } catch (NumberFormatException exception) {
                    MessageHelper.syntaxError(event, this, "syntax.tempban");
                }
            } catch (NumberFormatException exception) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.ban.notAnNumber", event));
            }
        }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))), userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
