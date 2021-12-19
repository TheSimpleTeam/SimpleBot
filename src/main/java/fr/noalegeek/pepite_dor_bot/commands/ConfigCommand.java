package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;

public class ConfigCommand extends Command {

    public ConfigCommand(){
        this.name = "config";
        this.cooldown = 5;
        this.help = "help.config";
        this.example = "example.config";
        this.aliases = new String[]{"cf", "parameter", "par"};
        this.arguments = "arguments.config";
        this.category = CommandCategories.CONFIG.category;
        this.guildOnly = true;
        this.guildOwnerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] args = event.getArgs().split("\\s+");
        if(args.length != 2 && args.length != 3){
            MessageHelper.syntaxError(event, this, null);
            return;
        }
        switch(args.length){
            case 2 -> {
                switch(args[0]){
                    case "joinrole", "joinRole" -> {
                        if(args[1].equalsIgnoreCase("reset")){
                            if(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null){
                                EmbedBuilder errorNotConfiguredEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.notConfigured", event)));
                                event.reply(new MessageBuilder(errorNotConfiguredEmbed.build()).build());
                                return;
                            }
                            Main.getServerConfig().guildJoinRole().remove(event.getGuild().getId());
                            EmbedBuilder successResetEmbed = new EmbedBuilder()
                                    .setColor(Color.GREEN)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.config.joinRole.reset", event)));
                            event.reply(new MessageBuilder(successResetEmbed.build()).build());
                        } else {
                            if(args[1].replaceAll("\\D+", "").isEmpty()){
                                EmbedBuilder errorNotValidIDEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.notValidID", event)));
                                event.reply(new MessageBuilder(errorNotValidIDEmbed.build()).build());
                                return;
                            }
                            if (event.getGuild().getRoleById(args[1].replaceAll("\\D+","")) == null) {
                                EmbedBuilder errorRoleNullEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.roleNull", event)));
                                event.reply(new MessageBuilder(errorRoleNullEmbed.build()).build());
                                return;
                            }
                            if (event.getGuild().getRoleById(args[1].replaceAll("\\D+","")).isManaged()) {
                                EmbedBuilder errorRoleManagedEmbed = new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.roleManaged", event)));
                                event.reply(new MessageBuilder(errorRoleManagedEmbed.build()).build());
                                return;
                            }
                            if(Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()) == null || !Main.getServerConfig().guildJoinRole().get(event.getGuild().getId()).equals(event.getGuild().getRoleById(args[1].replaceAll("\\D+","")).getId())){
                                Main.getServerConfig().guildJoinRole().put(event.getGuild().getId(), event.getGuild().getRoleById(args[1].replaceAll("\\D+","")).getId());
                                EmbedBuilder successResetEmbed = new EmbedBuilder()
                                        .setColor(Color.GREEN)
                                        .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                        .setTimestamp(Instant.now())
                                        .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, MessageHelper.translateMessage("success.config.joinRole.configured", event)));
                                event.reply(new MessageBuilder(successResetEmbed.build()).build());
                                return;
                            }
                            EmbedBuilder errorRoleManagedEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.joinRole.sameAsConfigured", event)));
                            event.reply(new MessageBuilder(errorRoleManagedEmbed.build()).build());
                        }
                    }
                    case "localization" -> {
                        if(Arrays.stream(Main.getLangs()).noneMatch(lang -> lang.equalsIgnoreCase(args[1]))){
                            EmbedBuilder errorLanguageDontExistEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.localization.languageDontExist", event)));
                            event.reply(new MessageBuilder(errorLanguageDontExistEmbed.build()).build());
                            return;
                        }
                        if(args[1].equals(Main.getServerConfig().language().get(event.getGuild().getId()))){
                            EmbedBuilder errorSameAsConfiguredEmbed = new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                    .setTimestamp(Instant.now())
                                    .setTitle(String.format("%s %s", UnicodeCharacters.crossMarkEmoji, MessageHelper.translateMessage("error.config.localization.sameAsConfig", event)));
                            event.reply(new MessageBuilder(errorSameAsConfiguredEmbed.build()).build());
                            return;
                        }
                        Main.getServerConfig().language().put(event.getGuild().getId(), args[1]);
                        EmbedBuilder successEmbed = new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setFooter(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl())
                                .setTimestamp(Instant.now())
                                .setTitle(String.format("%s %s", UnicodeCharacters.whiteHeavyCheckMarkEmoji, String.format(MessageHelper.translateMessage("success.config.localization.configured", event))), String.format("%s%s", ":flag_" + args[0].replace("en","us: / :flag_gb"), ':'));
                        event.reply(new MessageBuilder(successEmbed.build()).build());
                    }
                }
            }
            case 3 -> {

            }
        }
    }
}
