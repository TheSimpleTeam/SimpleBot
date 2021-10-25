package fr.noalegeek.pepite_dor_bot.commands;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.pepite_dor_bot.Main;
import fr.noalegeek.pepite_dor_bot.utils.DiscordFormatUtils;
import fr.noalegeek.pepite_dor_bot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

public class EvalCommand extends Command {

    private final V8Runtime engine;

    public EvalCommand() {
        this.name = "eval";
        this.ownerCommand = true;
        this.hidden = true;
        this.guildOnly = true;
        this.engine = Main.eval.getV8Runtime();
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs().replaceAll("([^(]+?)\\s*->", "function($1)");
        /*try {
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
        }*/
        try {
            /*IV8Module _event = engine.toV8Value(event);
            _event.setResourceName("event");
            engine.addV8Module(_event);*/
            addV8Module(event, "event");
            addV8Module(event.getMessage(), "message");
            addV8Module(event.getChannel(), "channel");
            addV8Module(args, "args");
            addV8Module(event.getJDA(), "api");
            addV8Module(event.getClient(), "client");
            addV8Module(event.getGuild(), "guild");
            addV8Module(event.getMember(), "member");
            addV8Module(Main.class);
            addV8Module(MessageBuilder.class);
            addV8Module(EmbedBuilder.class);
            addV8Module(MessageHelper.class);
            Object eval = engine.getExecutor(args).executeObject();
            if(eval == null) {
                event.reply("Evaluated Successfully");
            } else {
                event.reply("Evaluated Successfully:\n" + DiscordFormatUtils.MULTILINE_CODE_BLOCK.format + "\n" + eval + "\n" + DiscordFormatUtils.MULTILINE_CODE_BLOCK.format);
            }
            engine.getGlobalObject().forEach(value -> engine.getGlobalObject().delete(value));
        } catch (JavetException e) {
            if(e.getMessage().startsWith("ReferenceError")) {
                event.replyError(e.getMessage());
                return;
            }
            MessageHelper.sendError(e, event);
        }
    }

    private void addV8Module(Object o, String name) throws JavetException {
        engine.getGlobalObject().set(name, o);
    }

    private void addV8Module(Class<?> clazz, String name) throws JavetException {
        engine.getGlobalObject().set(name, clazz);
    }

    private void addV8Module(Class<?> clazz) throws JavetException {
        addV8Module(clazz, clazz.getSimpleName());
    }
}
