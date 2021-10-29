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

package fr.noalegeek.pepite_dor_bot.utils;

import java.util.HashMap;
import java.util.Map;

public class UnicodeCharacters {

    public final static String crossMarkEmoji = "\u274C";
    public final static String electricLightBulbEmoji = "\uD83D\uDCA1";
    public final static String whiteHeavyCheckMarkEmoji = "\u2705";
    public final static String informationSourceEmoji = "\u2139";

    public final static char plusExponent = '\u207A';
    public final static char minusExponent = '\u207B';
    public final static char leftParenthesisExponent = '\u207D';
    public final static char rightParenthesisExponent = '\u207E';

    public static Map<Character, Character> getNumeralExponents() {
        final Map<Character, Character> map = new HashMap<>();
        map.put('\u2070', '0');
        map.put('\u00B9', '1');
        map.put('\u00B2', '2');
        map.put('\u00B3', '3');
        map.put('\u2074', '4');
        map.put('\u2075', '5');
        map.put('\u2076', '6');
        map.put('\u2077', '7');
        map.put('\u2078', '8');
        map.put('\u2079', '9');
        return map;
    }

    public static boolean isNumeralExponent(char c){
        return getNumeralExponents().containsKey(c);
    }
}
