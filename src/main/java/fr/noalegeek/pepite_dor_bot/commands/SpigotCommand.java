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
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class SpigotCommand extends Command {

    public SpigotCommand() {
        this.name = "spigot";
        this.aliases = new String[]{"spiget", "plugin", "pl", "plugins"};
        this.cooldown = 5;
        this.example = "80802";
        this.help = "help.spigot";
        this.category = CommandCategories.INFO.category;
        this.arguments = "arguments.spigot";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(event.getArgs().replaceAll("\\s+", "").isEmpty()) {
            event.reply(MessageHelper.syntaxError(event, this, null));
            return;
        }
        if(args[0].chars().allMatch(Character::isDigit)) {
            //Search for plugin with a ID
            try {
                Resource pluginId = new Resource(Integer.parseInt(args[0]));
                EmbedBuilder successPluginIDEmbed = new EmbedBuilder()
                        .setTitle("\u2705 " + MessageHelper.translateMessage("success.spigot.pluginID.success", event))
                        .addField(MessageHelper.translateMessage("success.spigot.pluginID.pluginName", event), pluginId.getResourceName(), false)
                        .addField(MessageHelper.translateMessage("success.spigot.pluginID.pluginLink", event), pluginId.getResourceLink(), false)
                        .addField(MessageHelper.translateMessage("success.spigot.pluginID.pluginID", event), args[0], false)
                        .addField(MessageHelper.translateMessage("success.spigot.pluginID.description", event), getDescription(pluginId.getDescription().replaceAll(".SpoilerTarget\">Spoiler:", "")), false)
                        .setColor(Color.GREEN)
                        .setThumbnail(pluginId.getResourceIconLink() == null ? "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png" : pluginId.getResourceIconLink().toString())
                        .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl());
                event.reply(successPluginIDEmbed.build());
            } catch (IOException e) {
                if(e instanceof FileNotFoundException) {
                    EmbedBuilder errorEmptyUserListEmbed = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                            .setTimestamp(Instant.now())
                            .setTitle("\u274C " + String.format(MessageHelper.translateMessage("error.spigot.pluginID.pluginNull", event), args[0]));
                    event.reply(new MessageBuilder(errorEmptyUserListEmbed.build()).build());
                    return;
                }
                MessageHelper.sendError(e, event);
            } catch (NumberFormatException e){
                EmbedBuilder errorNumberTooLargeEmbed = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                        .setTimestamp(Instant.now())
                        .setTitle("\u274C " + String.format(MessageHelper.translateMessage("error.spigot.pluginID.numberTooLarge", event), args[0]));
                event.reply(new MessageBuilder(errorNumberTooLargeEmbed.build()).build());
            }
        } else {
            //Search for a Spigot user
            if(args[0].equalsIgnoreCase("user")) {
                try {
                    List<Author> users = Author.getByName(args[1]);
                    EmbedBuilder successUserEmbed = new EmbedBuilder()
                            .setTitle("\u2705 " + String.format(MessageHelper.translateMessage("success.spigot.user.success", event), args[1]))
                            .setTimestamp(Instant.now())
                            .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                            .setColor(Color.GREEN)
                            .setThumbnail(users.stream().findFirst().get().getIconURL());
                    for (Author author : users) {
                        successUserEmbed.addField(author.getName(), String.format("https://www.spigotmc.org/resources/authors/%s.%o/", author.getName(), author.getId()), true);
                    }
                    event.reply(successUserEmbed.build());
                } catch (IOException exception){
                    if(exception instanceof FileNotFoundException) {
                        EmbedBuilder errorEmptyUserListEmbed = new EmbedBuilder()
                                .setColor(Color.RED)
                                .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle("\u274C " + String.format(MessageHelper.translateMessage("error.spigot.user.listNull", event), args[1]));
                        event.reply(new MessageBuilder(errorEmptyUserListEmbed.build()).build());
                        return;
                    }
                    MessageHelper.sendError(exception, event);
                }
            } else {
                //Search for plugin with his name
                try {
                    List<Resource> resources = Resource.getResourcesByName(event.getArgs());
                    EmbedBuilder successPluginNameEmbed = new EmbedBuilder()
                            .setTitle("Resources list")
                            .setThumbnail("https://static.spigotmc.org/img/spigot.png")
                            .setTimestamp(Instant.now())
                            .setFooter(event.getAuthor().getName(), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                            .setColor(Color.GREEN);
                    for (Resource resource : resources) {
                        successPluginNameEmbed.addField(resource.getResourceName(), resource.getResourceLink(), true);
                    }
                    event.reply(successPluginNameEmbed.build());
                } catch (IOException | NullPointerException exception) {
                    if(exception instanceof FileNotFoundException) {
                        event.replyError("This resource does not exist");
                        return;
                    }
                    MessageHelper.sendError(exception, event);
                }
            }
        }
    }

    private String getDescription(String desc) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < desc.toCharArray().length; i++) {
            if (i == 1020) {
                builder.append("...");
                break;
            }
            builder.append(desc.toCharArray()[i]);
        }
        return builder.toString();
    }
}
