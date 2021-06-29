package fr.noalegeek.pepite_dor_bot.config;

import java.util.List;
import java.util.Map;

public class ServerConfig {

    public final Map<String, String> guildJoinRole,channelMemberJoin,channelMemberRemove;
    public final Map<String, List<String>> prohibitWords;

    public ServerConfig(Map<String, String> guildJoinRole, Map<String, String> channelMemberJoin, Map<String, String> channelMemberRemove, Map<String, List<String>> prohibitWords) {
        this.guildJoinRole = guildJoinRole;
        this.channelMemberJoin = channelMemberJoin;
        this.channelMemberRemove = channelMemberRemove;
        this.prohibitWords = prohibitWords;
    }
}
