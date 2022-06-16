package net.thesimpleteam.simplebot.utils;

import java.util.List;

public class UnicodeCharacters {

    private UnicodeCharacters() {}

    public static final String CROSS_MARK_EMOJI = "\u274C";
    public static final String ELECTRIC_LIGHT_BULB_EMOJI = "\uD83D\uDCA1";
    public static final String WHITE_HEAVY_CHECK_MARK_EMOJI = "\u2705";
    public static final String INFORMATION_SOURCE_EMOJI = "\u2139";
    public static final String WARNING_SIGN_EMOJI = "\u26A0";
    public static final String HEAVY_PLUS_SIGN = "\u2795";
    public static final String HEAVY_MINUS_SIGN = "\u2796";

    public static List<Character> getAllExponentCharacters() {
        return List.of('\u2070', '\u00B9', '\u00B2', '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079', '\u207A', '\u207B', '\u207D', '\u207E');
    }
}
