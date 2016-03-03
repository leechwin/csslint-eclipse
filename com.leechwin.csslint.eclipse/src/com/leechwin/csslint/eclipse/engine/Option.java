package com.leechwin.csslint.eclipse.engine;

import java.util.Locale;

public enum Option {

    // Possible Errors: The following rules point out potential errors in your CSS.
    BOX_MODEL("Beware of box model size", Boolean.class),
    DISPLAY_PROPERTY_GROUPING("Require properties appropriate for display", Boolean.class),
    DUPLICATE_PROPERTIES("Disallow duplicate properties", Boolean.class),
    EMPTY_RULES("Disallow empty rules", Boolean.class),
    KNOWN_PROPERTIES("Require use of known properties", Boolean.class),

    // Compatibility: The following rules flag for compatibility problems across browsers and browser settings.
    ADJOINING_CLASSES("Disallow adjoining classes", Boolean.class),
    BOX_SIZING("Disallow box-sizing", Boolean.class),
    COMPATIBLE_VENDOR_PREFIXES("Require compatible vendor prefixes", Boolean.class),
    GRADIENTS("Require all gradient definitions", Boolean.class),
    TEXT_INDENT("Disallow negative text-indent", Boolean.class),
    VENDOR_PREFIX("Require standard property with vendor prefix", Boolean.class),
    FALLBACK_COLORS("Require fallback colors", Boolean.class),
    STAR_PROPERTY_HACK("Disallow star hack", Boolean.class),
    UNDERSCORE_PROPERTY_HACK("Disallow underscore hack", Boolean.class),
    BULLETPROOF_FONT_FACE("Bulletproof font-face", Boolean.class),

    // Performance: The following rules are aimed at improving CSS performance, including runtime performance and overall code size.
    FONT_FACES("Don't use too many web fonts", Boolean.class),
    IMPORT("Disallow @import", Boolean.class),
    REGEX_SELECTORS("Disallow selectors that look like regular expressions", Boolean.class),
    UNIVERSAL_SELECTOR("Disallow universal selector", Boolean.class),
    UNQUALIFIED_ATTRIBUTES("Disallow unqualified attribute selectors", Boolean.class),
    ZERO_UNITS("Disallow units for zero values", Boolean.class),
    OVERQUALIFIED_ELEMENTS("Disallow overqualified elements", Boolean.class),
    SHORTHAND("Require shorthand properties", Boolean.class),
    DUPLICATE_BACKGROUND_IMAGES("Disallow duplicate background images", Boolean.class),

    // Maintainability & Duplication: These rules help to ensure your code is readable and maintainable by others.
    FLOATS("Disallow too many floats", Boolean.class),
    FONT_SIZES("Don't use too many font-size declarations", Boolean.class),
    IDS("Disallow IDs in selectors", Boolean.class),
    IMPORTANT("Disallow !important", Boolean.class),

    // Accessibility: These rules are designed to pick out possible accessibility issues.
    OUTLINE_NONE("Disallow outline:none", Boolean.class),

    // OOCSS: These rules are based on the principles of OOCSS.
    QUALIFIED_HAADINGS("Disallow qualified headings", Boolean.class),
    UNIQUE_HEADINGS("Headings should only be defined once", Boolean.class);

    private String description;
    private Class<?> type;

    private Option(String description, Class<?> type) {
        this.description = description;
        this.type = type;
    }

    /**
     * Return a description of what this option affects.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the lowercase name of this option.
     */
    public String getLowerName() {
        return name().toLowerCase(Locale.getDefault()).replace("_", "-");
    }

    /**
     * What type does the value of this option have?
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Show this option and its description.
     */
    @Override
    public String toString() {
        return getLowerName() + "[" + getDescription() + "]";
    }

}
