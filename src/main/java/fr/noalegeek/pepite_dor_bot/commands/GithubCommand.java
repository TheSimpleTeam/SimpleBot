package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;

public class GithubCommand extends Command {

    private final String githubToken;
    private final GitHub github;

    public GithubCommand() throws IOException {
        this.name = "github";
        this.cooldown = 5;
        this.arguments = "<search/list> <user> [repo name]";
        this.githubToken = Main.getInfos().githubToken;
        this.github = new GitHubBuilder().withOAuthToken(githubToken).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length <= 1 || args.length >= 4) {
            MessageHelper.syntaxError(this, event);
            return;
        }

        if(isCommandDisabled()) {
            event.replyError("Cette commande est désactivée.");
            return;
        }
        String user = args[1];
        switch (args[0]) {
            case "search":
                if(args.length != 3) {
                    MessageHelper.syntaxError(this, event);
                    return;
                }
                String repoS = args[2];
                GHRepository repo;
                try {
                    repo = github.getRepository(user + "/" + repoS);
                } catch (IOException ignored) {
                    event.replyError("Ce répertoire Github n'existe pas. \n" + MessageHelper.syntaxError(event.getAuthor(), this));
                    return;
                }
                try {
                    MessageEmbed builder = new EmbedBuilder()
                            .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl())
                            .setTimestamp(Instant.now())
                            .setTitle(repo.getName(), repo.getUrl().toString())
                            .setThumbnail(repo.getOwner().getAvatarUrl())
                            .setColor(getColor(repo.getLanguage()))
                            .addField("Auteur :", repo.getOwnerName(), false)
                            .addField("Description:", repo.getDescription(), false)
                            .addField("Readme:", IOUtils.toString(repo.getReadme().read(), StandardCharsets.UTF_8), false)
                            .addField("License:", repo.getLicense().getName(), false)
                            .addField("Language principal:", repo.getLanguage(), false)
                            .build();
                    event.reply(builder);
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event);
                }
                break;
            case "list":
                try {
                    GHUser ghuser = github.getUser(user);
                    EmbedBuilder builder = new EmbedBuilder()
                            .setAuthor(event.getAuthor().getName(), null, event.getAuthor().getEffectiveAvatarUrl())
                            .setTimestamp(Instant.now())
                            .setTitle("Liste des projets de " + name)
                            .setThumbnail(ghuser.getAvatarUrl());
                    Map<String, GHRepository> repositories = ghuser.getRepositories();
                    for (String ghname : repositories.keySet()) {
                        builder.addField(ghname, repositories.get(ghname).getHtmlUrl().toString(), false);
                        Main.LOGGER.info("Added " + ghname);
                    }
                    event.reply(builder.build());
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event);
                    return;
                }
                break;
            default:
                MessageHelper.syntaxError(this, event);
                break;
        }
    }

    private boolean isCommandDisabled() {
        try {
            github.checkApiUrlValidity();
        }catch (IOException ignored) {
            return true;
        }
        return false;
    }

    private int getColor(String language) {
        try {
            Map<String, Map<String, String>> lang = Main.gson.fromJson(new InputStreamReader(new URL("https://raw.githubusercontent.com/ozh/github-colors/master/colors.json")
                            .openStream()), Map.class);
            return getDecimal(lang.get(StringUtils.capitalize(language)).getOrDefault("color", "#FF0000"));
        } catch (IOException exception) {
            exception.printStackTrace();
            return Color.RED.getRGB();
        }
    }

    private int getDecimal(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
}
