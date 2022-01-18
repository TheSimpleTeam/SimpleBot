package fr.noalegeek.pepite_dor_bot.enums;

import fr.noalegeek.pepite_dor_bot.commands.MathsCommand;

public enum Date {
    SECONDS(MathsCommand.Unit.s.name()),
    MINUTES(MathsCommand.Unit.min.name()),
    HOURS(MathsCommand.Unit.h.name()),
    DAYS(MathsCommand.Unit.d.name()),
    WEEKS(MathsCommand.Unit.w.name()),
    MONTHS(MathsCommand.Unit.M.name()),
    YEARS(MathsCommand.Unit.y.name());

    private final String symbol;

    Date(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
