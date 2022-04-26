package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.commands.annotations.RequireConfig;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RequireConfig("botGithubToken")
public class GithubCommand extends Command {

    private final GitHub github;

    public GithubCommand() throws IOException {
        this.name = "github";
        this.cooldown = 5;
        this.arguments = "arguments.github";
        this.category = CommandCategories.MISC.category;
        this.help = "help.github";
        this.example = "search PufferTeam SuperPack";
        this.aliases = new String[]{"ghub","gith","gh"};
        this.github = new GitHubBuilder().withOAuthToken(SimpleBot.getInfos().botGithubToken()).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, "information.github");
            return;
        }
        if(isCommandDisabled()) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.github.disabled", null, null, null, (Object[]) null).build()).build());
            return;
        }
        switch (args[0]) {
            case "search":
                if(args.length != 3) {
                    MessageHelper.syntaxError(event, this, "information.github");
                    return;
                }
                GHRepository repository;
                try {
                    repository = github.getRepository(args[1] + "/" + args[2]);
                } catch (IOException ignored) {
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.github.search.repositoryDontExist", null, null, null).build()).build());
                    return;
                }
                try {
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.github.search.success", getColor(repository.getLanguage()), null, repository.getOwner().getAvatarUrl(), (Object[]) null)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.repositoryName"), repository.getName() + " (" + repository.getUrl().toString() + ")", false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.author"), repository.getOwnerName(), false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.description"), repository.getDescription(), false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.fileREADME"), MessageHelper.getDescription(IOUtils.toString(repository.getReadme().read(), StandardCharsets.UTF_8)), false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.license"), repository.getLicense() == null ? MessageHelper.translateMessage(event, "success.github.noLicense") : repository.getLicense().getName(), false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.mainLanguage"), repository.getLanguage(), false)
                            .build()).build());
                } catch (IOException exception) {
                    MessageHelper.sendError(exception, event, this);
                }
                break;
            case "list":
                try {
                    EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.github.list", null, null, github.getUser(args[1]).getAvatarUrl(), name);
                    List<GHRepository> repositories = new ArrayList<>(github.getUser(args[1]).getRepositories().values());
                    repositories.sort(
                            Comparator.comparing(GHRepository::getStargazersCount)
                                    .reversed()
                                    .thenComparing(GHRepository::getForksCount)
                                    .reversed()
                                    .thenComparing(GHRepository::getWatchersCount)
                                    .reversed()
                                    .thenComparing(GHRepository::getName));
                    repositories.forEach(repo -> embedBuilder.addField(repo.getName(), repo.getHtmlUrl().toString(), false));
                    event.reply(new MessageBuilder(embedBuilder.build()).build());
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event, this);
                }
                break;
            default:
                MessageHelper.syntaxError(event, this, "information.github");
                break;
        }
    }

    private boolean isCommandDisabled() {
        try {
            github.checkApiUrlValidity();
        } catch (IOException ignored) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Color getColor(String language) {
        try {
            Map<String, Map<String, String>> lang = SimpleBot.gson.fromJson(new InputStreamReader(new URL("https://raw.githubusercontent.com/ozh/github-colors/master/colors.json").openStream()), Map.class);
            return Color.getColor(String.valueOf(getDecimal(lang.get(StringUtils.capitalize(language)).getOrDefault("color", "#FF0000"))));
        } catch (IOException exception) {
            exception.printStackTrace();
            return Color.RED;
        }
    }

    private int getDecimal(String hex) {
        String digits = "0123456789ABCDEF";
        int val = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.toUpperCase().charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }
}
