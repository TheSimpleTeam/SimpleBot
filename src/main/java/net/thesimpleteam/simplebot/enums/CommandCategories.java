package net.thesimpleteam.simplebot.enums;

import com.jagrosh.jdautilities.command.Command;

public enum CommandCategories {

    FUN(new Command.Category("category.fun")),
    STAFF(new Command.Category("category.staff")),
    INFO(new Command.Category("category.infos")),
    MISC(new Command.Category("category.misc")),
    UTILITY(new Command.Category("category.utility")),
    //Add other categories
    ;

    public final Command.Category category;

    CommandCategories(Command.Category category) {
        this.category = category;
    }
}
