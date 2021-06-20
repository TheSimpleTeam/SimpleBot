package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;

public class MuteCommand extends Command {
    public MuteCommand() {
        this.category = CommandCategories.STAFF.category;
        this.aliases = new String[]{"m","mu","mut"};
        this.name = "mute";
        this.arguments = "<mention de l'utilisateur> [raison]";
        this.help = "Mute définitivement un utilisateur ";
        this.cooldown = 5;
        this.example = "@NoaLeGeek spam";
    }

    @Override
    protected void execute(CommandEvent event) {

    }
}
