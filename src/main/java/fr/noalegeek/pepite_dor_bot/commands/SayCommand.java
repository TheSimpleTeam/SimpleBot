package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Clock;
import java.time.OffsetDateTime;

public class SayCommand extends Command {

    public SayCommand() {
        this.name = "say";
        this.cooldown = 5;
        this.arguments = "<texte>";
        this.help = "Envoie le un message avec le texte défini après la commande sans supprimer le message d'origine.";
        this.example = "Hey, je suis un robot !";
        this.category = CommandCategories.STAFF.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMember().hasPermission(Permission.MESSAGE_MANAGE)){
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+event.getArgs());
            return;
        }
        event.replySuccess(event.getArgs());
        event.getMessage().delete().queue();
    }
}
