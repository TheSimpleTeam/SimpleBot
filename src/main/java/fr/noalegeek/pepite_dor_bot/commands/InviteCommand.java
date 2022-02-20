package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.time.Instant;
import java.util.Locale;

public class InviteCommand extends Command {

    public InviteCommand() {
        this.name = "invite";
        this.aliases = new String[]{"inv", "i"};
        this.guildOnly = true;
        this.cooldown = 10;
        this.example = "create";
        this.arguments = "<create/bot>";
        this.help = "help.invite";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(event.getArgs().length() != 1) {
            MessageHelper.syntaxError(event, this, "syntax.invite");
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create" -> event.reply(new MessageBuilder(MessageHelper.getEmbed(String.format(MessageHelper.translateMessage("success.invite.create.success", event), event.getGuild().getName()), event)
                    .addField(MessageHelper.translateMessage("success.invite.create.invitationLink", event), event.getTextChannel().createInvite().complete().getUrl(), false)
                    .build()).build());
            case "bot" -> event.reply(new MessageBuilder(MessageHelper.getEmbed(String.format(MessageHelper.translateMessage("success.invite.bot.success", event), event.getSelfMember().getEffectiveName()), event)
                    .addField(MessageHelper.translateMessage("success.invite.bot.invitationLink", event), String.format("https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=8589934591", event.getJDA().getSelfUser().getId()), false)
                    .build()).build());
            default -> MessageHelper.syntaxError(event, this, "syntax.invite");
        }
    }
}
