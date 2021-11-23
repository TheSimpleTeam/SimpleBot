package fr.noalegeek.pepite_dor_bot.enums;

import com.jagrosh.jdautilities.command.Command;

public enum CommandCategories {

    FUN(new Command.Category("category.fun")),
    STAFF(new Command.Category("category.mod")),
    INFO(new Command.Category("category.infos")),
    MISC(new Command.Category("category.misc")),
    CONFIG(new Command.Category("category.config")),
    MUSIC(new Command.Category("category.music")),
    NONE(new Command.Category("category.noCategory")),
    //Add other categories
    ;

    public final Command.Category category;

    CommandCategories(Command.Category category) {
        this.category = category;
    }
}
