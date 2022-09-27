package net.migats21.interactiveeye.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringMappings {
    public static Map<Material, String> materials;
    public static Map<MaterialColor, String> materialColors;
    public static Map<Property, Function<String, String>> propertyValues;
    private static List<String> notes;
    private static List<String> directions;

    public static void init() {
        materials = ImmutableMap.<Material, String>builder()
        .put(Material.AIR,"air")
        .put(Material.AMETHYST,"amethyst")
        .put(Material.BAMBOO,"bamboo")
        .put(Material.BARRIER,"static")
        .put(Material.BAMBOO_SAPLING,"bamboo")
        .put(Material.BUBBLE_COLUMN,"water")
        .put(Material.BUILDABLE_GLASS,"glass")
        .put(Material.CACTUS,"cactus")
        .put(Material.CAKE,"cake")
        .put(Material.CLAY,"clay")
        .put(Material.CLOTH_DECORATION,"wool")
        .put(Material.DECORATION,"decoration")
        .put(Material.DIRT,"dirt")
        .put(Material.EGG,"egg")
        .put(Material.EXPLOSIVE,"explosive")
        .put(Material.FIRE,"fire")
        .put(Material.FROGLIGHT,"froglight")
        .put(Material.FROGSPAWN,"frog spawn")
        .put(Material.GLASS,"glass")
        .put(Material.GRASS,"grass")
        .put(Material.ICE,"ice")
        .put(Material.ICE_SOLID,"ice")
        .put(Material.LAVA,"lava")
        .put(Material.LEAVES,"leaves")
        .put(Material.METAL,"metal")
        .put(Material.MOSS,"moss")
        .put(Material.NETHER_WOOD,"nether_wood")
        .put(Material.PISTON,"piston")
        .put(Material.PLANT,"plant")
        .put(Material.PORTAL,"portal")
        .put(Material.POWDER_SNOW,"snow")
        .put(Material.REPLACEABLE_PLANT,"plant")
        .put(Material.REPLACEABLE_FIREPROOF_PLANT,"plant")
        .put(Material.REPLACEABLE_WATER_PLANT,"water_plant")
        .put(Material.SAND,"sand")
        .put(Material.SCULK,"sculk")
        .put(Material.SHULKER_SHELL,"shulker_box")
        .put(Material.SNOW,"snow")
        .put(Material.SPONGE,"sponge")
        .put(Material.STONE,"stone")
        .put(Material.STRUCTURAL_AIR,"static")
        .put(Material.TOP_SNOW,"snow")
        .put(Material.VEGETABLE,"vegetable")
        .put(Material.WATER,"water")
        .put(Material.WATER_PLANT,"water_plant")
        .put(Material.WEB,"cobweb")
        .put(Material.WOOD,"wood")
        .put(Material.WOOL,"wool").build();

        materialColors = ImmutableMap.<MaterialColor, String>builder()
        .put(MaterialColor.CLAY, "clay")
        .put(MaterialColor.COLOR_BLACK, "black")
        .put(MaterialColor.COLOR_BLUE, "blue")
        .put(MaterialColor.COLOR_BROWN, "brown")
        .put(MaterialColor.COLOR_CYAN, "cyan")
        .put(MaterialColor.COLOR_GRAY, "gray")
        .put(MaterialColor.COLOR_GREEN, "black")
        .put(MaterialColor.COLOR_LIGHT_BLUE, "light_blue")
        .put(MaterialColor.COLOR_LIGHT_GRAY, "light_gray")
        .put(MaterialColor.COLOR_LIGHT_GREEN, "light_green")
        .put(MaterialColor.COLOR_MAGENTA, "magenta")
        .put(MaterialColor.COLOR_ORANGE, "orange")
        .put(MaterialColor.COLOR_PINK, "pink")
        .put(MaterialColor.COLOR_PURPLE, "purple")
        .put(MaterialColor.COLOR_RED, "red")
        .put(MaterialColor.COLOR_YELLOW, "yellow")
        .put(MaterialColor.CRIMSON_HYPHAE, "crimson_hyphae")
        .put(MaterialColor.CRIMSON_NYLIUM, "crimson_nylium")
        .put(MaterialColor.CRIMSON_STEM, "crimson_wood")
        .put(MaterialColor.DEEPSLATE, "deepslate")
        .put(MaterialColor.DIAMOND, "diamond")
        .put(MaterialColor.DIRT, "dirt")
        .put(MaterialColor.EMERALD, "emerald")
        .put(MaterialColor.FIRE, "fire")
        .put(MaterialColor.GLOW_LICHEN, "glow_lichen")
        .put(MaterialColor.GOLD, "gold")
        .put(MaterialColor.GRASS, "grass")
        .put(MaterialColor.ICE, "ice")
        .put(MaterialColor.LAPIS, "lapis")
        .put(MaterialColor.METAL, "metalic")
        .put(MaterialColor.NETHER, "nether")
        .put(MaterialColor.NONE, "transparent")
        .put(MaterialColor.PLANT, "plant")
        .put(MaterialColor.PODZOL, "podzol")
        .put(MaterialColor.QUARTZ, "quartz")
        .put(MaterialColor.RAW_IRON, "raw_iron")
        .put(MaterialColor.SAND, "sand")
        .put(MaterialColor.SNOW, "snow")
        .put(MaterialColor.STONE, "stone")
        .put(MaterialColor.TERRACOTTA_BLACK, "terracotta_black")
        .put(MaterialColor.TERRACOTTA_BLUE, "terracotta_blue")
        .put(MaterialColor.TERRACOTTA_BROWN, "terracotta_brown")
        .put(MaterialColor.TERRACOTTA_CYAN, "terracotta_cyan")
        .put(MaterialColor.TERRACOTTA_GRAY, "terracotta_gray")
        .put(MaterialColor.TERRACOTTA_GREEN, "terracotta_green")
        .put(MaterialColor.TERRACOTTA_LIGHT_BLUE, "terracotta_light_blue")
        .put(MaterialColor.TERRACOTTA_LIGHT_GRAY, "terracotta_light_gray")
        .put(MaterialColor.TERRACOTTA_LIGHT_GREEN, "terracotta_light_green")
        .put(MaterialColor.TERRACOTTA_MAGENTA, "terracotta_magenta")
        .put(MaterialColor.TERRACOTTA_ORANGE, "terracotta_orange")
        .put(MaterialColor.TERRACOTTA_PINK, "terracotta_pink")
        .put(MaterialColor.TERRACOTTA_PURPLE, "terracotta_purple")
        .put(MaterialColor.TERRACOTTA_RED, "terracotta_red")
        .put(MaterialColor.TERRACOTTA_WHITE, "terracotta_white")
        .put(MaterialColor.TERRACOTTA_YELLOW, "terracotta_yellow")
        .put(MaterialColor.WATER, "water")
        .put(MaterialColor.WARPED_HYPHAE, "warped_hyphae")
        .put(MaterialColor.WARPED_NYLIUM, "warped_nylium")
        .put(MaterialColor.WARPED_STEM, "warped_stem")
        .put(MaterialColor.WARPED_WART_BLOCK, "warped_wart")
        .put(MaterialColor.WOOD, "wood")
        .put(MaterialColor.WOOL, "wool").build();

        notes = ImmutableList.of("F#","G","G#","A","A#","B","C","C#","D","D#","E","F");
        directions = ImmutableList.of("North", "NNE", "North East", "ENE", "East", "ESE", "South East", "SSE", "South", "SSW", "South West", "WSW", "West", "WNW", "North West", "NNW");
        propertyValues = ImmutableMap.<Property, Function<String, String>>builder()
            .put(BlockStateProperties.NOTE, (string) -> {
                int i = Integer.parseInt(string);
                return notes.get(i % 12) + (int)Math.floor(i/12f);
            }).put(BlockStateProperties.DELAY, (string) -> string + " ticks")
            .put(BlockStateProperties.ROTATION_16, (string) -> directions.get(Integer.parseInt(string))
            ).put(BlockStateProperties.RAIL_SHAPE, (string) ->
                Pattern.compile("(^|_)([a-z])").matcher(string).replaceAll((result) -> (result.group(1).isEmpty() ? "" : " ") + result.group(2).toUpperCase())
            ).put(BlockStateProperties.RAIL_SHAPE_STRAIGHT, (string) ->
                Pattern.compile("(^|_)([a-z])").matcher(string).replaceAll((result) -> (result.group(1).isEmpty() ? "" : " ") + result.group(2).toUpperCase())
            )
            .build();
    }
}
