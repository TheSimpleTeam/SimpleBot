package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.MessageBuilder;
import org.jsoup.Jsoup;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UnshortURLCommand extends Command {

    public UnshortURLCommand() {
        this.name = "unshorturl";
        this.aliases = new String[]{"usu"};
        this.arguments = "arguments.unshorturl";
        this.help = "help.unshorturl";
        this.category = CommandCategories.MISC.category;
        this.example = "shorturl.at/aszN3";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        if(true) {
            //TODO: remove this command
            event.reply("This command has been removed.");
            return;
        }
        try {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.unshortURL.success", Color.GREEN, null, null)
                    .addField(MessageHelper.translateMessage(event, "success.unshortURL.link"), !event.getArgs().split("\\s")[0].startsWith("https://") && !event.getArgs().split("\\s")[0].startsWith("http://") ? "http://" + event.getArgs().split("\\s")[0] : event.getArgs().split("\\s")[0], false)
                    .addField(MessageHelper.translateMessage(event, "success.unshortURL.redirection"), new StringBuilder().append('`').append(unshortUrl(!event.getArgs().split("\\s")[0].startsWith("https://") && !event.getArgs().split("\\s")[0].startsWith("http://") ? "http://" + event.getArgs().split("\\s")[0] : event.getArgs().split("\\s")[0]).replaceFirst("http(s?)://", "").split("/")[0].equalsIgnoreCase("preview.tinyurl.com") ? Jsoup.connect(unshortUrl(!event.getArgs().split("\\s")[0].startsWith("https://") && !event.getArgs().split("\\s")[0].startsWith("http://") ? "http://" + event.getArgs().split("\\s")[0] : event.getArgs().split("\\s")[0])).get().body().text().substring(Jsoup.connect(unshortUrl(!event.getArgs().split("\\s")[0].startsWith("https://") && !event.getArgs().split("\\s")[0].startsWith("http://") ? "http://" + event.getArgs().split("\\s")[0] : event.getArgs().split("\\s")[0])).get().body().text().indexOf("to: "), Jsoup.connect(unshortUrl(!event.getArgs().split("\\s")[0].startsWith("https://") && !event.getArgs().split("\\s")[0].startsWith("http://") ? "http://" + event.getArgs().split("\\s")[0] : event.getArgs().split("\\s")[0])).get().body().text().indexOf(" Proceed")).replace("to: ", "").replace(" Proceed", "") : unshortUrl(!event.getArgs().split("\\s")[0].startsWith("https://") && !event.getArgs().split("\\s")[0].startsWith("http://") ? "http://" + event.getArgs().split("\\s")[0] : event.getArgs().split("\\s")[0])).append('`').toString(), false)
                    .build()).build());
        } catch (IOException e) {
            MessageHelper.sendError(e, event, this);
        }
    }

    /**
     * @param url the url
     * @return the unshorten url
     * @throws IOException
     */
    public static String unshortUrl(final String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(10000);
        return connection.getResponseCode() / 100 == 3 && connection.getHeaderField("Location") != null ? unshortUrl(connection.getHeaderField("Location")).equals(connection.getHeaderField("Location")) ? connection.getHeaderField("Location") : unshortUrl(connection.getHeaderField("Location")) : url;
    }
}
