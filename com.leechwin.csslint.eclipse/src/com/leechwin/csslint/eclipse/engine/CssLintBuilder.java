package com.leechwin.csslint.eclipse.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

public class CssLintBuilder {
    private static final String CSSLINT_FILE = "lib/csslint.js";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private ContextFactory contextFactory = new ContextFactory();

    /**
     * Initialize the scope from a csslint.js found in the classpath. Assumes a UTF-8 encoding.
     * @param resource the location of csslint.js on the classpath.
     * @return a configured {@link CssLint}
     * @throws IOException if there are any problems reading the resource.
     */
    public CssLint fromClasspathResource(String resource) throws IOException {
        return fromClasspathResource(resource, UTF8);
    }

    /**
     * Initialize the scope from a csslint.js found in the classpath.
     * @param resource the location of csslint.js on the classpath.
     * @param encoding the encoding of the resource.
     * @return a configured {@link CssLint}
     * @throws IOException if there are any problems reading the resource.
     */
    public CssLint fromClasspathResource(String resource, Charset encoding) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader()
                .getResourceAsStream(resource), encoding));
        return fromReader(reader, resource);
    }

    /**
     * Initialize the scope with a default csslint.js.
     * @return a configured {@link CssLint}
     * @throws RuntimeException if we fail to load the default csslint.js.
     */
    public CssLint fromDefault() {
        try {
            return fromClasspathResource(CSSLINT_FILE);
        } catch (IOException e) {
            // We wrap and rethrow, as there's nothing a caller can do in this case.
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize the scope with the csslint.js passed in on the filesystem. Assumes a UTF-8 encoding.
     * @param f the path to csslint.js
     * @return a configured {@link CssLint}
     * @throws IOException if the file can't be read.
     */
    public CssLint fromFile(File f) throws IOException {
        return fromFile(f, UTF8);
    }

    /**
     * Initialize the scope with the csslint.js passed in on the filesystem.
     * @param f the path to csslint.js
     * @param encoding the encoding of the file
     * @return a configured {@link CssLint}
     * @throws IOException if the file can't be read.
     */
    public CssLint fromFile(File f, Charset encoding) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), encoding));
        return fromReader(reader, f.toString());
    }

    /**
     * Initialize the scope with an arbitrary csslint.
     * @param reader an input source providing csslint.js.
     * @param name the name of the resource backed by the reader
     * @return a configured {@link CssLint}
     * @throws IOException if there are any problems reading from {@code reader} .
     */
    @NeedsContext
    public CssLint fromReader(Reader reader, String name) throws IOException {
        try {
            Context cx = contextFactory.enterContext();
            ScriptableObject scope = cx.initStandardObjects();
            cx.evaluateReader(scope, reader, name, 1, null);
            NativeObject nativeObj = (NativeObject) scope.get("CSSLint", scope);
            return new CssLint(contextFactory, nativeObj);
        } finally {
            Context.exit();
        }
    }

}
