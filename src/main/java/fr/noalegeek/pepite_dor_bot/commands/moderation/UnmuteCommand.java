package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        this.name = "unmute";
        this.guildOnly = true;
        this.cooldown = 5;
        this.arguments = "<identifiant/mention du membre> <raison>";
        this.example = "363811352688721930";
        this.category = CommandCategories.STAFF.category;
        this.help = "help.unmute";
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 1 && args.length != 2) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        if (MuteCommand.isMutedRoleHere(event)) {
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.unmute.mutedRoleDontExist", event));
            return;
        }
        Main.getJda().retrieveUserById(args[0].replaceAll("\\D+", "")).queue(user -> event.getGuild().retrieveMember(user).queue(member -> {
            if (!member.getRoles().contains(event.getGuild().getRoleById(Main.getServerConfig().mutedRole().get(event.getGuild().getId()))) || !MuteCommand.isMutedRoleHere(event)) {
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("error.unmute.notMuted", event), user.getName()));
                return;
            }
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.unmute", event), user.getName(), MessageHelper.setReason(args[1], event)));
            event.getGuild().removeRoleFromMember(member, event.getGuild().getRoleById(Main.getServerConfig().mutedRole().get(event.getGuild().getId()))).queue();
        }), userNull -> event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.userNull", event)));
    }
}
