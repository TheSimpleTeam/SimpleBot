package fr.noalegeek.pepite_dor_bot.commands;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.enums.CommandCategories;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import fr.noalegeek.pepite_dor_bot.utils.RequestHelper;
import fr.noalegeek.pepite_dor_bot.utils.UnicodeCharacters;
import net.dv8tion.jda.api.EmbedBuilder;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

public class TranslateCommand extends Command {

    public TranslateCommand() {
        this.name = "translate";
        this.aliases = new String[]{"tr","tra","tran","trans","transl","transla","translat"};
        this.example = "example.translate";
        this.arguments = "arguments.translate";
        this.help = "help.translate";
        this.category = CommandCategories.FUN.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!event.getMessage().getContentRaw().contains("--lang")) {
            MessageHelper.syntaxError(event, this, "syntax.translate");
            return;
        }
        /*
        Variables :
        args[0] = Text to translate
        args[1].split("\\s+")[0] = Language of the text
        args[1].split("\\s+")[1] = Language where the text should be translated
         */
        String[] args = event.getArgs().split(" --lang ");
        LingvaLanguage language1 = LingvaLanguage.AUTO;
        LingvaLanguage language2 = LingvaLanguage.EN;
        for(LingvaLanguage lingvaLanguage : LingvaLanguage.values()){
            if(lingvaLanguage.name().equalsIgnoreCase(args[1].split("\\s+")[0])) language1 = lingvaLanguage;
            if(lingvaLanguage.name().equalsIgnoreCase(args[1].split("\\s+")[1])) language2 = lingvaLanguage;
        }
        try {
            EmbedBuilder successEmbed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setFooter(MessageHelper.getTag(event.getAuthor()), event.getAuthor().getAvatarUrl() == null ? event.getAuthor().getDefaultAvatarUrl() : event.getAuthor().getAvatarUrl())
                    .setTimestamp(Instant.now())
                    .setTitle(UnicodeCharacters.whiteHeavyCheckMarkEmoji + " " + MessageHelper.translateMessage("success.translate.success", event))
                    .addField(MessageHelper.translateMessage("success.translate.translatedText", event), Main.gson.fromJson(RequestHelper.sendRequest(String.format("https://lingva.ml/api/v1/%s/%s/%s", language1.name(), language2.name(), URLEncoder.encode(args[0], StandardCharsets.UTF_8))).body().string(), JsonObject.class).get("translation").getAsString(), false)
                    .addField(MessageHelper.translateMessage("success.translate.text", event), args[0], false)
                    .addField()
        } catch (IOException exception) {
            MessageHelper.sendError(exception, event, this);
        }
    }

    private enum LingvaLanguage{

        AUTO("Detect"),
        AF("text.translate.afrikaans"),
        SQ("text.translate.albanian"),
        AM("text.translate.amharic"),
        AR("text.translate.arabic"),
        HY("text.translate.armenian"),
        AZ("Azerbaijani"),
        EU("Basque"),
        BE("Belarusian"),
        BN("Bengali"),
        BS("Bosnian"),
        BG("Bulgarian"),
        CA("Catalan"),
        CEB("Cebuano"),
        NY("Chichewa"),
        ZH("Chinese"),
        ZH_HANT("Chinese Traditional"),
        CO("Corsican"),
        HR("Croatian"),
        CS("Czech"),
        DA("Danish"),
        NL("Dutch"),
        EN("English"),
        EO("Esperanto"),
        ET("Estonian"),
        TL("Filipino"),
        FI("Finnish"),
        FR("French"),
        FY("Frisian"),
        GL("Galician"),
        KA("Georgian"),
        DE("German"),
        EL("Greek"),
        GU("Gujarati"),
        HT("Haitian Creole"),
        HA("Hausa"),
        HAW("Hawaiian"),
        IW("Hebrew"),
        HI("Hindi"),
        HMN("Hmong"),
        HU("Hungarian"),
        IS("Icelandic"),
        IG("Igbo"),
        ID("Indonesian"),
        GA("Irish"),
        IT("Italian"),
        JA("Japanese"),
        JW("Javanese"),
        KN("Kannada"),
        KK("Kazakh"),
        KM("Khmer"),
        RW("Kinyarwanda"),
        KO("Korean"),
        KU("Kurdish Kurmanji"),
        KY("Kyrgyz"),
        LO("Lao"),
        LA("Latin"),
        LV("Latvian"),
        LT("Lithuanian"),
        LB("Luxembourgish"),
        MK("Macedonian"),
        MG("Malagasy"),
        MS("Malay"),
        ML("Malayalam"),
        MT("Maltese"),
        MI("Maori"),
        MR("Marathi"),
        MN("Mongolian"),
        MY("Myanmar Burmese"),
        NE("Nepali"),
        NO("Norwegian"),
        OR("Odia Oriya"),
        PS("Pashto"),
        FA("Persian"),
        PL("Polish"),
        PT("Portuguese"),
        PA("Punjabi"),
        RO("Romanian"),
        RU("Russian"),
        SM("Samoan"),
        GD("Scots Gaelic"),
        SR("Serbian"),
        ST("Sesotho"),
        SN("Shona"),
        SD("Sindhi"),
        SI("Sinhala"),
        SK("Slovak"),
        SL("Slovenian"),
        SO("Somali"),
        ES("Spanish"),
        SU("Sundanese"),
        SW("Swahili"),
        SV("Swedish"),
        TG("Tajik"),
        TA("Tamil"),
        TT("Tatar"),
        TE("Telugu"),
        TH("Thai"),
        TR("Turkish"),
        TK("Turkmen"),
        UK("Ukrainian"),
        UR("Urdu"),
        UG("Uyghur"),
        UZ("Uzbek"),
        VI("Vietnamese"),
        CY("Welsh"),
        XH("Xhosa"),
        YI("Yiddish"),
        YO("Yoruba"),
        ZU("Zulu");

        private final String languageName;

        LingvaLanguage(String languageName){
            this.languageName = languageName;
        }
    }
}
