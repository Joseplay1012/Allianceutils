package net.joseplay.allianceutils.api.locale;

public enum Locale {
    en_us("American English"),
    pt_br("Brazilian Portuguese");

    private final String name;
    Locale(final String name) {
        this.name = name;
    }

    /**
     * Returns the language name.
     * @return the language name of this locale.
     */
    public String getName() {
        return name;
    }
}
