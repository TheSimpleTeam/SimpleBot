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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class SpigotCommand extends Command {

    public SpigotCommand() {
        this.name = "spigot";
        this.aliases = new String[]{"spigo", "spig", "spi", "plugin", "spige", "plu", "plug", "plugi", "spiget", "pl", "plugins"};
        this.cooldown = 5;
        this.example = "80802";
        this.help = "help.spigot";
        this.category = CommandCategories.UTILITY.category;
        this.arguments = "arguments.spigot";
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(event.getArgs().replaceAll("\\s+", "").isEmpty()) {
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        if(args[0].chars().allMatch(Character::isDigit)) { //Search for plugin with an ID
            try {
                Resource pluginId = new Resource(Integer.parseInt(args[0]));
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "success.spigot.pluginID.success", null, null, pluginId.getResourceIconLink() == null ? "https://static.spigotmc.org/styles/spigot/xenresource/resource_icon.png" : pluginId.getResourceIconLink().toString(), (Object[]) null)
                        .addField(MessageHelper.translateMessage(event, "success.spigot.pluginID.pluginName"), pluginId.getResourceName(), false)
                        .addField(MessageHelper.translateMessage(event, "success.spigot.pluginID.pluginLink"), pluginId.getResourceLink(), false)
                        .addField(MessageHelper.translateMessage(event, "success.spigot.pluginID.pluginID"), args[0], false)
                        .addField(MessageHelper.translateMessage(event, "success.spigot.pluginID.description"), MessageHelper.getDescription(pluginId.getDescription().replaceAll(".SpoilerTarget\">Spoiler:", "")), false)
                        .build()).build());
            } catch (IOException exception) {
                if(exception instanceof FileNotFoundException) {
                    event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.spigot.pluginID.pluginNull", null, null, null, args[0]).build()).build());
                    return;
                }
                MessageHelper.sendError(exception, event, this);
            } catch (NumberFormatException exception){
                event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.commands.numberTooLarge", null, null, null, args[0]).build()).build());
            }
        } else {
            //Search for a Spigot user
            if(args[0].equalsIgnoreCase("user")) {
                try {
                    List<Author> users = Author.getByName(args[1]);
                    EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, "success.spigot.user.success", null, null, users.stream().findFirst().get().getIconURL(), args[1]);
                    for (Author author : users) {
                        embedBuilder.addField(author.getName(), String.format("https://www.spigotmc.org/resources/authors/%s.%o/", author.getName(), author.getId()), true);
                    }
                    event.reply(new MessageBuilder(embedBuilder.build()).build());
                } catch (IOException exception){
                    if(exception instanceof FileNotFoundException) {
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.spigot.user", null, null, null, args[1]).build()).build());
                        return;
                    }
                    MessageHelper.sendError(exception, event, this);
                }
            } else {
                //Search for plugin with his name
                try {
                    List<Resource> resources = Resource.getResourcesByName(event.getArgs());
                    EmbedBuilder embedBuilder = MessageHelper.getEmbed(event, resources.size() == 1 ? "success.spigot.pluginName.success.singular" : "success.spigot.pluginName.success.plural", null, null, "https://static.spigotmc.org/img/spigot.png", (Object[]) null);
                    for (Resource resource : resources) {
                        embedBuilder.addField(resource.getResourceName(), resource.getResourceLink(), true);
                    }
                    event.reply(embedBuilder.build());
                } catch (IOException | NullPointerException e) {
                    if(e instanceof FileNotFoundException) {
                        event.reply(new MessageBuilder(MessageHelper.getEmbed(event, "error.spigot.pluginName", null, null, null, event.getArgs()).build()).build());
                        return;
                    }
                    MessageHelper.sendError(e, event, this);
                }
            }
        }
    }
}
