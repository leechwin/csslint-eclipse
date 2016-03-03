package com.leechwin.csslint.eclipse;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.leechwin.csslint.eclipse"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    private IPreferenceStore preferenceStore;

    /**
     * The constructor
     */
    public Activator() {
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
        }
        return preferenceStore;
    }

}
