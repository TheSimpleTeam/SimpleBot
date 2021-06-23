package fr.noalegeek.pepite_dor_bot.config;

import java.util.Map;

public class ServerConfig {

    public final Map<String, String> guildJoinRole;
    public final Map<String, String> channelMemberJoin;
    public final Map<String, String> channelMemberRemove;
    public final Map<String, Boolean> withoutMutedRole;
    public final Map<String, String> mutedRole;


    public ServerConfig(Map<String, String> guildJoinRole, Map<String, String> channelMemberJoin, Map<String, String> channelMemberRemove, Map<String, Boolean> withoutMutedRole, Map<String, String> mutedRole) {
        this.guildJoinRole = guildJoinRole;
        this.channelMemberJoin = channelMemberJoin;
        this.channelMemberRemove = channelMemberRemove;
        this.withoutMutedRole = withoutMutedRole;
        this.mutedRole = mutedRole;
    }
}
