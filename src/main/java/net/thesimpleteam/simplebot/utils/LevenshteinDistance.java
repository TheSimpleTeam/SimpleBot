package net.thesimpleteam.simplebot.utils;

/**
 * @author <a href="https://stackoverflow.com/a/26440076/14628115">Mohsen Abasi</a>
 */
public class LevenshteinDistance {

    public static int computeEditDistance(String s1, String s2) {
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.toLowerCase().charAt(i - 1) != s2.toLowerCase().charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.toLowerCase().length()] = lastValue;
        }
        return costs[s2.toLowerCase().length()];
    }

    public static double getDistance(String s1, String s2) {
        double similarityOfStrings;
        if (s1.length() < s2.length()) { // s1 should always be bigger
            String swap = s1;
            s1 = s2;
            s2 = swap;
        }
        if (s1.length() == 0)
            similarityOfStrings = 1.0; /* both strings are zero length */
        else
            similarityOfStrings = (s1.length() - computeEditDistance(s1, s2)) / (double) s1.length();
        return similarityOfStrings;
    }
}