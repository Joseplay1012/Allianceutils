package net.joseplay.allianceutils.api.materialVariants;

import org.bukkit.Material;

import java.util.List;

/**
 * Static registry that groups {@link Material} into logical variants.
 *
 * <p>A variant represents a set of related materials, such as different
 * forms of the same resource (e.g., ore, deepslate ore, raw block).</p>
 *
 * <b>Example usage:</b>
 * <pre>
 * Variant variant = MaterialVariant.getVariant(Material.IRON_ORE);
 * // returns "IRON"
 * </pre>
 *
 * <b>Limitations:</b>
 * <ul>
 *     <li>Lookup is O(n) (linear search)</li>
 *     <li>No reverse lookup cache (Material → Variant)</li>
 *     <li>Returns null if no variant is found</li>
 * </ul>
 */
public final class MaterialVariant {
    /**
     * Immutable list of all registered variants.
     *
     * <b>Important:</b>
     * <ul>
     *     <li>Search order matters (first match wins)</li>
     *     <li>Materials should not be duplicated across variants</li>
     * </ul>
     */
    public static final List<Variant> VARIANTS = List.of(

            // ===== ORES =====
            new Variant("IRON", List.of(
                    Material.IRON_ORE,
                    Material.DEEPSLATE_IRON_ORE,
                    Material.RAW_IRON_BLOCK
            )),

            new Variant("IRON_RAW", List.of(
                    Material.RAW_IRON
            )),

            new Variant("GOLD_RAW", List.of(
                    Material.RAW_GOLD
            )),

            new Variant("COPPER_RAW", List.of(
                    Material.RAW_COPPER
            )),

            new Variant("COAL", List.of(
                    Material.COAL_ORE,
                    Material.DEEPSLATE_COAL_ORE
            )),

            new Variant("COPPER", List.of(
                    Material.COPPER_ORE,
                    Material.DEEPSLATE_COPPER_ORE,
                    Material.RAW_COPPER_BLOCK
            )),

            new Variant("GOLD", List.of(
                    Material.GOLD_ORE,
                    Material.DEEPSLATE_GOLD_ORE,
                    Material.RAW_GOLD_BLOCK
            )),

            new Variant("REDSTONE", List.of(
                    Material.REDSTONE_ORE,
                    Material.DEEPSLATE_REDSTONE_ORE
            )),

            new Variant("LAPIS", List.of(
                    Material.LAPIS_ORE,
                    Material.DEEPSLATE_LAPIS_ORE
            )),

            new Variant("DIAMOND", List.of(
                    Material.DIAMOND_ORE,
                    Material.DEEPSLATE_DIAMOND_ORE
            )),

            new Variant("EMERALD", List.of(
                    Material.EMERALD_ORE,
                    Material.DEEPSLATE_EMERALD_ORE
            )),

            new Variant("QUARTZ", List.of(
                    Material.NETHER_QUARTZ_ORE
            )),

            new Variant("ANCIENT_DEBRIS", List.of(
                    Material.ANCIENT_DEBRIS
            )),

            // ===== MADEIRAS =====
            new Variant("OAK", List.of(
                    Material.OAK_LOG,
                    Material.OAK_WOOD
            )),

            new Variant("STRIPPED_OAK", List.of(
                    Material.STRIPPED_OAK_LOG,
                    Material.STRIPPED_OAK_WOOD
            )),

            new Variant("BIRCH", List.of(
                    Material.BIRCH_LOG,
                    Material.BIRCH_WOOD
            )),

            new Variant("STRIPPED_BIRCH", List.of(
                    Material.STRIPPED_BIRCH_LOG,
                    Material.STRIPPED_BIRCH_WOOD
            )),

            new Variant("SPRUCE", List.of(
                    Material.SPRUCE_LOG,
                    Material.SPRUCE_WOOD
            )),

            new Variant("STRIPPED_SPRUCE", List.of(
                    Material.STRIPPED_SPRUCE_LOG,
                    Material.STRIPPED_SPRUCE_WOOD
            )),

            new Variant("JUNGLE", List.of(
                    Material.JUNGLE_LOG,
                    Material.JUNGLE_WOOD
            )),

            new Variant("STRIPPED_JUNGLE", List.of(
                    Material.STRIPPED_JUNGLE_LOG,
                    Material.STRIPPED_JUNGLE_WOOD
            )),

            new Variant("ACACIA", List.of(
                    Material.ACACIA_LOG,
                    Material.ACACIA_WOOD
            )),

            new Variant("STRIPPED_ACACIA", List.of(
                    Material.STRIPPED_ACACIA_LOG,
                    Material.STRIPPED_ACACIA_WOOD
            )),

            new Variant("DARK_OAK", List.of(
                    Material.DARK_OAK_LOG,
                    Material.DARK_OAK_WOOD
            )),

            new Variant("STRIPPED_DARK_OAK", List.of(
                    Material.STRIPPED_DARK_OAK_LOG,
                    Material.STRIPPED_DARK_OAK_WOOD
            )),

            new Variant("MANGROVE", List.of(
                    Material.MANGROVE_LOG,
                    Material.MANGROVE_WOOD
            )),

            new Variant("STRIPPED_MANGROVE", List.of(
                    Material.STRIPPED_MANGROVE_LOG,
                    Material.STRIPPED_MANGROVE_WOOD
            )),

            new Variant("CHERRY", List.of(
                    Material.CHERRY_LOG,
                    Material.CHERRY_WOOD
            )),

            new Variant("STRIPPED_CHERRY", List.of(
                    Material.STRIPPED_CHERRY_LOG,
                    Material.STRIPPED_CHERRY_WOOD
            )),

            new Variant("CRIMSON", List.of(
                    Material.CRIMSON_STEM,
                    Material.CRIMSON_HYPHAE
            )),

            new Variant("STRIPPED_CRIMSON", List.of(
                    Material.STRIPPED_CRIMSON_STEM,
                    Material.STRIPPED_CRIMSON_HYPHAE
            )),

            new Variant("WARPED", List.of(
                    Material.WARPED_STEM,
                    Material.WARPED_HYPHAE
            )),

            new Variant("STRIPPED_WARPED", List.of(
                    Material.STRIPPED_WARPED_STEM,
                    Material.STRIPPED_WARPED_HYPHAE
            )),

            //Blocos
            new Variant("SAND", List.of(
                    Material.SAND,
                    Material.RED_SAND
            )),

            new Variant("COBBLESTONE", List.of(
                    Material.COBBLESTONE
            )),

            new Variant("CLAY", List.of(
                    Material.CLAY_BALL
            )),

            new Variant("NETHERRACK", List.of(
                    Material.NETHERRACK
            )),

            new Variant("CACTUS", List.of(
                    Material.CACTUS
            )),

            // ===== TERRACOTTA =====
            new Variant("TERRACOTTA_WHITE", List.of(
                    Material.WHITE_TERRACOTTA
            )),

            new Variant("TERRACOTTA_ORANGE", List.of(
                    Material.ORANGE_TERRACOTTA
            )),

            new Variant("TERRACOTTA_MAGENTA", List.of(
                    Material.MAGENTA_TERRACOTTA
            )),

            new Variant("TERRACOTTA_LIGHT_BLUE", List.of(
                    Material.LIGHT_BLUE_TERRACOTTA
            )),

            new Variant("TERRACOTTA_YELLOW", List.of(
                    Material.YELLOW_TERRACOTTA
            )),

            new Variant("TERRACOTTA_LIME", List.of(
                    Material.LIME_TERRACOTTA
            )),

            new Variant("TERRACOTTA_PINK", List.of(
                    Material.PINK_TERRACOTTA
            )),

            new Variant("TERRACOTTA_GRAY", List.of(
                    Material.GRAY_TERRACOTTA
            )),

            new Variant("TERRACOTTA_LIGHT_GRAY", List.of(
                    Material.LIGHT_GRAY_TERRACOTTA
            )),

            new Variant("TERRACOTTA_CYAN", List.of(
                    Material.CYAN_TERRACOTTA
            )),

            new Variant("TERRACOTTA_PURPLE", List.of(
                    Material.PURPLE_TERRACOTTA
            )),

            new Variant("TERRACOTTA_BLUE", List.of(
                    Material.BLUE_TERRACOTTA
            )),

            new Variant("TERRACOTTA_BROWN", List.of(
                    Material.BROWN_TERRACOTTA
            )),

            new Variant("TERRACOTTA_GREEN", List.of(
                    Material.GREEN_TERRACOTTA
            )),

            new Variant("TERRACOTTA_RED", List.of(
                    Material.RED_TERRACOTTA
            )),

            new Variant("TERRACOTTA_BLACK", List.of(
                    Material.BLACK_TERRACOTTA
            )),

            new Variant("SIGN", List.of(
                    Material.OAK_SIGN,
                    Material.BIRCH_SIGN,
                    Material.SPRUCE_SIGN,
                    Material.JUNGLE_SIGN,
                    Material.ACACIA_SIGN,
                    Material.DARK_OAK_SIGN,
                    Material.MANGROVE_SIGN,
                    Material.CHERRY_SIGN,
                    Material.CRIMSON_SIGN,
                    Material.WARPED_SIGN
            )),

            new Variant("WALL_SIGN", List.of(
                    Material.OAK_WALL_SIGN,
                    Material.BIRCH_WALL_SIGN,
                    Material.SPRUCE_WALL_SIGN,
                    Material.JUNGLE_WALL_SIGN,
                    Material.ACACIA_WALL_SIGN,
                    Material.DARK_OAK_WALL_SIGN,
                    Material.MANGROVE_WALL_SIGN,
                    Material.CHERRY_WALL_SIGN,
                    Material.CRIMSON_WALL_SIGN,
                    Material.WARPED_WALL_SIGN
            )),

            new Variant("PANE_GLASS", List.of(
                    Material.RED_STAINED_GLASS_PANE,
                    Material.ORANGE_STAINED_GLASS_PANE,
                    Material.MAGENTA_STAINED_GLASS_PANE,
                    Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                    Material.YELLOW_STAINED_GLASS_PANE,
                    Material.LIME_STAINED_GLASS_PANE,
                    Material.PINK_STAINED_GLASS_PANE,
                    Material.GRAY_STAINED_GLASS_PANE,
                    Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                    Material.CYAN_STAINED_GLASS_PANE,
                    Material.PURPLE_STAINED_GLASS_PANE,
                    Material.BLUE_STAINED_GLASS_PANE,
                    Material.BROWN_STAINED_GLASS_PANE,
                    Material.GREEN_STAINED_GLASS_PANE,
                    Material.RED_STAINED_GLASS_PANE,
                    Material.BLACK_STAINED_GLASS_PANE,
                    Material.WHITE_STAINED_GLASS_PANE
            )),

            new Variant("GLASS", List.of(
                    Material.RED_STAINED_GLASS,
                    Material.ORANGE_STAINED_GLASS,
                    Material.MAGENTA_STAINED_GLASS,
                    Material.LIGHT_BLUE_STAINED_GLASS,
                    Material.YELLOW_STAINED_GLASS,
                    Material.LIME_STAINED_GLASS,
                    Material.PINK_STAINED_GLASS,
                    Material.GRAY_STAINED_GLASS,
                    Material.LIGHT_GRAY_STAINED_GLASS,
                    Material.CYAN_STAINED_GLASS,
                    Material.PURPLE_STAINED_GLASS,
                    Material.BLUE_STAINED_GLASS,
                    Material.BROWN_STAINED_GLASS,
                    Material.GREEN_STAINED_GLASS,
                    Material.RED_STAINED_GLASS,
                    Material.BLACK_STAINED_GLASS,
                    Material.WHITE_STAINED_GLASS
            )),

            new Variant("SHULKER_BOX", List.of(
                    Material.SHULKER_BOX,
                    Material.WHITE_SHULKER_BOX,
                    Material.ORANGE_SHULKER_BOX,
                    Material.MAGENTA_SHULKER_BOX,
                    Material.LIGHT_BLUE_SHULKER_BOX,
                    Material.YELLOW_SHULKER_BOX,
                    Material.LIME_SHULKER_BOX,
                    Material.PINK_SHULKER_BOX,
                    Material.GRAY_SHULKER_BOX,
                    Material.LIGHT_GRAY_SHULKER_BOX,
                    Material.CYAN_SHULKER_BOX,
                    Material.PURPLE_SHULKER_BOX,
                    Material.BLUE_SHULKER_BOX,
                    Material.BROWN_SHULKER_BOX,
                    Material.GREEN_SHULKER_BOX,
                    Material.RED_SHULKER_BOX,
                    Material.BLACK_SHULKER_BOX
            ))
    );


    private MaterialVariant() {
    }

    /**
     * Returns the variant associated with the given material.
     *
     * <b>Behavior:</b>
     * <ul>
     *     <li>Returns the first matching variant</li>
     *     <li>Returns null if no match is found</li>
     * </ul>
     *
     * <b>Complexity:</b> O(n)
     *
     * @param material target material
     * @return matching variant or null
     */
    public static Variant getVariant(Material material) {
        return VARIANTS.stream().filter(variant -> variant.materials().contains(material)).findFirst().orElse(null);
    }


    /**
     * Represents a logical group of materials.
     *
     * @param variant   identifier name
     * @param materials associated materials
     */
    public record Variant(String variant, List<Material> materials) {

        /**
         * Convenience constructor for single-material variants.
         *
         * @param variant  identifier name
         * @param material single material
         */
        public Variant(String variant, Material material) {
            this(variant, List.of(material));
        }
    }
}