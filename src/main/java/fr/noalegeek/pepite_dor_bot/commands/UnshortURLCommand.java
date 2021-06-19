package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class UnshortURLCommand extends Command {

    public UnshortURLCommand() {
        this.name = "unshorturl";
        this.aliases = new String[]{"usu"};
        this.arguments = "<URL>";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(), this));
            return;
        }
        String[] args = event.getArgs().split("\\s");
        try {
            event.replySuccess(String.format("`%s`", getURL(args[0])));
        } catch (IOException e) {
            MessageHelper.sendError(e, event);
            return;
        }
    }

    public String getURL(String _url) throws IOException {
        String url = unshortenUrl(_url);
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

    public static String unshortenUrl(final String shortUrl) throws IOException {
        //http://www.freeproxylists.net/fr/?s=ts
        HttpURLConnection connection = (HttpURLConnection) new URL(shortUrl).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(10000);
        int responseCode = connection.getResponseCode();
        String url = connection.getHeaderField("Location");
        if (responseCode / 100 == 3 && url != null) {
            String expandedUrl = unshortenUrl(url);
            if (Objects.equals(expandedUrl, url))
                return url;
            else {
                return expandedUrl;
            }
        }
        return shortUrl;
    }
}
