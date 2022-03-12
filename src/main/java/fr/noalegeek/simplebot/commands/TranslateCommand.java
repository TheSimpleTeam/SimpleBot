package fr.noalegeek.simplebot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.simplebot.SimpleBot;
import fr.noalegeek.simplebot.enums.CommandCategories;
import fr.noalegeek.simplebot.utils.MessageHelper;
import fr.noalegeek.simplebot.utils.RequestHelper;
import fr.noalegeek.simplebot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.awt.Color;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TranslateCommand extends Command {

    public TranslateCommand() {
        this.name = "translate";
        this.aliases = new String[]{"tr"};
        this.example = "example.translate";
        this.arguments = "arguments.translate";
        this.help = "help.translate";
        this.hidden = true;
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (!event.getArgs().contains("--lang") || event.getArgs().length() > 1024) {
            MessageHelper.syntaxError(event, this, "lower than 1024 characters");
            return;
        }
        /*
        Variables :
        args[0] = Text to translate
        args[1].split("\\s+")[0] and language1 = Language of the text
        args[1].split("\\s+")[1] and language2 = Language where the text should be translated
         */
        String[] args = event.getArgs().split(" --lang ");
        LingvaLanguage language1 = LingvaLanguage.AUTO;
        LingvaLanguage language2 = LingvaLanguage.EN;
        for (LingvaLanguage lingvaLanguage : LingvaLanguage.values()) {
            if (lingvaLanguage.name().equalsIgnoreCase(args[1].split("\\s+")[0])) language1 = lingvaLanguage;
            if (lingvaLanguage.name().equalsIgnoreCase(args[1].split("\\s+")[1])) language2 = lingvaLanguage;
        }
        try {
            EmbedBuilder successEmbed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getEffectiveAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage(event, "success.translate.success"));
            if (args[0].length() > 1024) {
                int charactersCount = 0;
                List<String> list = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : args[0].toCharArray()) {
                    stringBuilder.append(c);
                    charactersCount++;
                    if (charactersCount == 1024 || charactersCount == ((args[0].length() / 1024D) - Math.floor(args[0].length() / 1024D)) * 1024) {
                        list.add(stringBuilder.toString());
                        charactersCount = 0;
                        stringBuilder.setLength(0);
                    }
                }
                System.out.println(list);
                for (int i = 0; i < Math.ceil(args[0].length() / 1024D); i++)
                    successEmbed.addField(i == 0 ? MessageHelper.translateMessage(event, "success.translate.text") : "", list.get(i), true);
            } else
                successEmbed.addField(MessageHelper.translateMessage(event, "success.translate.text"), args[0], true);
            //System.out.println(String.format("https://lingva.ml/api/v1/%s/%s/%s", language1.name().toLowerCase(Locale.ROOT), language2.name().toLowerCase(Locale.ROOT), URLEncoder.encode(args[0], StandardCharsets.UTF_8)));
            String translatedArgs = SimpleBot.gson.fromJson(RequestHelper.getResponseAsString(RequestHelper.sendRequest(String.format("https://lingva.ml/api/v1/%s/%s/%s", language1.name().toLowerCase(Locale.ROOT), language2.name().toLowerCase(Locale.ROOT), URLEncoder.encode(args[0], StandardCharsets.UTF_8)))), JsonObject.class).get("translation").getAsString();
            if (translatedArgs.length() > 1024) {
                int charactersCount = 0;
                List<String> list = new ArrayList<>();
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : translatedArgs.toCharArray()) {
                    stringBuilder.append(c);
                    charactersCount++;
                    if (charactersCount == 1024 || charactersCount == ((translatedArgs.length() / 1024D) - Math.floor(translatedArgs.length() / 1024D)) * 1024) {
                        list.add(stringBuilder.toString());
                        charactersCount = 0;
                        stringBuilder.setLength(0);
                    }
                }
                for (String str : list) System.out.println(str);
                for (int i = 0; i < Math.ceil(translatedArgs.length() / 1024D); i++)
                    successEmbed.addField(i == 0 ? MessageHelper.translateMessage(event, "success.translate.translatedText") : "", list.get(i), true);
            } else
                successEmbed.addField(MessageHelper.translateMessage(event, "success.translate.translatedText"), translatedArgs, true);
            successEmbed.addField(MessageHelper.translateMessage(event, "success.translate.isoCodeText"), language1.name().toLowerCase(Locale.ROOT), true)
                    .addField(MessageHelper.translateMessage(event, "success.translate.languageText"), MessageHelper.translateMessage(event, language1.languageName), true)
                    .addBlankField(true)
                    .addField(MessageHelper.translateMessage(event, "success.translate.isoCodeTranslation"), language2.name().toLowerCase(Locale.ROOT), true)
                    .addField(MessageHelper.translateMessage(event, "success.translate.languageTranslation"), MessageHelper.translateMessage(event, language2.languageName), true)
                    .addBlankField(true);
            event.reply(new MessageBuilder(successEmbed.build()).build());
        } catch (IOException exception) {
            MessageHelper.sendError(exception, event, this);
        }
    }

    private enum LingvaLanguage {

        AUTO("text.translate.detect"),
        AF("text.translate.afrikaans"),
        SQ("text.translate.albanian"),
        AM("text.translate.amharic"),
        AR("text.translate.arabic"),
        HY("text.translate.armenian"),
        AZ("text.translate.azerbaijani"),
        EU("text.translate.basque"),
        BE("text.translate.belarusian"),
        BN("text.translate.bengali"),
        BS("text.translate.bosnian"),
        BG("text.translate.bulgarian"),
        CA("text.translate.catalan"),
        CEB("text.translate.cebuano"),
        NY("text.translate.chichewa"),
        ZH("text.translate.chinese"),
        ZH_HANT("text.translate.chineseTraditional"),
        CO("text.translate.corsican"),
        HR("text.translate.croatian"),
        CS("text.translate.czech"),
        DA("text.translate.danish"),
        NL("text.translate.dutch"),
        EN("text.translate.english"),
        EO("text.translate.esperanto"),
        ET("text.translate.estonian"),
        TL("text.translate.filipino"),
        FI("text.translate.finnish"),
        FR("text.translate.french"),
        FY("text.translate.frisian"),
        GL("text.translate.galician"),
        KA("text.translate.georgian"),
        DE("text.translate.german"),
        EL("text.translate.greek"),
        GU("text.translate.gujarati"),
        HT("text.translate.haitianCreole"),
        HA("text.translate.hausa"),
        HAW("text.translate.hawaiian"),
        IW("text.translate.hebrew"),
        HI("text.translate.hindi"),
        HMN("text.translate.hmong"),
        HU("text.translate.hungarian"),
        IS("text.translate.icelandic"),
        IG("text.translate.igbo"),
        ID("text.translate.indonesian"),
        GA("text.translate.irish"),
        IT("text.translate.italian"),
        JA("text.translate.japanese"),
        JW("text.translate.javanese"),
        KN("text.translate.kannada"),
        KK("text.translate.kazakh"),
        KM("text.translate.khmer"),
        RW("text.translate.kinyarwanda"),
        KO("text.translate.korean"),
        KU("text.translate.kurdishKurmanji"),
        KY("text.translate.kyrgyz"),
        LO("text.translate.lao"),
        LA("text.translate.latin"),
        LV("text.translate.latvian"),
        LT("text.translate.lithuanian"),
        LB("text.translate.luxembourgish"),
        MK("text.translate.macedonian"),
        MG("text.translate.malagasy"),
        MS("text.translate.malay"),
        ML("text.translate.malayalam"),
        MT("text.translate.maltese"),
        MI("text.translate.maori"),
        MR("text.translate.marathi"),
        MN("text.translate.mongolian"),
        MY("text.translate.myanmarBurmese"),
        NE("text.translate.nepali"),
        NO("text.translate.norwegian"),
        OR("text.translate.odiaOriya"),
        PS("text.translate.pashto"),
        FA("text.translate.persian"),
        PL("text.translate.polish"),
        PT("text.translate.portuguese"),
        PA("text.translate.punjabi"),
        RO("text.translate.romanian"),
        RU("text.translate.russian"),
        SM("text.translate.samoan"),
        GD("text.translate.scotsGaelic"),
        SR("text.translate.serbian"),
        ST("text.translate.sesotho"),
        SN("text.translate.shona"),
        SD("text.translate.sindhi"),
        SI("text.translate.sinhala"),
        SK("text.translate.slovak"),
        SL("text.translate.slovenian"),
        SO("text.translate.somali"),
        ES("text.translate.spanish"),
        SU("text.translate.sundanese"),
        SW("text.translate.swahili"),
        SV("text.translate.swedish"),
        TG("text.translate.tajik"),
        TA("text.translate.tamil"),
        TT("text.translate.tatar"),
        TE("text.translate.telugu"),
        TH("text.translate.thai"),
        TR("text.translate.turkish"),
        TK("text.translate.turkmen"),
        UK("text.translate.ukrainian"),
        UR("text.translate.urdu"),
        UG("text.translate.uyghur"),
        UZ("text.translate.uzbek"),
        VI("text.translate.vietnamese"),
        CY("text.translate.welsh"),
        XH("text.translate.xhosa"),
        YI("text.translate.yiddish"),
        YO("text.translate.yoruba"),
        ZU("text.translate.zulu");

        private final String languageName;

        LingvaLanguage(String languageName) {
            this.languageName = languageName;
        }
    }
}
