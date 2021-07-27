package fr.noalegeek.pepite_dor_bot.slashcommand;

import com.jagrosh.jdautilities.command.SlashCommand;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class SupportCommand extends SlashCommand {
    public SupportCommand() {
        this.name = "support";
        this.guildOnly = true;
        this.cooldown = 5;
        this.aliases = new String[]{"sup","supp","suppo","suppor"};
        this.category = CommandCategories.INFO.category;
        this.help = "Envoie le discord de support.";
    }
    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply("Voici le discord officiel de " + event.getJDA().getSelfUser().getName() + "\n https://discord.gg/jw3kn4gNZW").setEphemeral(true).queue();
    }
}
