package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
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

    private final GitHub github;

    public GithubCommand() throws IOException {
        this.name = "github";
        this.cooldown = 5;
        this.arguments = "<recherche/liste> <utilisateur GitHub> [nom du répertoire GitHub]";
        this.category = CommandCategories.MISC.category;
        this.example = "Liste tous les répertoires GitHub d'un utilisateur GitHub ou donne des informations sur un répertoire GitHub d'un utilisateur GitHub.\nL'utilisateur GitHub peut être remplacé par une organisation GitHub.";
        this.help = "recherche PufferTeam SuperPack";
        this.aliases = new String[]{"ghub","gith","gh"};
        this.github = new GitHubBuilder().withOAuthToken(Main.getInfos().githubToken).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event.getAuthor(), this);
            return;
        }
        if(isCommandDisabled()) {
            event.replyError("Cette commande est désactivée.");
            return;
        }
        String user = args[1];
        switch (args[0]) {
            case "recherche":
                if(args.length != 3) {
                    MessageHelper.syntaxError(event.getAuthor(), this);
                    return;
                }
                String strRepo = args[2];
                GHRepository repo;
                try {
                    repo = github.getRepository(user + "/" + strRepo);
                } catch (IOException ignored) {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Ce répertoire Github n'existe pas.");
                    return;
                }
                try {
                    MessageEmbed embedSearch = new EmbedBuilder()
                            .setTimestamp(Instant.now())
                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                            .setTitle(repo.getName(), repo.getUrl().toString())
                            .setThumbnail(repo.getOwner().getAvatarUrl())
                            .setColor(getColor(repo.getLanguage()))
                            .addField("Auteur :", repo.getOwnerName(), false)
                            .addField("Description :", repo.getDescription(), false)
                            .addField("README :", readmeString(IOUtils.toString(repo.getReadme().read(), StandardCharsets.UTF_8)), false)
                            .addField("License :", getLicense(repo), false)
                            .addField("Language principal :", repo.getLanguage(), false)
                            .build();
                    event.reply(embedSearch);
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event);
                }
                break;
            case "liste":
                try {
                    GHUser ghuser = github.getUser(user);
                    EmbedBuilder embedList = new EmbedBuilder()
                            .setTimestamp(Instant.now())
                            .setTitle("Liste des projets de " + name + " :")
                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                            .setThumbnail(ghuser.getAvatarUrl());
                    Map<String, GHRepository> repositories = ghuser.getRepositories();
                    for (String ghname : repositories.keySet()) {
                        embedList.addField(ghname, repositories.get(ghname).getHtmlUrl().toString(), false);
                        Main.LOGGER.info("Added " + ghname);
                    }
                    event.reply(embedList.build());
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event);
                    return;
                }
                break;
            default:
                MessageHelper.syntaxError(event.getAuthor(), this);
                break;
        }
    }

    private String getLicense(GHRepository repo) throws IOException {
        return repo.getLicense() == null ? "Aucune license" : repo.getLicense().getName();
    }

    private String readmeString(String readme) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < readme.toCharArray().length; i++) {
            if(i == 1020) {
                builder.append("...");
                break;
            }
            builder.append(readme.toCharArray()[i]);
        }
        return builder.toString();
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
