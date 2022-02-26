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
import java.util.ArrayList;
import java.util.Scanner;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        this.name = "unban";
        this.arguments = "arguments.unban";
        this.aliases = new String[]{"pardon"};
        this.category = CommandCategories.STAFF.category;
        this.help = "help.unban";
        this.guildOnly = true;
        this.example = "285829396009451522 wrong person";
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length < 1) {
            MessageHelper.syntaxError(event, this, "informations.unban");
            return;
        }
        if(args[0].replaceAll("\\D+", "").isEmpty()){
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.IDNull", null, null, null, (Object[]) null).build()).build());
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveBanList().queue(banList -> {
            if(banList.stream().anyMatch(banUser -> user.getId().equals(banUser.getUser().getId()))) {
                event.getGuild().unban(user).queue(unused -> {
                    Main.getServerConfig().tempBan().remove(new StringBuilder().append(user.getId()).append("-").append(event.getGuild().getId()).toString());
                    event.reply(new MessageBuilder(new EmbedBuilder()
                        .setTitle(new StringBuilder().append(UnicodeCharacters.whiteHeavyCheckMarkEmoji).append(" ").append(String.format(MessageHelper.translateMessage(event, "success.unban"), user.getName(), args.length == 1 ? MessageHelper.translateMessage(event, "text.commands.reasonNull") : new StringBuilder().append(MessageHelper.translateMessage(event, "text.commands.reason")).append(" ").append(event.getArgs().substring(args[0].length() + 1)).toString())).toString())
                        .setColor(Color.GREEN)
                        .setTimestamp(Instant.now())
                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl()).build()).build());
                });
                return;
            }
            event.reply(new MessageBuilder(new EmbedBuilder()
                    .setTitle(new StringBuilder().append(UnicodeCharacters.crossMarkEmoji).append(" ").append(String.format(MessageHelper.translateMessage(event, "error.unban"), user.getName())).toString())
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl()).build()).build());
        }), userNull -> event.reply(new MessageBuilder(new EmbedBuilder()
                .setTitle(new StringBuilder().append(UnicodeCharacters.crossMarkEmoji).append(" ").append(MessageHelper.translateMessage(event, "error.commands.userNull")).toString())
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl()).build()).build()));
    }
}
