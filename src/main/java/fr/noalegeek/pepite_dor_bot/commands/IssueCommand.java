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
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.annotations.RequireConfig;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

@RequireConfig("botGithubToken")
public class IssueCommand extends Command {

    private final GitHub gh;

    public IssueCommand() throws IOException {
        this.name = "issue";
        this.cooldown = 30;
        this.gh = new GitHubBuilder().withOAuthToken(Main.getInfos().botGithubToken()).build();
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getArgs().split("\\s+").length == 0) {
            MessageHelper.syntaxError(event, this);
            return;
        }
        try {
            this.gh.checkApiUrlValidity();
        } catch (IOException ex) {
            event.replyError("Sorry, the github token is not valid !");
            return;
        }
        try {
            GHRepository repo = this.gh.getRepositoryById(370083508);
            repo.createIssue("Automatic Issue").body(String.format("""
                This issue has been made by %s on %s
                
                ----
                
                %s
                """, event.getAuthor().getName(), event.getGuild().getName(), event.getArgs())).create();
        } catch (IOException exception) {
            MessageHelper.sendError(exception, event);
        }

    }
}
