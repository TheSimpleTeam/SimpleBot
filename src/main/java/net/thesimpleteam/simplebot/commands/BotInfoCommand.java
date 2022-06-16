package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.thesimpleteam.simplebot.utils.UnicodeCharacters;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;
import java.util.Locale;

public class BotInfoCommand extends Command {

    public BotInfoCommand() {
        this.name = "botinfo";
        this.aliases = new String[]{"bi","boti","binfo"};
        this.guildOnly = true;
        this.cooldown = 5;
        this.help = "help.botInfo";
        this.category = CommandCategories.INFO.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.botInfo.success", Color.BLUE, null, event.getSelfUser().getAvatarUrl())
                .setTitle(new StringBuilder().append(UnicodeCharacters.INFORMATION_SOURCE_EMOJI).append(" ").append(String.format(MessageHelper.translateMessage(event, "success.botInfo.success"), event.getSelfUser().getName())).toString())
                .addField(MessageHelper.translateMessage(event, "success.botInfo.id"), event.getSelfUser().getId(), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.tag"), "#" + event.getSelfUser().getDiscriminator(), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.creationDate"), MessageHelper.formatShortDate(event.getSelfMember().getTimeCreated().toLocalDateTime()), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.joinDate"), MessageHelper.formatShortDate(event.getSelfMember().getTimeJoined().toLocalDateTime()), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.activity"), event.getSelfUser().getJDA().getPresence().getActivity() == null ? MessageHelper.translateMessage(event, "text.commands.nothing") : event.getSelfUser().getJDA().getPresence().getActivity().getName(), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.status"), StringUtils.capitalize(String.valueOf(event.getSelfUser().getJDA().getPresence().getStatus()).toLowerCase(Locale.ROOT).replace("_", "")), false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.github"), "https://github.com/TheSimpleTeam/SimpleBot", false)
                .addField(MessageHelper.translateMessage(event, "success.botInfo.invitationLink"), String.format("https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=8589934591", event.getJDA().getSelfUser().getId()), false);
        if(event.getSelfMember().getNickname() != null) embedBuilder.addField(MessageHelper.translateMessage(event, "success.botInfo.name"), event.getSelfUser().getName(), false);
        if(SimpleBot.getServerConfig().prefix().get(event.getGuild().getId()) != null) embedBuilder.addField(MessageHelper.translateMessage(event, "success.botInfo.serverPrefix"), SimpleBot.getServerConfig().prefix().get(event.getGuild().getId()), false).addField(MessageHelper.translateMessage(event, "success.botInfo.prefix"), SimpleBot.getInfos().prefix(), false);
        event.reply(new MessageBuilder(embedBuilder.build()).build());
    }
}
