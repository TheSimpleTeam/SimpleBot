package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RequestHelper;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;

public class TranslateCommand extends Command {

    public TranslateCommand() {
        this.name = "translate";
        this.aliases = new String[]{"tr","tra","tran","trans","transl","transla","translat"};
        this.example = "Hello everyone. --lang en fr";
        this.arguments = "<text> <--lang> <from> <to>";
        this.help = "Traduit la phrase spécifiée. Il faut spécifié la langue de la phrase et la langue dans laquelle vous voulez qu'elle soit traduit.";
        this.category = CommandCategories.FUN.category;
    }
    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMessage().getContentRaw().contains("--lang")) {
            event.reply(MessageHelper.syntaxError(event, this, null));
            return;
        }
        String[] args = event.getArgs().split(" --lang ");
        String text = args[0];
        String[] langs = args[1].split("\\s+");
        String firstLang = langs[0];
        String secondLang = langs[1];
        try {
            Response response = RequestHelper.sendRequest(String.format("https://lingva.ml/api/v1/%s/%s/%s", firstLang, secondLang, URLEncoder.encode(text, "UTF-8")));
            event.reply(Main.gson.fromJson(response.body().string(), JsonObject.class).get("translation").getAsString());
        } catch (IOException ex) {
            Main.LOGGER.severe(ex.getMessage());
            event.reply("Une erreur est survenue. Veuillez contacter le développeur et envoyez lui ce message :\n" +
                    ex.getMessage());
        }
    }
}
