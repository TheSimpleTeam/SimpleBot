package fr.noalegeek.pepite_dor_bot.enums;

import fr.noalegeek.pepite_dor_bot.commands.MathsCommand;

public enum Date {
    SECONDS(MathsCommand.Unit.SECOND.symbol),
    MINUTES(MathsCommand.Unit.MINUTE.symbol),
    HOURS(MathsCommand.Unit.HOUR.symbol),
    DAYS(MathsCommand.Unit.DAY.symbol),
    WEEKS(MathsCommand.Unit.WEEK.symbol),
    MONTHS(MathsCommand.Unit.MONTH.symbol),
    YEARS(MathsCommand.Unit.YEAR.symbol);

    private final String symbol;

    Date(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
