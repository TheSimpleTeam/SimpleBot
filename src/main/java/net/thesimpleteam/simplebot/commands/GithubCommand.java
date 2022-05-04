package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.typesafe.config.ConfigException;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    private Optional<String> getReadme(GHRepository repository) {
        try {
            return Optional.of(IOUtils.toString(repository.getReadme().read(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return Optional.empty();
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
            return language == null ? Color.RED : Color.getColor(String.valueOf(getDecimal(lang.get(StringUtils.capitalize(language)).getOrDefault("color", "#FF0000"))));
        } catch (IOException e) {
            e.printStackTrace();
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
