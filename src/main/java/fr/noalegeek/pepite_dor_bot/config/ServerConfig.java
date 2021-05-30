package fr.noalegeek.pepite_dor_bot.config;

import java.util.Map;

public class ServerConfig {

    public final Map<String, String> guildJoinRole;

    public ServerConfig(Map<String, String> guildJoinRole) {
        this.guildJoinRole = guildJoinRole;
    }
}
