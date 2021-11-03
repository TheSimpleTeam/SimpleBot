package fr.noalegeek.pepite_dor_bot.enums;

public enum Date {
    SECONDS("s"),
    MINUTES("min"),
    HOURS("h"),
    DAYS("d"),
    WEEKS("w"),
    MONTHS("m"),
    YEARS("y");

    private final String symbol;

    Date(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
