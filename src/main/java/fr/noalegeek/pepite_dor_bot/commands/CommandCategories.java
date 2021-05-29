package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;

public enum CommandCategories {

    FUN(new Command.Category("Fun")),
    STAFF(new Command.Category("Staff")),
    WTF(new Command.Category("??????????")),
    IDK(new Command.Category("Idk srry")),
    //Add other categories
    ;

    public final Command.Category category;

    CommandCategories(Command.Category category) {
        this.category = category;
    }
}
