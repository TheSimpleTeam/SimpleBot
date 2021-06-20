package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class CreateChannel extends Command {

    public CreateChannel() {
        this.name = "createchannel";
        this.cooldown = 30;
        this.arguments = "<type : text|voice> <nom> <identifiant de la catégorie>";
        this.help = "Crée un channel selon le type, le nom et la catégorie spécifiés.";
        this.example = "text général 846048803554852905";
        this.category = CommandCategories.STAFF.category;
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.userPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.guildOnly = true;
        this.aliases = new String[]{"createc","cc","ccommand"};
    }
    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        try{
            if(!(args[2].toCharArray().length > 100)) {
                if(args[1].equalsIgnoreCase("text")) {
                    event.getGuild().createTextChannel(args[2],event.getGuild().getCategoryById(args[3])).queue();
                } else if (args[1].equalsIgnoreCase("voice")) {
                    event.getGuild().createVoiceChannel(args[2],event.getGuild().getCategoryById(args[3])).queue();
                } else {
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le type à spécifier doit être soit \"text\" soit \"voice\"!");
                }
            } else {
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le nom du channel ne doit pas dépasser les 100 caractères !");
            }
        } catch (ArrayIndexOutOfBoundsException e){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Le type à spécifier doit être soit \"text\" soit \"voice\".\nLe nom du nouveau salon ne " +
                    "doit pas dépasser les 100 caractères.\nMettre une catégorie n'est pas obligatoire.");
        }
    }
}
