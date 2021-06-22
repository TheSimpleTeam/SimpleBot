package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.EnumSet;
import java.util.Objects;

public class MuteCommand extends Command {
    public MuteCommand() {
        this.category = CommandCategories.STAFF.category;
        this.aliases = new String[]{"m","mu","mut"};
        this.name = "mute";
        this.arguments = "<mention de l'utilisateur> [raison]";
        this.help = "Mute définitivement un utilisateur avec une raison ou non. Unmute si la personne est déjà mute.";
        this.cooldown = 5;
        this.example = "@NoaLeGeek spam";
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split("\\s+");
        if(!event.getMember().hasPermission(Permission.VOICE_MUTE_OTHERS)) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor()) + "Vous n'avez pas la permission de faire cette commande.");
            return;
        }
        if(args.length > 2){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Mettre une raison n'est pas obligatoire.");
            return;
        }
        try{
            User target = event.getMessage().getMentionedUsers().get(0);
            if (target == null) {
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous devez spécifié une personne existante.");
                return;
            }
            if(event.getGuild().getMember(target) != null){
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"La personne doit être présente sur ce serveur.");
                return;
            }
            Member targetMember = event.getMessage().getMentionedMembers().get(0);
            if(!event.getSelfMember().canInteract(targetMember)){
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Le bot n'a pas les permissions de faire cela.");
                return;
            }
            if(!event.getMember().canInteract(targetMember)){
                event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas la permission de mute ce membre.");
                return;
            }
            int verifyMuted = 0;
            for(GuildChannel gc : event.getGuild().getTextChannels()){
                //Verify if in every channels, the target hasn't the permission to write
                for(PermissionOverride po : gc.getMemberPermissionOverrides()){
                    if(!targetMember.hasPermission(gc,Permission.MESSAGE_WRITE)) {
                        verifyMuted++;
                    }
                }
            }
            if(verifyMuted == event.getGuild().getTextChannels().size()){
                //Unmute the target
                for(GuildChannel gc : event.getGuild().getTextChannels()){
                    gc.putPermissionOverride(targetMember).setAllow(Permission.MESSAGE_WRITE).queue();
                }
                if(args[1] != null){
                    event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+targetMember.getEffectiveName()+" a bien été unmuté pour la raison "+args[1]+".");
                    return;
                }
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+targetMember.getEffectiveName()+" a bien été unmuté.");
            } else {
                //Mute the target
                for(GuildChannel gc : event.getGuild().getTextChannels()){
                    gc.putPermissionOverride(targetMember).setDeny(Permission.MESSAGE_WRITE).queue();
                }
                if(args[1] != null){
                    event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+targetMember.getEffectiveName()+" a bien été muté pour la raison "+args[1]+".");
                    return;
                }
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+targetMember.getEffectiveName()+" a bien été muté.");
            }
        } catch (IndexOutOfBoundsException e){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Mettre une raison n'est pas obligatoire.");
        }
    }
}
