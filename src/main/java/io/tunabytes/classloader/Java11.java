/*
 * MIT License
 *
 * Copyright (c) 2022 minemobs
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

package io.tunabytes.classloader;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

public final class Java11 implements ClassDefiner {

    private final Method defineClass = getDefineClassMethod();

    private Method getDefineClassMethod() {
        try {
            return getDeclaredMethod(ClassLoader.class, "defineClass",
                    String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("cannot initialize", e);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getDeclaredMethod = Class.forName("io.tunabytes.classloader.SecurityActions")
                .getDeclaredMethod("getDeclaredMethod", Class.class, String.class, Class[].class);
        getDeclaredMethod.setAccessible(true);
        return (Method) getDeclaredMethod.invoke(null, clazz, name, parameterTypes);
    }

    @Override
    public Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor,
                                ClassLoader loader, ProtectionDomain protectionDomain)
            throws ClassFormatError {
        if (neighbor != null)
            return toClass(neighbor, b);
        else {
            // Lookup#defineClass() is not available.  So fallback to invoking defineClass on
            // ClassLoader, which causes a warning message.

            try {
                setAccessible(defineClass, true);
                return (Class<?>) defineClass.invoke(loader, new Object[]{
                        name, b, off, len, protectionDomain
                });
            } catch (Throwable e) {
                sneakyThrow(e);
                return null;
            }
        }
    }

    private void setAccessible(final AccessibleObject ao, final boolean accessible) throws InvocationTargetException, IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException {
        Method setAccessible = Class.forName("io.tunabytes.classloader.SecurityActions").getDeclaredMethod("setAccessible", AccessibleObject.class, boolean.class);
        setAccessible.setAccessible(true);
        setAccessible.invoke(null, ao, accessible);
    }

    private static RuntimeException sneakyThrow(Throwable t) {
        if (t == null) throw new NullPointerException("t");
        return sneakyThrow0(t);
    }

    private static <T extends Throwable> T sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

    /**
     * Loads a class file by {@code java.lang.invoke.MethodHandles.Lookup}.
     * It is obtained by using {@code neighbor}.
     *
     * @param neighbor a class belonging to the same package that the loaded
     *                 class belogns to.
     * @param bcode    the bytecode.
     * @since 3.24
     */
    public static Class<?> toClass(Class<?> neighbor, byte[] bcode) {
        try {
            TunaClassDefiner.class.getModule().addReads(neighbor.getModule());
            Lookup lookup = MethodHandles.lookup();
            Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
            return prvlookup.defineClass(bcode);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + ": " + neighbor.getName()
                    + " has no permission to define the class");
        }
    }

    @Override public boolean requiresNeighbor() {
        return true;
    }
}
