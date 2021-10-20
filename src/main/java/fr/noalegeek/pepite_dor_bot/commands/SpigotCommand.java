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
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import me.bluetree.spiget.Author;
import me.bluetree.spiget.Resource;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class SpigotCommand extends Command {

    public SpigotCommand() {
        this.name = "spigot";
        this.aliases = new String[]{"spiget", "plugin", "pl", "plugins"};
        this.cooldown = 5;
        this.example = "80802";
        this.help = "Donne ";
        this.category = CommandCategories.INFO.category;
        this.arguments = "arguments.spigot";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length == 0) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        if(args[0].chars().allMatch(Character::isDigit)) {
            try {
                Resource pluginId = new Resource(Integer.parseInt(args[0]));
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle(pluginId.getResourceName(), pluginId.getResourceLink())
                        .setColor(Color.yellow)
                        .setThumbnail(pluginId.getResourceIconLink() == null ? "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png" : pluginId.getResourceIconLink().toString())
                        .setFooter(event.getAuthor().getName(), getAvatarURL(event.getAuthor()));
                builder.setDescription(getDescription(pluginId.getDescription().replaceAll(".SpoilerTarget\">Spoiler:", "")));
                event.reply(builder.build());
            } catch (Exception e) {
                MessageHelper.sendError(e, event);
            }
        } else {
            //Search for plugin
            if(args[0].equalsIgnoreCase("user")) {
                List<Author> authors;
                try {
                    authors = Author.getByName(args[1]);
                } catch (IOException exception) {
                    MessageHelper.sendError(exception, event);
                    return;
                }
                EmbedBuilder b = new EmbedBuilder()
                        .setTitle("User list")
                        .setTimestamp(Instant.now())
                        .setFooter(event.getAuthor().getName(), getAvatarURL(event.getAuthor()));

                for (Author author : authors) {
                    b.addField(author.getName(), String.format("https://www.spigotmc.org/resources/authors/%s.%o/", author.getName(), author.getId()), true);
                }
                b.setThumbnail(authors.stream().findFirst().get().getIconURL());
                event.reply(b.build());
            } else {
                List<Resource> resources;
                try {
                    resources = Resource.getResourcesByName(args.length == 1 ? args[0] : args[1]);
                } catch (IOException | NullPointerException exception) {
                    MessageHelper.sendError(exception, event);
                    return;
                }
                EmbedBuilder b = new EmbedBuilder()
                        .setTitle("Resources list")
                        .setThumbnail("https://static.spigotmc.org/img/spigot.png")
                        .setTimestamp(Instant.now())
                        .setFooter(event.getAuthor().getName(), getAvatarURL(event.getAuthor()));

                for (Resource resource : resources) {
                    b.addField(resource.getResourceName(), resource.getResourceLink(), true);
                }
                event.reply(b.build());
            }
        }
    }

    private String getDescription(String desc) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < desc.toCharArray().length; i++) {
            if(i == 1020) {
                builder.append("...");
                break;
            }
            builder.append(desc.toCharArray()[i]);
        }
        return builder.toString();
    }

    private String getAvatarURL(User user) {
        return user.getAvatarUrl() == null ? user.getDefaultAvatarUrl() : user.getAvatarUrl();
    }
}
