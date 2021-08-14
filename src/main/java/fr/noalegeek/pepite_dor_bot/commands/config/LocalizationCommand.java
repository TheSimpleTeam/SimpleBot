package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;
import java.util.Random;

public class LocalizationCommand extends Command {

    public LocalizationCommand() {
        this.name = "localization";
        this.cooldown = 5;
        this.help = "help.localization";
        this.example = "en";
        this.aliases = new String[]{"l","lo","local","loc"};
        this.arguments = "arguments.localization";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length == 0) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        if(Arrays.stream(Main.getLangs()).noneMatch(s -> s.equalsIgnoreCase(args[0]))) {
            event.replyError("This lang does not exist !");
            return;
        }
        Main.getServerConfig().language.put(event.getGuild().getId(), args[0]);
        event.replySuccess(String.format(MessageHelper.translateMessage("success.localization.configured", event.getGuild().getId()), ":flag_" + args[0] + ':'));
    }
}
