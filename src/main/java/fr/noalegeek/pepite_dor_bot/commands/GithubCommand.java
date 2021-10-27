package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.annotations.RequireConfig;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
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
import java.time.Instant;
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
        this.github = new GitHubBuilder().withOAuthToken(Main.getInfos().botGithubToken()).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3) {
            event.reply(new MessageBuilder(MessageHelper.syntaxError(event, this)).build());
            return;
        }
        if(isCommandDisabled()) {
            EmbedBuilder errorDisabledCommandEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("\u274C " + MessageHelper.translateMessage("error.github.disabled", event))
                    .setTimestamp(Instant.now())
                    .setFooter(MessageHelper.getTag(event.getAuthor()) + event.getAuthor().getAvatarUrl());
            event.reply(new MessageBuilder(errorDisabledCommandEmbed.build()).build());
            return;
        }
        switch (args[0]) {
            case "search":
                if(args.length != 3) {
                    MessageHelper.syntaxError(event, this, null);
                    return;
                }
                GHRepository repository;
                try {
                    repository = github.getRepository(args[1] + "/" + args[2]);
                } catch (IOException ignored) {
                    event.reply(MessageHelper.formattedMention(event.getAuthor()) + MessageHelper.translateMessage("error.github.search.repositoryDontExist", event));
                    return;
                }
                try {
                    EmbedBuilder successSearchEmbed = new EmbedBuilder()
                            .setTimestamp(Instant.now())
                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                            .setTitle("\u2705 " + MessageHelper.translateMessage("success.github.search.success", event))
                            .setThumbnail(repository.getOwner().getAvatarUrl())
                            .setColor(getColor(repository.getLanguage()))
                            .addField(MessageHelper.translateMessage("success.github.search.repositoryName", event), repository.getName() + " (" + repository.getUrl().toString() + ")", false)
                            .addField(MessageHelper.translateMessage("success.github.search.author", event), repository.getOwnerName(), false)
                            .addField(MessageHelper.translateMessage("success.github.search.description", event), repository.getDescription(), false)
                            .addField(MessageHelper.translateMessage("success.github.search.fileREADME", event), readmeString(IOUtils.toString(repository.getReadme().read(), StandardCharsets.UTF_8)), false)
                            .addField(MessageHelper.translateMessage("success.github.search.license", event), getLicense(repository, event), false)
                            .addField(MessageHelper.translateMessage("success.github.search.mainLanguage", event), repository.getLanguage(), false);
                    event.reply(new MessageBuilder(successSearchEmbed.build()).build());
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event, this);
                }
                break;
            case "list":
                try {
                    EmbedBuilder successListEmbed = new EmbedBuilder()
                            .setTimestamp(Instant.now())
                            .setTitle("\u2705 " + String.format(MessageHelper.translateMessage("success.github.list", event), name))
                            .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                            .setThumbnail(github.getUser(args[1]).getAvatarUrl());
                    for (String ghname : github.getUser(args[1]).getRepositories().keySet()) {
                        successListEmbed.addField(ghname, github.getUser(args[1]).getRepositories().get(ghname).getHtmlUrl().toString(), false);
                    }
                    event.reply(new MessageBuilder(successListEmbed.build()).build());
                } catch (IOException ex) {
                    MessageHelper.sendError(ex, event, this);
                    return;
                }
                break;
            default:
                MessageHelper.syntaxError(event, this, null);
                break;
        }
    }

    private String getLicense(GHRepository repo, CommandEvent event) throws IOException {
        return repo.getLicense() == null ? MessageHelper.translateMessage("success.github.noLicense", event) : repo.getLicense().getName();
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
        } catch (IOException ignored) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private int getColor(String language) {
        try {
            Map<String, Map<String, String>> lang = Main.gson.fromJson(new InputStreamReader(new URL("https://raw.githubusercontent.com/ozh/github-colors/master/colors.json").openStream()), Map.class);
            return getDecimal(lang.get(StringUtils.capitalize(language)).getOrDefault("color", "#FF0000"));
        } catch (IOException exception) {
            exception.printStackTrace();
            return Color.RED.getRGB();
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
