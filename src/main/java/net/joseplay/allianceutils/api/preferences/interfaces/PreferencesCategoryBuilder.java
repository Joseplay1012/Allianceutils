package net.joseplay.allianceutils.api.preferences.interfaces;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PreferencesCategoryBuilder {
    public static class Builder {
        private String name = "";
        private List<String> description = List.of();
        private String id = "";
        private ItemStack icon = null;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(List<String> description) {
            this.description = description;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }

        public PreferencesCategory build() {
            return new PreferencesCategory() {
                @Override
                public String getName() {
                    return name;
                }

                @Override
                public List<String> getDescription() {
                    return description;
                }

                @Override
                public String getID() {
                    return id;
                }

                @Override
                public ItemStack getIcon() {
                    return icon;
                }
            };
        }
    }
}