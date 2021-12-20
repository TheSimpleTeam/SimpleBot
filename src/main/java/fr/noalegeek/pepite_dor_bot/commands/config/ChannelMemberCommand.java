package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.util.Locale;

public class ChannelMemberCommand extends Command {

    public ChannelMemberCommand() {
        this.name = "channelmember";
        this.cooldown = 5;
        this.help = "help.channelMember";
        this.example = "join 848965362971574282\nleave reset";
        this.aliases = new String[]{"cm", "channelm", "cmember"};
        this.arguments = "arguments.channelMember";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if (args.length != 2) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.channelMember", event));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "join":
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "reset" -> {
                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null) {
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.notConfigured", event));
                            return;
                        }
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.join.reset", event), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getAsMention()));
                        Main.getServerConfig().channelMemberJoin().remove(event.getGuild().getId());
                    }
                    case "this" -> {
                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !event.getChannel().getId().equals(event.getChannel().getId())) {
                            Main.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), event.getChannel().getId());
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.join.configured", event), ((GuildChannel) event.getChannel()).getAsMention()));
                            return;
                        }
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.sameAsConfigured", event));
                    }
                    default -> {
                        if (args[1].replaceAll("\\D+", "").isEmpty()) {
                            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.channelMember", event));
                            return;
                        }
                        if (event.getGuild().getGuildChannelById(args[1].replaceAll("\\D+", "")) == null) {
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.channelNull", event));
                            return;
                        }
                        if (Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId()).equals(args[1].replaceAll("\\D+", ""))) {
                            Main.getServerConfig().channelMemberJoin().put(event.getGuild().getId(), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getId());
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.join.configured.configuredBefore", event), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberJoin().get(event.getGuild().getId())).getAsMention()));
                            return;
                        }
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.join.sameAsConfigured", event));
                    }
                }
                break;
            case "leave":
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "reset" -> {
                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null) {
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.notConfigured", event));
                            return;
                        }
                        Main.getServerConfig().channelMemberLeave().remove(event.getGuild().getId());
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.reset", event), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())).getAsMention()));
                    }
                    case "this" -> {
                        GuildChannel channelMember = (GuildChannel) event.getChannel();
                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(event.getChannel().getId())) {
                            Main.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), channelMember.getId());
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), channelMember.getAsMention()));
                            return;
                        }
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.sameAsConfigured", event));
                    }
                    default -> {
                        if (args[1].replaceAll("\\D+", "").isEmpty()) {
                            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.channelMember", event));
                            return;
                        }
                        if (event.getGuild().getGuildChannelById(args[1].replaceAll("\\D+", "")) == null) {
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.channelNull", event));
                            return;
                        }
                        if (Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()) == null || !Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId()).equals(args[1].replaceAll("\\D+", ""))) {
                            Main.getServerConfig().channelMemberLeave().put(event.getGuild().getId(), event.getGuild().getGuildChannelById(args[1].replaceAll("\\D+", "")).getId());
                            event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.channelMember.leave.configured", event), event.getGuild().getGuildChannelById(Main.getServerConfig().channelMemberLeave().get(event.getGuild().getId())).getAsMention()));
                            return;
                        }
                        event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.channelMember.leave.sameAsConfigured", event));
                    }
                }
                break;
            default:
                MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.channelMember", event));
        }
    }
}
