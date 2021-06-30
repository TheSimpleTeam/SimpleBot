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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MutedRoleCommand extends Command {
    private static final String WHITE_CHECK_MARK = "\u2705";
    private static final String X = "\u274c";
    public MutedRoleCommand(){
        this.category = CommandCategories.CONFIG.category;
        this.aliases = new String[]{"mr","muter","mrole"};
        this.name = "muterole";
        this.arguments = "<identifiant du rôle>";
        this.help = "Défini le rôle que nécessite la commande !mute pour fonctionner lorsque le serveur a choisi d'utiliser une rôle.";
        this.cooldown = 5;
        this.example = "660114547646005280";
        this.guildOnly = true;
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }
    @Override
    protected void execute(CommandEvent event) {
        if(event.getAuthor().isBot()) return;
        String[] args = event.getArgs().split(" \\s+");
        if (args.length != 1) {
            event.replyError(MessageHelper.syntaxError(event.getAuthor(),this));
            return;
        }
        Role mutedRole = event.getGuild().getRoleById(args[0]);
        if (mutedRole == null) {
            event.replyError(MessageHelper.formattedMention(event.getAuthor())+"Ce rôle n'existe pas.");
            return;
        }
        event.getMessage().delete().queue();
        mutedRoleParameter(event);
    }
    public static void mutedRoleParameter(CommandEvent event){
        String[] args = event.getArgs().split(" \\s+");
        MessageEmbed embedMR = new EmbedBuilder()
                .setTitle("Êtes-vous sûr de vouloir faire cela ?")
                .addField("Qu'est-ce que cela va faire ?", "Si vous faites cette commande avec l'argument demandé, cela définirera le rôle qu'aura la personne mentionnée lors de l'execution de la commande !mute quand vous avez sélectionner le paramètre **WithoutMutedRole** sur **Non**.",false)
                .addField("Pourquoi faire cela ?", "Parce que le rôle indispensable lorsque vous avez sélectionner le paramètre **WithoutMutedRole** sur **Non**.", false)
                .addField("Informations","Vous devez répondre pendant les une minute qui suivent après avoir fait la commande.\n",false)
                .addField("Choix", "✅ Oui\n❌ Non", false)
                .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(0x2f3136)
                .build();
        event.reply(embedMR,message -> RestAction.allOf( // Allow multiple RestActions
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
                        event.replyInDm("Vous ne pouvez pas modifier le rôle qu'aura la personne mentionnée dans la commande !mute !",messageNotAuthor -> messageNotAuthor.delete().queueAfter(10, TimeUnit.SECONDS));
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
                        Main.getServerConfig().mutedRole.put(event.getGuild().getId(), event.getGuild().getRoleById(args[0]).getId()); // Don't worry, we already checked if it was null above
                        event.replySuccess("Le rôle " + event.getGuild().getRoleById(args[0]).getAsMention() + " à bien été défini.",messageSuccess -> messageSuccess.delete().queueAfter(10, TimeUnit.SECONDS));
                    } else { // If it's X emoji
                        event.reply(MessageHelper.formattedMention(event.getAuthor())+"Choix du rôle annulé.",messageNO -> messageNO.delete().queueAfter(10,TimeUnit.SECONDS));
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
