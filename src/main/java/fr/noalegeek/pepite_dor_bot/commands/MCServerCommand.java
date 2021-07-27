package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.helpers.RequestHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.io.IOException;
import java.time.Instant;

public class MCServerCommand extends Command {

    String baseURL = "https://api.mcsrvstat.us/2/";

    public MCServerCommand() {
        this.name = "minecraftserveur";
        this.cooldown = 5;
        this.help = "Donne la version, le nombre de joueurs connectés et le port d'un serveur.";
        this.example = "hypixel.net";
        this.aliases = new String[]{"minecraftserver","minecraftserveu","minecraftserve","minecraftserv","minecraftser","minecraftse","minecrafts","mcserveur",
                "mcserver","mcserveu","mcserve","mcser","mcse","mcs","minecserveur","minecserveu","minecserver","minecserve","minecserv","minecser","minecse","minecs",
                "mcraftserveur","mcraftserveu","mcraftserver","mcraftserve","mcraftserv","mcraftser","mcraftse","mcrafts"};
        this.arguments = "<IP d'un serveur Minecraft>";
        this.category = CommandCategories.FUN.category;
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        Main.LOGGER.info(args[0]);
        try {
            JsonObject object = Main.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest(baseURL + args[0])), JsonObject.class);
            if (!object.get("online").getAsBoolean()) {
                event.replyError("Le serveur est hors-ligne.");
                return;
            }
            getServerInfos(object, event);
        } catch (IOException ex) {
            MessageHelper.sendError(ex, event);
        }
    }

    //From https://github.com/Minemobs/McStatusJava/blob/master/src/main/java/fr/minemobs/test/Main.java
    private void getServerInfos(JsonObject jo, CommandEvent event) {
        String ip = jo.get("ip").getAsString();
        String port = jo.get("port").getAsString();
        String age = jo.get("version").getAsString();
        JsonObject player = jo.get("players").getAsJsonObject();
        int playerList = player.get("online").getAsInt();

        MessageEmbed embed = new EmbedBuilder()
                .setTimestamp(Instant.now())
                .addField("IP :", ip, false)
                .addField("Port :", port, false)
                .addField("Version :", age, false)
                .addField("Nombre de joueurs :", String.valueOf(playerList), false)
                .setColor(Color.GREEN)
                .build();
        event.reply(embed);
    }
}
