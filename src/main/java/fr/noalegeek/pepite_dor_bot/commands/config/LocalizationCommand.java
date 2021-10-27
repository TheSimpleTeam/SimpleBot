package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

import java.util.Arrays;

public class LocalizationCommand extends Command {

    public LocalizationCommand() {
        this.name = "localization";
        this.cooldown = 5;
        this.help = "help.localization";
        this.example = "en";
        this.aliases = new String[]{"l","lo","local","loc","locali","localiz","localiza","localizat","localizati", "localizatio"};
        this.arguments = "<en/fr>";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 1 || Arrays.stream(Main.getLangs()).noneMatch(s -> s.equalsIgnoreCase(args[0]))) {
            MessageHelper.syntaxError(event, this, MessageHelper.translateMessage("syntax.localization", event));
            return;
        }
        if(args[0].equals(Main.getServerConfig().language().get(event.getGuild().getId()))){
            event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.localization.sameAsConfigured", event));
            return;
        }
        Main.getServerConfig().language().put(event.getGuild().getId(), args[0]);
        event.reply(MessageHelper.formattedMention(event.getAuthor()) + String.format(MessageHelper.translateMessage("success.localization.configured", event), ":flag_" + args[0].replace("en","us: / :flag_gb") + ':'));
    }
}
