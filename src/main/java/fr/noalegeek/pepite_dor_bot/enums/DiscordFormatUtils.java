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

package fr.noalegeek.pepite_dor_bot.enums;

public enum DiscordFormatUtils {

    ITALIC("_"),
    UNDERLINE_ITALIC("__*"),
    BOLD("**"),
    UNDERLINE_BOLD("__**"),
    BOLD_ITALIC("***"),
    UNDERLINE_BOLD_ITALIC("__***"),
    UNDERLINE("__"),
    STRIKETHROUGH("~~"),
    QUOTE(">", false),
    CODE_BLOCK("`"),
    MULTILINE_CODE_BLOCK("```");

    public final String format;
    private final boolean reverse;

    DiscordFormatUtils(String s) {
        this.format = s;
        this.reverse = true;
    }

    DiscordFormatUtils(String s, boolean reverse) {
        this.format = s;
        this.reverse = reverse;
    }

    public static String formatText(String text, DiscordFormatUtils _format) {
        String t = _format.format + text;
        return _format.reverse ? t : t + new StringBuilder(_format.format).reverse();
    }

    /**
     * This function should be used only if you want to format your text with {@link DiscordFormatUtils#MULTILINE_CODE_BLOCK}
     * @param text Your text
     * @param language The programming language
     * @return The formatted text
     * @see <a href="https://highlightjs.org/static/demo/">List of programming languages supported by the syntax highlighting</a>
     */
    public static String formatMultilineCodeBlock(String text, String language) {
        return MULTILINE_CODE_BLOCK.format + language + "\n" + text + MULTILINE_CODE_BLOCK.format;
    }
    
}
