package fr.noalegeek.simplebot.commands;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fr.noalegeek.simplebot.SimpleBot;
import fr.noalegeek.simplebot.enums.CommandCategories;
import fr.noalegeek.simplebot.enums.DiscordFormatUtils;
import fr.noalegeek.simplebot.utils.Eval;
import fr.noalegeek.simplebot.utils.MessageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import org.python.util.PythonInterpreter;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EvaluateCommand extends Command {

    private final V8Runtime engine;
    private final PythonInterpreter pyInterpreter;
    private final StringWriter writer;

    public EvaluateCommand() {
        this.name = "evaluate";
        this.help = "help.eval";
        this.aliases = new String[]{"eval", "e"};
        this.arguments = "arguments.eval";
        this.engine = SimpleBot.eval.getV8Runtime();
        this.pyInterpreter = SimpleBot.eval.getPyInterpreter();
        this.writer = SimpleBot.eval.getWriter();
        this.category = CommandCategories.MISC.category;
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        Eval.Languages language = Eval.Languages.JS;
        if(event.getArgs().startsWith(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format)) {
            if(Eval.Languages.isLanguageAvailable(args.split("\n")[0].replaceAll(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format, "")).isPresent()) {
                language = Eval.Languages.isLanguageAvailable(args.split("\n")[0].replaceAll(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format, "")).get();
            }
            List<String> list = new LinkedList<>(Arrays.asList(args.split("\n")));
            list.remove(0);
            list.remove(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format);
            args = String.join("\n", list);
        }
        eval(language, args, event);
    }

    private void eval(Eval.Languages language, String arg, CommandEvent event) {
        if(language == Eval.Languages.PY) {
            evalPY(arg, event);
        } else {
            evalJS(arg, event);
        }
    }

    private void evalJS(String args, CommandEvent event) {
        try {
            addV8Module(event, "event");
            addV8Module(event.getMessage(), "message");
            addV8Module(event.getChannel(), "channel");
            addV8Module(args, "args");
            addV8Module(event.getJDA(), "jda");
            addV8Module(event.getClient(), "client");
            addV8Module(event.getGuild(), "guild");
            addV8Module(event.getMember(), "member");
            addV8Module(SimpleBot.class);
            addV8Module(MessageBuilder.class);
            addV8Module(EmbedBuilder.class);
            addV8Module(MessageHelper.class);
            addV8Module(net.dv8tion.jda.api.entities.TextChannel.class);
            Object eval = engine.getExecutor(args).executeObject();
            event.reply(eval == null ? MessageHelper.translateMessage(event, "success.eval") : new StringBuilder().append(MessageHelper.translateMessage(event, "success.eval")).append("\n").append(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format).append("\n").append(eval).append("\n").append(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format).toString());
            engine.getGlobalObject().forEach(value -> engine.getGlobalObject().delete(value));
        } catch (JavetException e) {
            if(e.getMessage().startsWith("ReferenceError") || e.getMessage().startsWith("TypeError")) {
                event.replyError(e.getMessage());
                return;
            }
            MessageHelper.sendError(e, event, this);
        }
    }

    private void evalPY(String args, CommandEvent event) {
        pyInterpreter.set("event", event);
        pyInterpreter.set("message", event.getMessage());
        pyInterpreter.set("channel", event.getChannel());
        pyInterpreter.set("args", args);
        pyInterpreter.set("jda", event.getJDA());
        pyInterpreter.set("client", event.getClient());
        pyInterpreter.set("guild", event.getGuild());
        pyInterpreter.set("member", event.getMember());
        pyInterpreter.exec(args);
        String eval = writer.toString();
        event.reply(eval == null ? MessageHelper.translateMessage(event, "success.eval") : new StringBuilder().append(MessageHelper.translateMessage(event, "success.eval")).append("\n").append(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format).append("\n").append(eval).append("\n").append(DiscordFormatUtils.MULTILINE_CODE_BLOCK.format).toString());
        pyInterpreter.cleanup();
        writer.getBuffer().setLength(0);
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
