package fr.noalegeek.pepite_dor_bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;

import javax.script.ScriptEngine;
import java.io.StringWriter;

public class EvalCommand extends Command {

    private final ScriptEngine engine;
    private final StringWriter writer;

    public EvalCommand() {
        this.name = "eval";
        this.ownerCommand = true;
        this.hidden = true;
        this.guildOnly = true;
        this.engine = Main.eval.getEngine();
        this.writer = Main.eval.getWriter();
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs().replaceAll("([^(]+?)\\s*->", "function($1)");
        try {
            engine.put("event", event);
            engine.put("message", event.getMessage());
            engine.put("channel", event.getChannel());
            engine.put("args", event.getArgs().split("\\s+"));
            engine.put("api", event.getJDA());
            engine.put("guild", event.getGuild());
            engine.put("member", event.getMember());
            engine.eval(args);
            if(writer.toString() == null){
                event.reply("Evaluated Successfully");
            }else{
                event.reply("Evaluated Successfully:\n```\n" + writer + " ```");
            }
        } catch (Exception ex) {
            MessageHelper.sendError(ex, event);
        }
    }
}
