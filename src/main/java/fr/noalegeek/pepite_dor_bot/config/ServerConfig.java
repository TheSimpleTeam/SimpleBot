package fr.noalegeek.pepite_dor_bot.config;

import java.util.Map;

public class ServerConfig {

    public final Map<String, String> guildJoinRole,channelMemberJoin,channelMemberRemove,withoutMutedRole,mutedRole;


    public ServerConfig(Map<String, String> guildJoinRole, Map<String, String> channelMemberJoin, Map<String, String> channelMemberRemove, Map<String, String> withoutMutedRole, Map<String, String> mutedRole) {
        this.guildJoinRole = guildJoinRole;
        this.channelMemberJoin = channelMemberJoin;
        this.channelMemberRemove = channelMemberRemove;
        this.withoutMutedRole = withoutMutedRole;
        this.mutedRole = mutedRole;
    }
}
