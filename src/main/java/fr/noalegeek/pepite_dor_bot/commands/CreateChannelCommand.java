package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;

public class CreateChannelCommand extends Command {

    public CreateChannelCommand() {
        this.name = "createchannel";
        this.cooldown = 30;
        this.arguments = "<type : texte|vocal> <nom> [identifiant de la catégorie]";
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
        String[] args = event.getArgs().split("\\s+");
        try{
            if(args[1].toCharArray().length > 100) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le nom du channel ne doit pas dépasser les 100 caractères !");
                return;
            }
            switch (args[0]) {
                case "texte":
                    event.getGuild().createTextChannel(args[2], event.getGuild().getCategoryById(args[3])).queue();
                case "vocal":
                    event.getGuild().createVoiceChannel(args[2], event.getGuild().getCategoryById(args[3])).queue();
                default:
                    event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Le type à spécifier doit être soit **texte** soit **vocal** !");
            }
        } catch (ArrayIndexOutOfBoundsException e){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Le type à spécifier doit être soit **texte** soit **vocal**.\n" +
                    "Le nom du nouveau salon ne doit pas dépasser les 100 caractères.\n" +
                    "Mettre une catégorie n'est pas obligatoire.");
        }
    }
}
