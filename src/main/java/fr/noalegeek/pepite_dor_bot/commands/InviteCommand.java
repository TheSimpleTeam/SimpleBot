package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Locale;

public class InviteCommand extends Command {

    public InviteCommand() {
        this.name = "invite";
        this.aliases = new String[]{"inv", "i"};
        this.guildOnly = true;
        this.cooldown = 10;
        this.arguments = "<create/bot>";
        this.help = "Crée une invitation du serveur.";
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(event.getArgs().length() == 0) {
            event.reply(MessageHelper.syntaxError(event, this, "syntax.invite"));
            return;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "create" -> {
                EmbedBuilder successCreateEmbed = new EmbedBuilder();

                event.reply(MessageHelper.formattedMention(event.getAuthor()) + "Voici ton lien d'invitation du serveur " + event.getGuild().getName() + ", n'hésite pas à faire venir plein de personnes !\n" + event.getTextChannel().createInvite().complete().getUrl());
            }
            case "bot" -> {
                event.reply("Voici le lien d'invitation pour inviter le bot sur ton serveur !\n" + String.format("https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=8589934591", event.getJDA().getSelfUser().getId()));
            }
            default -> event.reply(MessageHelper.syntaxError(event, this, "syntax.invite"));
        }
    }
}
