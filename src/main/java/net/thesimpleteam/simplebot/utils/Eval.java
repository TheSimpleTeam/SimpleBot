/*
 * MIT License
 *
 * Copyright (c) 2021 minemobs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.thesimpleteam.simplebot.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import org.python.util.PythonInterpreter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

public class Eval {

    public enum Languages {
        JS("javascript"),
        PY("python"),
        ;

        private final String[] aliases;

        Languages(String... aliases) {
            this.aliases = aliases;
        }

        public String[] getAliases() {
            return aliases;
        }

        public static Optional<Languages> isLanguageAvailable(String language) {
            return Arrays.stream(values()).filter(languages -> languages.name().equalsIgnoreCase(language) ||
                    Arrays.stream(languages.getAliases()).anyMatch(s -> s.equalsIgnoreCase(language))).findFirst();
        }
    }

    private final ScriptEngine engine;
    private V8Runtime v8Runtime;
    private final JavetProxyConverter javetProxyConverter;
    private final PythonInterpreter pyInterpreter;
    private final StringWriter writer;

    public Eval() {
        System.setProperty("polyglot.js.nashorn-compat", "true");
        this.engine = new ScriptEngineManager().getEngineByName("graal.js");
        ScriptContext context = engine.getContext();
        this.writer = new StringWriter();
        context.setWriter(writer);
        this.javetProxyConverter = new JavetProxyConverter();
        this.pyInterpreter = new PythonInterpreter();
        this.pyInterpreter.setOut(writer);
        try {
            this.v8Runtime = V8Host.getNodeInstance().createV8Runtime();
            this.v8Runtime.setConverter(javetProxyConverter);
        } catch (JavetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the V8Runtime
     */
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    /**
     * @return the JavetProxyConverter
     */
    public JavetProxyConverter getJavetProxyConverter() {
        return javetProxyConverter;
    }

    /**
     * @return the PythonInterpreter
     */
    public PythonInterpreter getPyInterpreter() {
        return pyInterpreter;
    }

    /**
     * @return the Writer
     */
    public StringWriter getWriter() {
        return writer;
    }
}
