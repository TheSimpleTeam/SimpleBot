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
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;

@RequireConfig("botGithubToken")
public class GithubCommand extends Command {

    private final GitHub github;
    private final Map<String, Map<String, String>> colorMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public GithubCommand() throws IOException {
        this.name = "github";
        this.cooldown = 5;
        this.arguments = "arguments.github";
        this.category = CommandCategories.MISC.category;
        this.help = "help.github";
        this.example = "search PufferTeam SuperPack";
        this.aliases = new String[]{"git","gh"};
        this.github = new GitHubBuilder().withOAuthToken(SimpleBot.getInfos().botGithubToken()).build();
        try(InputStreamReader reader = new InputStreamReader(new URL("https://raw.githubusercontent.com/ozh/github-colors/master/colors.json").openStream(), StandardCharsets.UTF_8)) {
            colorMap.putAll(SimpleBot.gson.fromJson(reader, Map.class));
        } catch (IOException e) {
            SimpleBot.LOGGER.log(Level.WARNING, "Error while loading github colors", e);
        }
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3) {
            MessageHelper.syntaxError(event, this, "information.github");
            return;
        }
        if(isCommandDisabled()) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.github.disabled", null, null, null).build()).build());
            return;
        }
        switch (args[0]) {
            case "search" -> {
                if (args.length != 3) {
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
                    EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.github.search.success", getColor(repository.getLanguage()), null, repository.getOwner().getAvatarUrl())
                            .addField(MessageHelper.translateMessage(event, "success.github.search.repositoryName"), repository.getName() + " (" + repository.getUrl().toString() + ")", false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.author"), repository.getOwnerName(), false)
                            .addField(MessageHelper.translateMessage(event, "success.github.search.license"), repository.getLicense() == null ? MessageHelper.translateMessage(event, "success.github.noLicense") : repository.getLicense().getName(), false);
                    if(repository.getDescription() != null) embedBuilder.addField(MessageHelper.translateMessage(event, "success.github.search.description"), repository.getDescription(), false);
                    if(repository.getLanguage() != null) embedBuilder.addField(MessageHelper.translateMessage(event, "success.github.search.mainLanguage"), repository.getLanguage(), false);
                    try{
                        embedBuilder.addField(MessageHelper.translateMessage(event, "success.github.search.fileREADME"), MessageHelper.stringShortener(IOUtils.toString(repository.getReadme().read(), StandardCharsets.UTF_8), 1024), false);
                    } finally {
                        event.reply(new MessageBuilder(embedBuilder).build());
                    }
                } catch (IOException e) {
                    MessageHelper.sendError(e, event, this);
                }
            }
            case "list" ->
                    event.getMessage().reply(new MessageBuilder(MessageHelper.getEmbed(event, "warning.commands.takeTime", null, null, null).build()).build()).queue(message -> {
                        try {
                            EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.github.list", null, null, github.getUser(args[1]).getAvatarUrl(), name);
                            List<GHRepository> repositories = new ArrayList<>(github.getUser(args[1]).getRepositories().values());
                            repositories.stream().sorted(Comparator.comparing(GHRepository::getStargazersCount)
                                            .reversed()
                                            .thenComparing(GHRepository::getForksCount)
                                            .reversed()
                                            .thenComparing(GHRepository::getWatchersCount)
                                            .reversed()
                                            .thenComparing(GHRepository::getName))
                                    .forEach(repository -> {
                                        try {
                                            embedBuilder.addField(repository.getName() + " \u2B50 " + repository.getStargazersCount() + " <:github_fork:969261831359197214> " + repository.getForksCount() + " \uD83D\uDCC5 " + MessageHelper.formatShortDate(repository.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()), repository.getHtmlUrl().toString(), false);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                            event.getMessage().reply(new MessageBuilder(embedBuilder.build()).build()).queue(unused -> message.delete().queue());
                        } catch (IOException e) {
                            MessageHelper.sendError(e, event, this);
                        }
                    });
            default -> MessageHelper.syntaxError(event, this, "information.github");
        }
    }

    /**
     * @return {@code true} if the command is disabled, {@code false} otherwise
     */
    private boolean isCommandDisabled() {
        try {
            github.checkApiUrlValidity();
        } catch (IOException ignored) {
            return true;
        }
        return false;
    }

    /**
     * @param language the principal repository's language
     * @return the language's color
     */
    private Color getColor(String language) {
        return language == null || colorMap.isEmpty() ? Color.RED : Color.getColor(String.valueOf(getDecimal(colorMap.get(StringUtils.capitalize(language)).getOrDefault("color", "#FF0000"))));
    }

    /**
     * @param hex the hexadecimal color
     * @return the decimal color
     */
    private int getDecimal(String hex) {
        String digits = "0123456789ABCDEF";
        int val = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.toUpperCase().charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }
}
