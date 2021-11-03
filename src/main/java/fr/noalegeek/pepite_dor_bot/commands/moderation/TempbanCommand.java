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
import fr.noalegeek.pepite_dor_bot.utils.DiscordRegexPattern;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class TempbanCommand extends Command {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    public TempbanCommand() {
        this.name = "tempban";
        this.arguments = "arguments.commands.argumentsBan";
        this.help = "help.tempban";
        this.category = CommandCategories.STAFF.category;
        this.aliases = new String[]{"tempb", "tempba", "temb", "temban", "temba", "teb", "teban", "teba", "tb", "tban", "tba"};
        this.cooldown = 5;
        this.example = "363811352688721930 1d flood";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> {
            event.getGuild().retrieveMember(user).queue(member -> {
                if (!event.getMember().canInteract(member)) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userCantInteractTarget", event));
                    return;
                }
                if (!event.getSelfMember().canInteract(member)) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.botCantInteractTarget", event));
                    return;
                }
                if (args[1] == null || args[1].isEmpty()) args[1] = "7";
                String reason;
                if (args[2] == null || args[2].isEmpty())
                    reason = MessageHelper.translateMessage("text.commands.reasonNull", event);
                else
                    reason = MessageHelper.translateMessage("text.commands.reason", event) + args[2];
                try {
                    int banTime = Integer.parseInt(args[1]);
                    if (banTime > 7) {
                        banTime = 7;
                        event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.ban", event));
                    }
                    event.getGuild().ban(user, banTime).queue();
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.ban", event), user.getName(),
                            reason));
                } catch (NumberFormatException ex) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.ban.notAnNumber", event));
                }
            }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event)));
        }, userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
        String date = args[1].replaceAll("\\d+", "");
        Optional<Date> opDate = Arrays.stream(Date.values()).filter(dates -> dates.name().equalsIgnoreCase(date) || dates.getSymbol().equalsIgnoreCase(date)).findFirst();
        if (opDate.isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        Date dates = opDate.get();
        int time;
        try {
            time = Integer.parseInt(args[1].replaceAll("\\D+", ""));
        } catch (NumberFormatException ex) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        String reason = "No Reason";
        if (args.length >= 3) {
            reason = args[2];
        }
        Member m = event.getMessage().getMentionedMembers().get(0);
        m.ban(7, reason).queue(unused -> {
            try {
                Main.getServerConfig().tempBan().put(m.getId() + "-" + event.getGuild().getId(), ((LocalDateTime) LocalDateTime.class.getDeclaredMethod("plus" + StringUtils.capitalize(dates.name().toLowerCase(Locale.ROOT)), long.class).invoke(LocalDateTime.now(), time)).format(formatter));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                MessageHelper.sendError(e, event, this);
            }
        });
    }
}
