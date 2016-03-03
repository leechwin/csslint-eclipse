package com.leechwin.csslint.eclipse.builder;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.leechwin.csslint.eclipse.Activator;
import com.leechwin.csslint.eclipse.CssLintLog;
import com.leechwin.csslint.eclipse.engine.CssLint;
import com.leechwin.csslint.eclipse.engine.CssLintBuilder;
import com.leechwin.csslint.eclipse.engine.Option;

/**
 * Provide a fully configured instance of {@link CssLint} on demand.
 */
public class CssLintProvider {

    private final CssLintBuilder builder = new CssLintBuilder();

    private CssLint cssLint;

    /**
     * Set up a listener for preference changes. This will ensure that the instance of {@link CssLint} that we have is kept in sync with the users choices. We do this by ensuring that a new lint will
     * be created and configured on the next request.
     */
    public void init() {
        IEclipsePreferences x = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        x.addPreferenceChangeListener(new IPreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent ev) {
                cssLint = null;
                CssLintLog.info("pref %s changed; nulling cssLint", ev.getKey());
            }
        });
    }

    /**
     * Return a fully configured instance of lint. This should not be cached; each use should call this method.
     */
    public CssLint getCssLint() {
        if (cssLint == null) {
            cssLint = builder.fromDefault();
            configure();
        }
        return cssLint;
    }

    /** Set up the current instance of lint using the current preferences. */
    public void configure() {
        CssLint lint = getCssLint();
        lint.resetOptions();
        IPreferencesService prefs = Platform.getPreferencesService();
        for (Option o : Option.values()) {
            String value = prefs.getString(Activator.PLUGIN_ID, o.getLowerName(), null, null);
            if (value != null) {
                lint.addOption(o, value);
            }
        }
    }

}
