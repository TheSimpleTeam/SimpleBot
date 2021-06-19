package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.shekhargulati.urlcleaner.UrlCleaner;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class UnshortURLCommand extends Command {

    public UnshortURLCommand() {
        this.name = "unshorturl";
        this.aliases = new String[]{"usu"};
        this.arguments = "<URL>";
        this.help = "Donne la redirection d'un lien.";
        this.category = CommandCategories.MISC.category;
        this.example = "Liens AdFly";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        try {
            event.replySuccess(String.format("`%s`", getURL(args[0])));
        } catch (IOException e) {
            MessageHelper.sendError(e, event);
            return;
        }
    }
    public String getURL(String _url) throws IOException {
        String url = UrlCleaner.unshortenUrl(_url);
        String[] urls = url.replace("https://", "").replace("http://", "").split("/");
        if(urls[0].equalsIgnoreCase("preview.tinyurl.com")) {
            Document doc = Jsoup.connect(_url).get();
            Elements redirectURL = doc.getElementById("contentcontainer").getElementsByAttribute("blockquote");
            Main.LOGGER.info(String.valueOf(redirectURL.size()));
            String docText = doc.body().text();
            return docText.substring(docText.indexOf("to: "), docText.indexOf(" Proceed")).replace("to: ", "").replace(" Proceed", "");
        }
        return url;
    }
}
