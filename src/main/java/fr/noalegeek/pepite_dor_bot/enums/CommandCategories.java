package fr.noalegeek.pepite_dor_bot.enums;

import com.jagrosh.jdautilities.command.Command;

public enum CommandCategories {

    FUN(new Command.Category("Fun")),
    STAFF(new Command.Category("Staff")),
    INFO(new Command.Category("Informations")),
    MISC(new Command.Category("Divers")),
    CONFIG(new Command.Category("Configuration")),
    NONE(new Command.Category("No category")),
    //Add other categories
    ;

    public final Command.Category category;

    CommandCategories(Command.Category category) {
        this.category = category;
    }
}
