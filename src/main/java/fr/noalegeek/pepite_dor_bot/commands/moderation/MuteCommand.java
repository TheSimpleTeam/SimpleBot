package fr.noalegeek.pepite_dor_bot.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
import fr.noalegeek.pepite_dor_bot.commands.config.MutedRoleCommand;
import fr.noalegeek.pepite_dor_bot.commands.config.WithoutMutedRoleCommand;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.concurrent.TimeUnit;

public class MuteCommand extends Command {
    private static final String WHITE_CHECK_MARK = "\u2705";
    private static final String X = "\u274c";
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
            if(Main.getServerConfig().withoutMutedRole.get(event.getGuild().getId())) { // If withoutMutedRole is true
                muteWithoutMutedRole(event, targetMember, args[1]);
            } else if(!Main.getServerConfig().withoutMutedRole.get(event.getGuild().getId())){ // If withoutMutedRole is false
                if(Main.getServerConfig().mutedRole.get(event.getGuild().getId()) == null){
                    event.reply(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas configurer le rôle qu'aura la personne mentionnée. Voulez-vous configurer ce rôle ?", messageMutedRoleConfig -> RestAction.allOf(
                            messageMutedRoleConfig.addReaction(WHITE_CHECK_MARK),
                            messageMutedRoleConfig.addReaction(X)
                    ).queue(unused -> Main.getEventWaiter().waitForEvent(
                            GuildMessageReactionAddEvent.class,
                            e -> {
                                if(!e.getMessageId().equals(messageMutedRoleConfig.getId())) return false; // Verify if the message is the same
                                if(e.getUser().isBot()) return false; // Ignore bots
                                MessageReaction.ReactionEmote emoteDefaultConfig = e.getReactionEmote(); // Get the emote
                                if(!emoteDefaultConfig.isEmoji()) return false; // Verify if the emote is a unicode and not a custom
                                if(!e.getUser().getId().equals(event.getAuthor().getId())){ // Verify if the user who reacted is the author of the message
                                    event.replyInDm("Vous ne pouvez pas configurer le rôle qu'aura la personne mentionnée !",messageNotAuthor -> messageNotAuthor.delete().queueAfter(10,TimeUnit.SECONDS));
                                    messageMutedRoleConfig.removeReaction(emoteDefaultConfig.getEmoji(),e.getUser()).queue();
                                    return false;
                                }
                                if(!emoteDefaultConfig.getName().equals(WHITE_CHECK_MARK) && !emoteDefaultConfig.getName().equals(X)){ // Verify if the emote is one of these choices
                                    messageMutedRoleConfig.clearReactions(emoteDefaultConfig.getEmoji()).complete();
                                    event.replyInDm("Vous ne pouvez pas ajouter d'autres emojis que ceux déjà présent.", messageNotAuthor -> messageNotAuthor.delete().queueAfter(10, TimeUnit.SECONDS));
                                    return false;
                                }
                                return true;
                            },
                            e -> {
                                MessageReaction.ReactionEmote emoteDefaultConfig = e.getReactionEmote();
                                if(emoteDefaultConfig.getName().equals(WHITE_CHECK_MARK)){
                                    MutedRoleCommand.mutedRoleParameter(event);
                                } else {
                                    event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+"Vous ne pourrez pas mute la personne mentionnée avec un rôle car vous n'avez pas configurer le rôle qu'aura la personne mentionnée.");
                                }
                            },
                            1L, TimeUnit.MINUTES,
                            () -> {
                                event.reply(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas répondu dans les temps !");
                                messageMutedRoleConfig.delete().queue();
                            }
                    )));
                    muteWithMutedRole(event,targetMember,args[1]);
                }
            } else { // If withoutMutedRole is empty
                event.reply(MessageHelper.formattedMention(event.getAuthor()) + ":warning: Vous n'avez pas configurer la commande !mute. Voulez-vous configurer la commande !mute ?",messageEmptyConfig -> RestAction.allOf( // Allow multiple RestActions
                        messageEmptyConfig.addReaction(WHITE_CHECK_MARK),
                        messageEmptyConfig.addReaction(X)
                        ).queue(unused -> Main.getEventWaiter().waitForEvent(
                        GuildMessageReactionAddEvent.class,
                        e -> {
                            if(!e.getMessageId().equals(messageEmptyConfig.getId())) return false; // Verify if the message is the same
                            if(e.getUser().isBot()) return false; // Ignore bots
                            MessageReaction.ReactionEmote emote = e.getReactionEmote(); // Get the emote
                            if(!emote.isEmoji()) return false; // Verify if the emote is a unicode and not a custom
                            if(!e.getUser().getId().equals(event.getAuthor().getId())){ // Verify if the user who reacted is the author of the message
                                event.replyInDm("Vous ne pouvez pas répondre à cette question !",messageNotAuthor -> messageNotAuthor.delete().queueAfter(10, TimeUnit.SECONDS));
                                messageEmptyConfig.removeReaction(emote.getEmoji(),e.getUser()).queue();
                                return false;
                            }
                            if(!emote.getName().equals(WHITE_CHECK_MARK) && !emote.getName().equals(X)){ // Verify if the emote is one of these choices
                                messageEmptyConfig.clearReactions(emote.getEmoji()).complete();
                                event.replyInDm("Vous ne pouvez pas ajouter d'autres emojis que ceux déjà présent.", messageNotAuthor -> messageNotAuthor.delete().queueAfter(10, TimeUnit.SECONDS));
                                return false;
                            }
                            return true;
                        },
                        e -> {
                            MessageReaction.ReactionEmote emote = e.getReactionEmote();
                            messageEmptyConfig.delete().queue();
                            if(emote.getName().equals(WHITE_CHECK_MARK)){ // If it's the WHITE_CHECK_MARK emoji
                                event.reply(MessageHelper.formattedMention(event.getAuthor())+":information_source: Vous devrez refaire la commande !mute après l'avoir paramétrée.");
                                WithoutMutedRoleCommand.withoutMutedRoleParameter(event);
                            } else { // If it's X emoji
                                event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+"Question annulée. Voulez-vous mute l'utilisateur sans avoir configurer de rôle ? (Le paramètre par défaut lorsque vous n'avez pas configurer le paramètre **WithoutMutedRole**)",messageDefaultConfig -> RestAction.allOf(
                                        messageDefaultConfig.addReaction(WHITE_CHECK_MARK),
                                        messageDefaultConfig.addReaction(X)
                                ).queue(unused1 -> Main.getEventWaiter().waitForEvent(
                                        GuildMessageReactionAddEvent.class,
                                        f -> {
                                            if(!f.getMessageId().equals(messageDefaultConfig.getId())) return false; // Verify if the message is the same
                                            if(f.getUser().isBot()) return false; // Ignore bots
                                            MessageReaction.ReactionEmote emoteDefaultConfig = f.getReactionEmote(); // Get the emote
                                            if(!emoteDefaultConfig.isEmoji()) return false; // Verify if the emote is a unicode and not a custom
                                            if(!f.getUser().getId().equals(event.getAuthor().getId())){ // Verify if the user who reacted is the author of the message
                                                event.replyInDm("Vous ne pouvez pas décider si la personne mentionnée va être mute sans rôle !",messageNotAuthor -> messageNotAuthor.delete().queueAfter(10,TimeUnit.SECONDS));
                                                messageDefaultConfig.removeReaction(emoteDefaultConfig.getEmoji(),f.getUser()).queue();
                                                return false;
                                            }
                                            if(!emoteDefaultConfig.getName().equals(WHITE_CHECK_MARK) && !emoteDefaultConfig.getName().equals(X)){ // Verify if the emote is one of these choices
                                                messageDefaultConfig.clearReactions(emoteDefaultConfig.getEmoji()).complete();
                                                event.replyInDm("Vous ne pouvez pas ajouter d'autres emojis que ceux déjà présent.", messageNotAuthor -> messageNotAuthor.delete().queueAfter(10, TimeUnit.SECONDS));
                                                return false;
                                            }
                                            return true;
                                        },
                                        f -> {
                                            MessageReaction.ReactionEmote emoteDefaultConfig = f.getReactionEmote();
                                            if(emoteDefaultConfig.getName().equals(WHITE_CHECK_MARK)){
                                                muteWithMutedRole(event,targetMember,args[1]);
                                            } else {
                                                event.replySuccess(MessageHelper.formattedMention(event.getAuthor())+"Execution de la commande !mute annulée.");
                                            }
                                        },
                                            1L, TimeUnit.MINUTES,
                                        () -> {
                                            event.reply(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas répondu dans les temps !");
                                            messageDefaultConfig.delete().queue();
                                        }
                                )));
                            }
                        },
                        1L, TimeUnit.MINUTES,
                        () -> {
                            event.reply(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas répondu dans les temps !");
                            messageEmptyConfig.delete().queue();
                        }
                        ))
                );
            }
        } catch (IndexOutOfBoundsException e){
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this)+"Mettre une raison n'est pas obligatoire.");
        }
    }
    public static void muteWithMutedRole(CommandEvent event, Member targetMember, String reason){
        if(targetMember.getRoles().contains(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())))){ // Unmute the target
            targetMember.getRoles().remove(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())));
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été unmuté pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été unmuté.");
        } else { // Mute the target
            targetMember.getRoles().add(event.getGuild().getRoleById(Main.getServerConfig().mutedRole.get(event.getGuild().getId())));
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muté pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muté.");
        }
    }
    public static void muteWithoutMutedRole(CommandEvent event, Member targetMember, String reason){
        int verifyMuted = 0;
        for (GuildChannel gc : event.getGuild().getTextChannels()) {
            //Verify if in every channels, the target hasn't the permission to write
            for (PermissionOverride po : gc.getMemberPermissionOverrides()) {
                if (!targetMember.hasPermission(gc, Permission.MESSAGE_WRITE)) {
                    verifyMuted++;
                }
            }
        }
        if (verifyMuted == event.getGuild().getTextChannels().size()) {
            //Unmute the target
            for (GuildChannel gc : event.getGuild().getTextChannels()) {
                gc.putPermissionOverride(targetMember).setAllow(Permission.MESSAGE_WRITE).queue();
            }
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été unmuté pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été unmuté.");
        } else {
            //Mute the target
            for (GuildChannel gc : event.getGuild().getTextChannels()) {
                gc.putPermissionOverride(targetMember).setDeny(Permission.MESSAGE_WRITE).queue();
            }
            if (reason != null) {
                event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muté pour la raison " + reason + ".");
                return;
            }
            event.replySuccess(MessageHelper.formattedMention(event.getAuthor()) + targetMember.getEffectiveName() + " a bien été muté.");
        }
    }
}
