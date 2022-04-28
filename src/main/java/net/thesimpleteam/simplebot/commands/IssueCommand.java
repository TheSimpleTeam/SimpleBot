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

package net.thesimpleteam.simplebot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.thesimpleteam.simplebot.SimpleBot;
import net.thesimpleteam.simplebot.commands.annotations.RequireConfig;
import net.thesimpleteam.simplebot.enums.CommandCategories;
import net.thesimpleteam.simplebot.enums.GithubInfo;
import net.thesimpleteam.simplebot.utils.MessageHelper;
import org.apache.commons.cli.*;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

@RequireConfig("botGithubToken")
public class IssueCommand extends Command {

    private final GitHub github;

    public IssueCommand() throws IOException {
        this.name = "issue";
        this.cooldown = 600;
        this.help = "help.issue";
        this.arguments = "arguments.issue";
        this.example = "example.issue";
        this.category = CommandCategories.MISC.category;
        this.github = new GitHubBuilder().withOAuthToken(SimpleBot.getInfos().botGithubToken()).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s");
        if(event.getArgs().isBlank() || args.length == 0) {
            MessageHelper.syntaxError(event, this, "information.issue");
            return;
        }
        try {
            this.github.checkApiUrlValidity();
        } catch (IOException ex) {
            event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.issue.githubTokenNotValid", null, null, null).build()).build());
            return;
        }
        try {
            Options options = new Options();
            addOptions(options);
            CommandLine parser = new DefaultParser().parse(options, args);
            if (parser.getOptions().length != 0 && !parser.hasOption("body")) {
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.issue.bodyParameterNotHere", null, null, null).build()).build());
                return;
            }
           this.github.getRepositoryById(GithubInfo.REPOSITORY_ID.id).createIssue(String.join(" ", getOrDefault(parser, "title",
                   MessageHelper.translateMessage(event, "text.issue.issue"))))
                   .body(String.format(MessageHelper.translateMessage(event, "success.issue.success") + "\n\n" + MessageHelper.translateMessage(event, "text.issue.issue") +
                           MessageHelper.translateMessage(event, "text.issue.twoSuperimposedPoints") + "\n\n%s", MessageHelper.getTag(event.getAuthor()), event.getAuthor().getId(),
                           event.getGuild().getName(), event.getGuild().getId(), String.join(" ", getOrDefault(new DefaultParser().parse(options, args),
                                   "body", event.getArgs())))).create();
        } catch (IOException | ParseException e) {
            MessageHelper.sendError(e, event, this);
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
