package fr.noalegeek.pepite_dor_bot.config;

public record Infos(String token, String prefix, String defaultRoleID, String botGithubToken, String[] activities, long timeBetweenStatusChange, long autoSaveDelay) {}