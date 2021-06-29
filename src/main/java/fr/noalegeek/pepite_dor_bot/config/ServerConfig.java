package fr.noalegeek.pepite_dor_bot.config;

import java.util.List;
import java.util.Map;

public class ServerConfig {

    public final Map<String, Boolean> withoutMutedRole;
    public final Map<String, String> guildJoinRole,channelMemberJoin,channelMemberRemove, mutedRole;
    public final Map<String, List<String>> prohibitWords;


    public ServerConfig(Map<String, String> guildJoinRole, Map<String, String> channelMemberJoin, Map<String, String> channelMemberRemove, Map<String, Boolean> withoutMutedRole, Map<String, String> mutedRole, Map<String, List<String>> prohibitWords) {
        this.guildJoinRole = guildJoinRole;
        this.channelMemberJoin = channelMemberJoin;
        this.channelMemberRemove = channelMemberRemove;
        this.withoutMutedRole = withoutMutedRole;
        this.mutedRole = mutedRole;
        this.prohibitWords = prohibitWords;
    }
}
