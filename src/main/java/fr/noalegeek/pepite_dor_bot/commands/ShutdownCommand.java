package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.time.Instant;

public class ShutdownCommand extends Command {

    public ShutdownCommand() {
        this.name = "shutdown";
        this.help = "help.shutdown";
        this.aliases = new String[]{"sd","shutd","sdown"};
        this.guildOnly = false;
        this.ownerCommand = true;
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        EmbedBuilder successEmbed = new EmbedBuilder()
                .setTimestamp(Instant.now())
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .setTitle("\u2705 " + MessageHelper.translateMessage("success.shutdown", event))
                .setColor(Color.GREEN);
        event.reply(new MessageBuilder(successEmbed.build()).build());
        event.getJDA().shutdown();
    }
}
