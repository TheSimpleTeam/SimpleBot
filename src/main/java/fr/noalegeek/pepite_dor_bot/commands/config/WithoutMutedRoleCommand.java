package fr.noalegeek.pepite_dor_bot.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.commands.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.helpers.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class WithoutMutedRoleCommand extends Command {
    private static final String WHITE_CHECK_MARK = "\u2705";
    private static final String X = "\u274c";
    public WithoutMutedRoleCommand(){
        this.category = CommandCategories.CONFIG.category;
        this.aliases = new String[]{"wmr","withoutmr","withoutmuter","withoutmrole","wmuterole","wmrole","wmuter"};
        this.name = "withoutmuterole";
        this.help = "Défini si la commande !mute doit nécessiter d'un rôle ou non. Si vous faites cette commande, cela inversera le paramètre de votre serveur.";
        this.cooldown = 5;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        this.guildOnly = true;
    }
    @Override
    protected void execute(CommandEvent event) {
        if (event.getAuthor().isBot()) return;
        event.getMessage().delete().queue();
        withoutMutedRoleParameter(event);
    }
    public static void withoutMutedRoleParameter(CommandEvent event){
        MessageEmbed embedWMR = new EmbedBuilder()
                .setTitle("Êtes-vous sûr de vouloir faire cela ?")
                .addField("Qu'est-ce que cela va faire ?", "Si vous faites cette commande, cela inversera le paramètre **WithoutMutedRole** de votre serveur.\nLe paramètre **WithoutMutedRole** est mis par défaut sur **Non**.", false)
                .addField("Pourquoi faire cela ?", "Parce que la commande !mute nécessite ce paramètre.\nSi ce paramètre est défini sur **Non**, la commande !mute va nécessiter d'un rôle qui faudra définir.\nPar défaut, la commande !mute fonctionnera sans rôle à spécifier.", false)
                .addField("Informations","Vous devez répondre pendant les une minute qui suivent après avoir fait la commande.\n",false)
                .addField("Choix", "✅ Oui\n❌ Non", false)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(0x2f3136)
                .build();
        event.reply(embedWMR,message -> RestAction.allOf( // Allow multiple RestActions
                message.addReaction(WHITE_CHECK_MARK),
                message.addReaction(X)
                ).queue(unused -> Main.getEventWaiter().waitForEvent(
                GuildMessageReactionAddEvent.class,
                e -> {
                    if(!e.getMessageId().equals(message.getId())) return false; // Verify if the message is the same
                    if(e.getUser().isBot()) return false; // Ignore bots
                    MessageReaction.ReactionEmote emote = e.getReactionEmote(); // Get the emote
                    if(!emote.isEmoji()) return false; // Verify if the emote is a unicode and not a custom
                    if(!e.getUser().getId().equals(event.getAuthor().getId())){ // Verify if the user who reacted is the author of the message
                        event.replyInDm("Vous ne pouvez pas modifier la configuration du serveur !",messageNotAuthor -> messageNotAuthor.delete().queueAfter(10,TimeUnit.SECONDS));
                        message.removeReaction(emote.getEmoji(),e.getUser()).queue();
                        return false;
                    }
                    if(!emote.getName().equals(WHITE_CHECK_MARK) && !emote.getName().equals(X)){ // Verify if the emote is one of these choices
                        message.clearReactions(emote.getEmoji()).complete();
                        event.replyInDm("Vous ne pouvez pas ajouter d'autres emojis que ceux déjà présent.", messageNotAuthor -> messageNotAuthor.delete().queueAfter(10, TimeUnit.SECONDS));
                        return false;
                    }
                    return true;
                },
                e -> {
                    MessageReaction.ReactionEmote emote = e.getReactionEmote();
                    message.delete().queue();
                    if(emote.getName().equals(WHITE_CHECK_MARK)){ // If it's the WHITE_CHECK_MARK emoji
                        String withoutMutedRole;
                        if(Main.getServerConfig().withoutMutedRole.get(event.getGuild().getId())){ // If withoutMutedRole is true, withoutMutedRole = false
                            withoutMutedRole = "false";
                            Main.getServerConfig().withoutMutedRole.put(event.getGuild().getId(),false);
                        } else { // If withoutMutedRole is false, withoutMutedRole = true
                            withoutMutedRole = "true";
                            Main.getServerConfig().withoutMutedRole.put(event.getGuild().getId(), true);
                        }
                        event.reply(MessageHelper.formattedMention(event.getAuthor())+"Le paramètre **WithoutMutedRole** a été défini sur **"+withoutMutedRole+"**.",messageYES -> messageYES.delete().queueAfter(10,TimeUnit.SECONDS));
                    } else { // If it's X emoji
                        event.reply(MessageHelper.formattedMention(event.getAuthor())+"Choix du paramètre **WithoutMutedRole** annulé.",messageNO -> messageNO.delete().queueAfter(10,TimeUnit.SECONDS));
                    }
                },
                1L, TimeUnit.MINUTES,
                () -> {
                    event.reply(MessageHelper.formattedMention(event.getAuthor())+"Vous n'avez pas répondu dans les temps !");
                    message.delete().queue();
                }
                ))
        );
    }
}
