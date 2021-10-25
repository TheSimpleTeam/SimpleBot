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

package fr.noalegeek.pepite_dor_bot.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.StringWriter;

public class Eval {

    private final ScriptEngine engine;
    private V8Runtime v8Runtime;
    private final JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
    private final StringWriter writer;

    public Eval() {
        System.setProperty("polyglot.js.nashorn-compat", "true");
        this.engine = new ScriptEngineManager().getEngineByName("graal.js");
        ScriptContext context = engine.getContext();
        this.writer = new StringWriter();
        context.setWriter(writer);
        try {
            this.v8Runtime = V8Host.getNodeInstance().createV8Runtime();
            this.v8Runtime.setConverter(javetProxyConverter);
        } catch (JavetException e) {
            e.printStackTrace();
        }
    }

    /**
     * @deprecated {@link Eval#getV8Runtime()}
     * @return GraalJS's Script Engine
     */
    @Deprecated
    public ScriptEngine getEngine() {
        return engine;
    }

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    public JavetProxyConverter getJavetProxyConverter() {
        return javetProxyConverter;
    }

    public StringWriter getWriter() {
        return writer;
    }
}
