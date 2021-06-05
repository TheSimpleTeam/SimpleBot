package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Clock;
import java.time.OffsetDateTime;

public class SayCommand extends BotCommand {

    public SayCommand() {
        this.name = "say";
        this.cooldown = 5;
        this.arguments = "<texte>";
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.help = "Envoie le un message avec le texte défini après la commande sans supprimer le message d'origine.";
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageEmbed embed = new EmbedBuilder()
                .setTimestamp(OffsetDateTime.now(Clock.systemUTC()))
                .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl())
                .addField(event.getAuthor().getName() + " a dit :", event.getArgs(), false)
                .setColor(Color.MAGENTA)
                .build();
        event.reply(embed);
    }
}
