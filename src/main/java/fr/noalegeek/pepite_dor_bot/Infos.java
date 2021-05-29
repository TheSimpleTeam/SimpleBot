package fr.noalegeek.pepite_dor_bot;

public class Infos {
    public final String token, prefix, defaultRoleID;
    public final String[] activities;

    public Infos(String token, String prefix, String defaultRoleID, String[] activities) {
        this.token = token;
        this.prefix = prefix;
        this.defaultRoleID = defaultRoleID;
        this.activities = activities;
    }
}
