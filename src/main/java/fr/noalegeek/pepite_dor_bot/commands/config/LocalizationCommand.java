package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

import java.util.Arrays;

public class LocalizationCommand extends Command {

    public LocalizationCommand() {
        this.name = "localization";
        this.aliases = new String[]{"clc"};
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length == 0) {
            MessageHelper.syntaxError(event.getAuthor(), this);
            return;
        }
        if(Arrays.stream(Main.getLangs()).noneMatch(s -> s.equalsIgnoreCase(args[0]))) {
            event.replyError("This lang does not exist !");
            return;
        }
        Main.getServerConfig().language.put(event.getGuild().getId(), args[0]);
        event.replySuccess(String.format(MessageHelper.sendTranslatedMessage("msg.languageconfig", event.getGuild().getId()), ":flag_" + args[0] + ':'));
    }
}
