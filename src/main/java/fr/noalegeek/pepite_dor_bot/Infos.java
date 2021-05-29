package fr.noalegeek.pepite_dor_bot;

public class Infos {
    public final String token, prefix;
    public final String[] activities;

    public Infos(String token, String prefix, String[] activities) {
        this.token = token;
        this.prefix = prefix;
        this.activities = activities;
    }
}
