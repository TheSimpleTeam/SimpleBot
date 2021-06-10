package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.helpers.RequestHelper;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;

public class TranslateCommand extends Command {

    public TranslateCommand() {
        this.name = "translate";
        this.aliases = new String[]{"t"};
        this.example = "Hello everyone. --lang en fr";
        this.arguments = "<text> <--lang <from> <to>>";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMessage().getContentRaw().contains("--lang")) {
            MessageHelper.syntaxError(event.getAuthor(), this);
            return;
        }
        String[] args = event.getArgs().split(" --lang ");
        String text = args[0];
        Main.LOGGER.info(text);
        String[] langs = args[1].split("\\s");
        String firstLang = langs[0];
        String secondLang = langs[1];
        try {
            Response response = RequestHelper.sendRequest(String.format("https://lingva.ml/api/v1/%s/%s/%s", firstLang, secondLang, URLEncoder.encode(text, "UTF-8")));
            event.replySuccess(Main.gson.fromJson(response.body().string(), JsonObject.class).get("translation").getAsString());
        } catch (IOException ex) {
            Main.LOGGER.severe(ex.getMessage());
            event.replyError("Une erreur est survenue. Veuillez contacter le développeur \n" +
                    ex.getMessage());
        }
    }
}
