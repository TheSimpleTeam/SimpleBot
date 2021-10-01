package fr.noalegeek.pepite_dor_bot.config;

public class Infos {

    public final String botName, token, prefix, defaultRoleID, githubToken;
    public final String[] activities;
    public final long timeBetweenStatusChange,
            autoSaveDelay;

    public Infos(String botName, String token, String prefix, String defaultRoleID, String githubToken, String[] activities, int timeBetweenStatusChange, long autoSaveDelay) {
        this.botName = botName;
        this.token = token;
        this.prefix = prefix;
        this.defaultRoleID = defaultRoleID;
        this.githubToken = githubToken;
        this.activities = activities;
        this.timeBetweenStatusChange = timeBetweenStatusChange;
        this.autoSaveDelay = autoSaveDelay;
    }
}
