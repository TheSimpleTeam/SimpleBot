package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.util.Locale;

public class ChannelMemberCommand extends Command {

    public ChannelMemberCommand() {
        this.name = "channelmember";
        this.cooldown = 5;
        this.help = "help.channelMember";
        this.example = "join 848965362971574282";
        this.aliases = new String[]{"channelm", "cmember", "cm"};
        this.arguments = "arguments.channelMember";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        if(!event.getMember().isOwner()){
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.commands.notOwner", event));
            return;
        }
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 2) {
            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.channelMember", event));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "join":
                String channelMemberId = Main.getServerConfig().channelMemberJoin.get(event.getGuild().getId());
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "reset":
                        if (channelMemberId == null) {
                            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.notConfigured", event));
                            return;
                        }
                        event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.join.reset", event), event.getGuild().getGuildChannelById(channelMemberId).getAsMention()));
                        Main.getServerConfig().channelMemberJoin.remove(event.getGuild().getId());
                        break;
                    case "this":
                        GuildChannel channelMember = (GuildChannel) event.getChannel();
                        if (channelMemberId == null || !channelMember.getId().equals(event.getChannel().getId())) {
                            Main.getServerConfig().channelMemberJoin.put(event.getGuild().getId(), channelMember.getId());
                            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), channelMember.getAsMention()));
                            return;
                        }
                        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.sameAsConfigured", event));
                        break;
                    default:
                        String channelId = args[1].replaceAll("\\D+", "");
                        if (channelId.isEmpty()) {
                            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.channelMember", event));
                            return;
                        }
                        channelMember = event.getGuild().getGuildChannelById(channelId);
                        if (channelMember == null) {
                            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.channelNull", event));
                            return;
                        }
                        if (channelMemberId == null || !channelMemberId.equals(channelId)) {
                            Main.getServerConfig().channelMemberJoin.put(event.getGuild().getId(), channelMember.getId());
                            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), channelMember.getAsMention()));
                            return;
                        }
                        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.sameAsConfigured", event));
                        break;
                }
                break;
            case "leave":
                channelMemberId = Main.getServerConfig().channelMemberRemove.get(event.getGuild().getId());
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "reset":
                        if (channelMemberId == null) {
                            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.notConfigured", event));
                            return;
                        }
                        Main.getServerConfig().channelMemberRemove.remove(event.getGuild().getId());
                        event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.reset", event), event.getGuild().getGuildChannelById(channelMemberId).getAsMention()));
                        break;
                    case "this":
                        GuildChannel channelMember = (GuildChannel) event.getChannel();
                        if (channelMemberId == null || !channelMemberId.equals(event.getChannel().getId())) {
                            Main.getServerConfig().channelMemberRemove.put(event.getGuild().getId(), channelMember.getId());
                            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), channelMember.getAsMention()));
                            return;
                        }
                        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.sameAsConfigured", event));
                        break;
                    default:
                        String channelId = args[1].replaceAll("\\D+", "");
                        if (channelId.isEmpty()) {
                            event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.channelMember", event));
                            return;
                        }
                        channelMember = event.getGuild().getGuildChannelById(channelId);
                        if (channelMember == null) {
                            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.channelNull", event));
                            return;
                        }
                        if (channelMemberId == null || !channelMemberId.equals(channelId)) {
                            Main.getServerConfig().channelMemberRemove.put(event.getGuild().getId(), channelMember.getId());
                            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), channelMember.getAsMention()));
                            return;
                        }
                        event.replyError(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.sameAsConfigured", event));
                        break;
                }
                break;
            default:
                event.replyError(MessageHelper.syntaxError(event, this) + MessageHelper.translateMessage("syntax.channelMember", event));
        }
    }
}
