package fr.noalegeek.pepite_dor_bot.config;

import java.util.List;
import java.util.Map;

public record ServerConfig(Map<String, String> guildJoinRole, Map<String, String> channelMemberJoin, Map<String, String> channelMemberLeave, Map<String, Boolean> withoutMutedRole,
                           Map<String, String> mutedRole, Map<String, List<String>> prohibitWords, Map<String, String> language, Map<String, String> prefix) {}
