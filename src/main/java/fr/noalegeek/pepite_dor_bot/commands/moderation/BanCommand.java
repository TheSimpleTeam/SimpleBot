package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;

import java.awt.*;
import java.time.Instant;

public class BanCommand extends Command {
    public BanCommand() {
        this.name = "ban";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "arguments.ban";
        this.example = "363811352688721930 7 spam";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.ban";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 2) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("informations.ban", event));
            return;
        }
        if(args[0].isEmpty()){
            event.reply(new MessageBuilder(new EmbedBuilder()
                    .setTitle(new StringBuilder().append(UnicodeCharacters.crossMarkEmoji).append(" ").append(MessageHelper.translateMessage("error.commands.IDNull", event)).toString())
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl()).build()).build());
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> {
            if (event.getGuild().retrieveBanList().complete().stream().anyMatch(ban -> ban.getUser() == user)) {
                event.getGuild().unban(user).queue(unused -> event.reply(String.format(MessageHelper.translateMessage("success.unban", event), user.getName())));
            } else {
                event.getGuild().retrieveMember(user).queue(member -> {
                    if(MessageHelper.cantInteract(event.getMember(), event.getSelfMember(), member, event)) return;
                    if (args[1] == null || args[1].isEmpty()) args[1] = "7";
                    try {
                        int days = Integer.parseInt(args[1]);
                        if (days > 7) {
                            days = 7;
                            event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.ban", event));
                        }
                        event.getGuild().ban(user, days).queue();
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.ban", event), user.getName(), args.length == 2 ? MessageHelper.translateMessage("text.commands.reasonNull", event) : MessageHelper.translateMessage("text.commands.reason", event) + " " + event.getArgs().substring(args[0].length() + args[1].length() + 2)));
                    } catch (NumberFormatException ex) {
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.ban.notAnNumber", event));
                    }
                }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event)));
            }
        }, userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}


