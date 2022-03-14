/*
 * MIT License
 *
 * Copyright (c) 2021 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.thesimpleteam.simplebot.utils;

public class UnicodeCharacters {

    private UnicodeCharacters() {}

    public static final String CROSS_MARK_EMOJI = "\u274C";
    public static final String crossMarkEmoji = CROSS_MARK_EMOJI;
    public static final String ELECTRIC_LIGHT_BULB_EMOJI = "\uD83D\uDCA1";
    public static final String WHITE_HEAVY_CHECK_MARK_EMOJI = "\u2705";
    public static final String INFORMATION_SOURCE_EMOJI = "\u2139";
    public static final String WARNING_SIGN_EMOJI = "\u26A0";
    public static final String HEAVY_PLUS_SIGN = "\u2795";
    public static final String HEAVY_MINUS_SIGN = "\u2796";


    public static char[] getAllExponentsCharacters() {
        return new char[]{'\u2070', '\u00B9', '\u00B2', '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078', '\u2079', '\u207A', '\u207B', '\u207D', '\u207E'};
    }
}
