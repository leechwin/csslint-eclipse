package com.leechwin.csslint.eclipse.preferences;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.leechwin.csslint.eclipse.Activator;
import com.leechwin.csslint.eclipse.engine.Option;

/**
 * Set up the default preferences. By default,we enable:
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

    public static final String PREDEF_ID = Activator.PLUGIN_ID + ".preference.predef";

    private final Set<Option> defaultEnable = EnumSet.of(Option.BOX_MODEL, Option.DISPLAY_PROPERTY_GROUPING,
            Option.DUPLICATE_PROPERTIES, Option.EMPTY_RULES, Option.KNOWN_PROPERTIES);

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences node = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
        for (Option o : defaultEnable) {
            node.putBoolean(o.getLowerName(), true);
        }
    }

}
