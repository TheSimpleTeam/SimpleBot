package fr.noalegeek.pepite_dor_bot.config;

public record Infos(String botName, String token, String prefix, String defaultRoleID, String githubToken, String[] activities, long timeBetweenStatusChange, long autoSaveDelay) {}