package fr.noalegeek.pepite_dor_bot.utils;

import java.util.regex.Pattern;

/**
 * This class contains some useful precompiled regex patterns.
 * @author <a href="https://github.com/Javacord/Javacord/blob/master/javacord-api/src/main/java/org/javacord/api/util/DiscordRegexPattern.java">Javacord</a>
 */
public class RegexPattern {

    /**
     * A pattern which checks for mentioned users (e.g. {@code <@1234567890>}).
     */
    public static final Pattern USER_MENTION =
            Pattern.compile("""
                    (?x)                  # enable comment mode\s
                    (?<!                # negative lookbehind\s
                                        # (do not have uneven amount of backslashes before)\s
                        (?<!\\\\)       # negative lookbehind (do not have one backslash before)\s
                        (?:\\\\{2}+)    # exactly two backslashes\s
                        {0,1000000000}+ # 0 to 1_000_000_000 times\s
                                        # (basically *, but a lookbehind has to have a maximum length)\s
                        \\\\            # the one escaping backslash\s
                    )                   #\s
                    <@!?+               # '<@' or '<@!'\s
                    (?<id>[0-9]++)      # the user id as named group\s
                    >                   # '>'""");

    /**
     * A pattern which checks for mentioned roles (e.g. {@code <@&1234567890>}).
     */
    public static final Pattern ROLE_MENTION =
            Pattern.compile("""
                    (?x)                  # enable comment mode\s
                    (?<!                # negative lookbehind\s
                                        # (do not have uneven amount of backslashes before)\s
                        (?<!\\\\)       # negative lookbehind (do not have one backslash before)\s
                        (?:\\\\{2}+)    # exactly two backslashes\s
                        {0,1000000000}+ # 0 to 1_000_000_000 times\s
                                        # (basically *, but a lookbehind has to have a maximum length)\s
                        \\\\            # the one escaping backslash\s
                    )                   #\s
                    <@&                 # '<@&'\s
                    (?<id>[0-9]++)      # the role id as named group\s
                    >                   # '>'""");

    /**
     * A pattern which checks for mentioned channels (e.g. {@code <#1234567890>}).
     */
    public static final Pattern CHANNEL_MENTION =
            Pattern.compile("(?x)                  # enable comment mode \n"
                    + "(?<!                # negative lookbehind \n"
                    + "                    # (do not have uneven amount of backslashes before) \n"
                    + "    (?<!\\\\)       # negative lookbehind (do not have one backslash before) \n"
                    + "    (?:\\\\{2}+)    # exactly two backslashes \n"
                    + "    {0,1000000000}+ # 0 to 1_000_000_000 times \n"
                    + "                    # (basically *, but a lookbehind has to have a maximum length) \n"
                    + "    \\\\            # the one escaping backslash \n"
                    + ")                   # \n"
                    + "(?-x:<#)            # '<#' with disabled comment mode due to the # \n"
                    + "(?<id>[0-9]++)      # the channel id as named group \n"
                    + ">                   # '>'");

    /**
     * A pattern which checks for custom emojis (e.g. {@code <:my_emoji:1234567890>}).
     */
    public static final Pattern CUSTOM_EMOJI =
            Pattern.compile("""
                    (?x)                  # enable comment mode\s
                    (?<!                # negative lookbehind\s
                                        # (do not have uneven amount of backslashes before)\s
                        (?<!\\\\)       # negative lookbehind (do not have one backslash before)\s
                        (?:\\\\{2}+)    # exactly two backslashes\s
                        {0,1000000000}+ # 0 to 1_000_000_000 times\s
                                        # (basically *, but a lookbehind has to have a maximum length)\s
                        \\\\            # the one escaping backslash\s
                    )                   #\s
                    <a?+:               # '<:' or '<a:'\s
                    (?<name>\\w++)      # the custom emoji name as named group\s
                    :                   # ':'\s
                    (?<id>[0-9]++)      # the custom emoji id as named group\s
                    >                   # '>'\s
                    """);

    /**
     * A pattern which checks for message links (e.g. {@code https://discord.com/channels/@me/1234/5678}
     */
    public static final Pattern MESSAGE_LINK =
            Pattern.compile("""
                    (?x)                               # enable comment mode\s
                    (?i)                             # ignore case\s
                    (?:https?+://)?+                 # 'https://' or 'http://' or ''\s
                    (?:(?:canary|ptb)\\.)?+          # 'canary.' or 'ptb.'
                    discord(?:app)?+\\.com/channels/ # 'discord(app).com/channels/'\s
                    (?:(?<server>[0-9]++)|@me)       # '@me' or the server id as named group\s
                    /                                # '/'\s
                    (?<channel>[0-9]++)              # the textchannel id as named group\s
                    /                                # '/'\s
                    (?<message>[0-9]++)              # the message id as named group\s
                    """);

    /**
     * A pattern which checks for webhook urls (e.g. {@code https://discord.com/api/webhooks/1234/abcd}
     */
    public static final Pattern WEBHOOK_URL =
            Pattern.compile("""
                    (?x)                                   # enable comment mode\s
                    (?i)                                 # ignore case\s
                    (?:https?+://)?+                     # 'https://' or 'http://' or ''\s
                    (?:(?:canary|ptb)\\.)?+              # 'canary.' or 'ptb.'
                    discord(?:app)?+\\.com/api/webhooks/ # 'discord(app).com/api/webhooks'\s
                    (?<id>[0-9]++)                       # the webhook id as named group\s
                    /                                    # '/'\s
                    (?<token>[^/\\s]++)                  # the webhook token as named group\s
                    """);

    /**
     * A pattern to match snowflakes.
     */
    public static final Pattern SNOWFLAKE =
            Pattern.compile("(?<id>[0-9]{15,25})");

    public static final Pattern YOUTUBE_URL =
            Pattern.compile("^((?:https?:)?\\/\\/)?((?:www|m)\\.)?((?:youtube\\.com|youtu.be|music.youtube.com))(\\/(?:[\\w\\-]+\\?v=|embed\\/|v\\/)?)([\\w\\-]+)(\\S+)?$");

    private RegexPattern() {}
}