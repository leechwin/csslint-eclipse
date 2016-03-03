package com.leechwin.csslint.eclipse.engine;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

/**
 * A utility class to check css source code for potential problems.
 * @see CssLintBuilder Construction of lint
 */
public class CssLint {

    private final Map<Option, Object> options = new EnumMap<Option, Object>(Option.class);

    private final ContextFactory contextFactory;

    private final NativeObject lintFunc;

    /**
     * singleton instance
     */
    protected final static CssLint instance = new CssLintBuilder().fromDefault();

    /**
     * access method for singleton object
     * @return singleton instance
     */
    public static CssLint getInstance() {
        return instance;
    }

    /**
     * Create a new {@link CssLint} object. You must pass in a {@link Function}, which is the lint function defined by csslint.js. You are expected to use {@link CssLintBuilder} rather than calling
     * this constructor.
     */
    CssLint(ContextFactory contextFactory, NativeObject lintFunc) {
        this.contextFactory = contextFactory;
        this.lintFunc = lintFunc;
    }

    /**
     * Add an option to change the behaviour of the lint. This will be passed in with a value of "true".
     * @param o Any {@link Option}.
     */
    public void addOption(Option o) {
        options.put(o, Boolean.TRUE);
    }

    /**
     * Add an option to change the behaviour of the lint. The option will be parsed as appropriate using an {@link OptionParser}.
     * @param o Any {@link Option}.
     * @param arg The value to associate with <i>o</i>.
     */
    public void addOption(Option o, String arg) {
        OptionParser optionParser = new OptionParser();
        options.put(o, optionParser.parse(o.getType(), arg));
    }

    @NeedsContext
    private CssLintResult doLint(final String css) {
        return (CssLintResult) contextFactory.call(new ContextAction() {
            @SuppressWarnings("static-access")
            public CssLintResult run(Context cx) {
                String src = css == null ? "" : css;
                Object[] args = new Object[] { src, optionsAsCssObject() };
                NativeObject result = (NativeObject) lintFunc.callMethod(cx, lintFunc, "verify", args);
                NativeArray nativeList = (NativeArray) result.get("messages");

                ArrayList<Issue> issueList = new ArrayList<Issue>();
                for (int i = 0; i < nativeList.getLength(); i++) {
                    Object obj = nativeList.get(i);
                    if (obj instanceof NativeObject) {
                        NativeObject nativeObj = (NativeObject) obj;
                        Object line = nativeObj.get("line");
                        Object col = nativeObj.get("col");
                        Object message = nativeObj.get("message");
                        Object type = nativeObj.get("type");
                        if (line != null && col != null && message != null) {
                            Issue issue = new Issue(((Double) line).intValue(),
                                    ((Double) col).intValue(),
                                    (String) message.toString(),
                                    (String) type);
                            issueList.add(issue);
                        }
                    }
                }

                return new CssLintResult(issueList);
            }
        });
    }

    /**
     * Check for problems in a {@link Reader} which contains css source.
     * @param systemId a filename
     * @param reader a {@link Reader} over css source code.
     * @return a {@link CssLintResult}.
     */
    public CssLintResult lint(String systemId, Reader reader) throws IOException {
        return lint(systemId, Util.readerToString(reader));
    }

    /**
     * Check for problems in css source.
     * @param systemId a filename
     * @param css a String of css source code.
     * @return a {@link CssLintResult}.
     */
    public CssLintResult lint(String systemId, String css) {
        // This is synchronized, even though Rhino is thread safe, because we have multiple
        // accesses to the scope, which store state in between them. This synchronized block
        // is slightly larger than I would like, but in practical terms, it doesn't make much
        // difference. The cost of running lint is larger than the cost of pulling out the
        // results.
        synchronized (this) {
            return doLint(css);
        }
    }

    /**
     * Turn the set of options into a JavaScript object, where the key is the name of the option and the value is true.
     */
    @NeedsContext
    private Scriptable optionsAsCssObject() {
        return (Scriptable) contextFactory.call(new ContextAction() {
            public Object run(Context cx) {
                Scriptable opts = cx.newObject(lintFunc);
                for (Entry<Option, Object> entry : options.entrySet()) {
                    String key = entry.getKey().getLowerName();
                    Object value = Util.javaToJS(entry.getValue(), opts);
                    opts.put(key, opts, value);
                }
                return opts;
            }
        });
    }

    /**
     * Clear out all options that have been set with {@link #addOption(Option)}.
     */
    public void resetOptions() {
        options.clear();
    }

}
