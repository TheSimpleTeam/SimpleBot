package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

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
        if (args.length != 1 && args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("informations.ban", event));
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
                        int banTime = Integer.parseInt(args[1]);
                        if (banTime > 7) {
                            banTime = 7;
                            event.replyWarning(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("warning.ban", event));
                        }
                        event.getGuild().ban(user, banTime).queue();
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.ban", event), user.getName(), MessageHelper.setReason(args[2], event)));
                    } catch (NumberFormatException ex) {
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.ban.notAnNumber", event));
                    }
                }, memberNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.memberNull", event)));
            }
        }, userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}


