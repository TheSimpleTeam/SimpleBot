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
import fr.noalegeek.pepite_dor_bot.commands.MathsCommand;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.listener.Listener;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        if (args.length < 3) {
            MessageHelper.syntaxError(event, this, "informations.tempban");
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed(MessageHelper.translateMessage("error.commands.IDNull", event), event).build()).build());
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            if(MessageHelper.cantInteract(event.getMember(), event.getSelfMember(), member, event)) return;
            try {
                int days  = Integer.parseInt(args[1]);
                if (days > 7) {
                    days = 7;
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.commands.commandsBan", event));
                }
                if (MathsCommand.Unit.getAllSymbolsByType(MathsCommand.UnitType.TIME).stream().filter(symbolDate -> symbolDate.equalsIgnoreCase(args[2].replaceAll("\\d+", ""))).findFirst().isEmpty()) {
                    MessageHelper.syntaxError(event, this, "informations.tempban");
                    return;
                }
                if(Arrays.stream(MathsCommand.Date.values()).filter(date -> date.name().equals(args[2].replaceAll("\\d+", ""))).findFirst().get().factor * Double.parseDouble(args[2].replaceAll("\\D+", "")) > 3155760000D){
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(MessageHelper.translateMessage("error.tempban.timeTooLarge", event), event).build()).build());
                    return;
                }
                try {
                    member.ban(days, args.length == 3 ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + " " + event.getArgs().substring(args[0].length() + args[1].length() + args[2].length() + 3)).queue(unused -> {
                        try {
                            Main.getServerConfig().tempBan().put(member.getId() + "-" + event.getGuild().getId(), ((LocalDateTime) LocalDateTime.class.getDeclaredMethod("plus" + StringUtils.capitalize(Arrays.stream(MathsCommand.Date.values()).filter(dates -> dates.name().equalsIgnoreCase(args[2].replaceAll("\\d+", ""))).findFirst().get().functionName.toLowerCase(Locale.ROOT)), long.class).invoke(LocalDateTime.now(), Integer.parseInt(args[2].replaceAll("\\D+", "")))).format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")));
                            try {
                                Listener.saveConfigs();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                            MessageHelper.sendError(exception, event, this);
                        }
                        event.reply(new MessageBuilder(new EmbedBuilder()
                                .setTitle(new StringBuilder().append(UnicodeCharacters.whiteHeavyCheckMarkEmoji).append(" ").append(String.format(MessageHelper.translateMessage("success.tempban", event), user.getName(), args.length == 3 ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + " " + event.getArgs().substring(args[0].length() + args[1].length() + args[2].length() + 3), MathsCommand.dateTime(args[2], event))).toString())
                                .setColor(Color.GREEN)
                                .setTimestamp(Instant.now())
                                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl()).build()).build());
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(MessageHelper.translateMessage("success.tempban", event), event).build()).build());
                    });
                } catch (NumberFormatException exception) {
                    MessageHelper.syntaxError(event, this, "informations.tempban");
                }
            } catch (NumberFormatException exception) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.ban.notAnNumber", event));
            }
        }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event))), userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
