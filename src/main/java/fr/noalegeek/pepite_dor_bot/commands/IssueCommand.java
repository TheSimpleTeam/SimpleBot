/*
 * MIT License
 *
 * Copyright (c) 2021 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.GithubInfo;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.annotations.RequireConfig;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.apache.commons.cli.*;
import org.kohsuke.github.GHIssueBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;

@RequireConfig("botGithubToken")
public class IssueCommand extends Command {

    private final GitHub github;

    public IssueCommand() throws IOException {
        this.name = "issue";
        this.cooldown = 600;
        this.help = "help.issue";
        this.arguments = "arguments.issue";
        this.example = "example.issue";
        this.github = new GitHubBuilder().withOAuthToken(Main.getInfos().botGithubToken()).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s");
        if(args.length == 0) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        try {
            this.github.checkApiUrlValidity();
        } catch (IOException ex) {
            EmbedBuilder errorGithubTokenNotValidEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.issue.githubTokenNotValid", event));
            event.reply(new MessageBuilder(errorGithubTokenNotValidEmbed.build()).build());
            return;
        }
        try {
            addOptions(new Options());
            if (new DefaultParser().parse(new Options(), args).getOptions().length != 0 && !new DefaultParser().parse(new Options(), args).hasOption("body")) {
                EmbedBuilder errorBodyParameterNotHereEmbed = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setTitle(UnicodeCharacters.crossMarkEmoji + " " + MessageHelper.translateMessage("error.issue.bodyParameterNotHere", event));
                event.reply(new MessageBuilder(errorBodyParameterNotHereEmbed.build()).build());
                return;
            }
           this.github.getRepositoryById(GithubInfo.REPOSITORY_ID.id).createIssue(String.join(" ", getOrDefault(new DefaultParser().parse(new Options(), args), "title", MessageHelper.translateMessage("text.issue.issue", event)))).body(String.format(MessageHelper.translateMessage("success.issue.success", event) + "\n\n" + MessageHelper.translateMessage("text.issue.issue", event) + MessageHelper.translateMessage("text.issue.twoSuperimposedPoints", event) + "\n\n%s", MessageHelper.getTag(event.getAuthor()), event.getAuthor().getId(), event.getGuild().getName(), event.getGuild().getId(), String.join(" ", getOrDefault(new DefaultParser().parse(new Options(), args), "body", event.getArgs())))).create();
        } catch (IOException | ParseException exception) {
            MessageHelper.sendError(exception, event, this);
        }
    }

    private void addOptions(Options options) {
        Option body = new Option("b", "body", true, "Add body to the issue");
        Option title = new Option("t", "title", true, "Add title to the issue");
        body.setArgs(Option.UNLIMITED_VALUES);
        title.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(body);
        options.addOption(title);
    }

    private String[] getOrDefault(CommandLine options, String option, String o) {
        return options.hasOption(option) ? options.getOptionValues(option) : new String[]{o};
    }
}
