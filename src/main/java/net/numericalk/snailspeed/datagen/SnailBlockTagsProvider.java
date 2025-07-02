package net.numericalk.snailspeed.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.numericalk.snailspeed.Snailspeed;
import net.numericalk.snailspeed.blocks.SnailBlocks;

import java.util.concurrent.CompletableFuture;

public class SnailBlockTagsProvider extends FabricTagProvider<Block> {

    public static final TagKey<Block> TRIMMED_LOGS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "trimmed_logs"));
    public static final TagKey<Block> CRACKED_LOGS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "cracked_logs"));
    public static final TagKey<Block> DAMAGED_LOGS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "damaged_logs"));

    public static final TagKey<Block> DAMAGED_PLANKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "damaged_planks"));

    public static final TagKey<Block> TAPPED_LOGS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "tapped_logs"));

    public static final TagKey<Block> STRIPPED_LOGS_AND_WOODS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "stripped_logs_and_woods"));

    public static final TagKey<Block> STONE_BLOCK = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "stone_block"));
    public static final TagKey<Block> STONE_BLOCK_VARIANTS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "stone_block_variants"));
    public static final TagKey<Block> CRUMBLED_STONE_BLOCK = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "crumbled_stone_block"));

    public static final TagKey<Block> ORES = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "ores"));
    public static final TagKey<Block> DEEPSLATE_ORES = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "deepslate_ores"));

    public static final TagKey<Block> NEEDS_BRONZE_TOOL = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "needs_bronze_tool"));
    public static final TagKey<Block> INCORRECT_FOR_BRONZE_TOOL = TagKey.of(RegistryKeys.BLOCK, Identifier.of(Snailspeed.MOD_ID, "incorrect_for_bronze_tool"));

    public SnailBlockTagsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.BLOCK, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(INCORRECT_FOR_BRONZE_TOOL)
                .forceAddTag(BlockTags.NEEDS_IRON_TOOL)
                .forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL);
        getOrCreateTagBuilder(BlockTags.INCORRECT_FOR_STONE_TOOL)
                .forceAddTag(NEEDS_BRONZE_TOOL);
        getOrCreateTagBuilder(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
                .forceAddTag(NEEDS_BRONZE_TOOL);
        getOrCreateTagBuilder(BlockTags.INCORRECT_FOR_GOLD_TOOL)
                .forceAddTag(NEEDS_BRONZE_TOOL);
        getOrCreateTagBuilder(NEEDS_BRONZE_TOOL)
                .add(Blocks.IRON_ORE)
                .add(Blocks.DEEPSLATE_IRON_ORE)
                .add(Blocks.RAW_IRON_BLOCK)
                .add(Blocks.IRON_BLOCK)
                .add(SnailBlocks.GRAPHITE_ORE)
                .add(SnailBlocks.DEEPSLATE_GRAPHITE_ORE)
                .forceAddTag(BlockTags.NEEDS_STONE_TOOL);
        getOrCreateTagBuilder(TAPPED_LOGS)
                .add(SnailBlocks.TAPPED_SPRUCE_LOG)
                .add(SnailBlocks.TAPPED_BIRCH_LOG)
                .add(SnailBlocks.TAPPED_PALE_OAK_LOG);
        getOrCreateTagBuilder(STONE_BLOCK_VARIANTS)
                .add(Blocks.SMOOTH_STONE)
                .add(Blocks.STONE_SLAB)
                .add(Blocks.SMOOTH_STONE_SLAB)
                .add(Blocks.STONE_STAIRS)
                .add(Blocks.STONE_BRICKS)
                .add(Blocks.CRACKED_STONE_BRICKS)
                .add(Blocks.CHISELED_STONE_BRICKS)
                .add(Blocks.STONE_BRICK_SLAB)
                .add(Blocks.STONE_BRICK_STAIRS)
                .add(Blocks.STONE_BRICK_WALL)

                .add(Blocks.COBBLESTONE_SLAB)
                .add(Blocks.COBBLESTONE_STAIRS)
                .add(Blocks.COBBLESTONE_WALL)
                .add(Blocks.MOSSY_COBBLESTONE)
                .add(Blocks.MOSSY_COBBLESTONE_SLAB)
                .add(Blocks.MOSSY_COBBLESTONE_STAIRS)
                .add(Blocks.MOSSY_COBBLESTONE_WALL)

                .add(Blocks.ANDESITE)
                .add(Blocks.POLISHED_ANDESITE)
                .add(Blocks.ANDESITE_SLAB)
                .add(Blocks.POLISHED_ANDESITE_SLAB)
                .add(Blocks.ANDESITE_STAIRS)
                .add(Blocks.POLISHED_ANDESITE_STAIRS)
                .add(Blocks.ANDESITE_WALL)

                .add(Blocks.GRANITE)
                .add(Blocks.POLISHED_GRANITE)
                .add(Blocks.GRANITE_SLAB)
                .add(Blocks.POLISHED_GRANITE_SLAB)
                .add(Blocks.GRANITE_STAIRS)
                .add(Blocks.POLISHED_GRANITE_STAIRS)
                .add(Blocks.GRANITE_WALL)

                .add(Blocks.DIORITE)
                .add(Blocks.POLISHED_DIORITE)
                .add(Blocks.DIORITE_SLAB)
                .add(Blocks.POLISHED_DIORITE_SLAB)
                .add(Blocks.DIORITE_STAIRS)
                .add(Blocks.POLISHED_DIORITE_STAIRS)
                .add(Blocks.DIORITE_WALL)

                .add(Blocks.TUFF)
                .add(Blocks.POLISHED_TUFF)
                .add(Blocks.TUFF_SLAB)
                .add(Blocks.POLISHED_TUFF_SLAB)
                .add(Blocks.TUFF_STAIRS)
                .add(Blocks.POLISHED_TUFF_STAIRS)
                .add(Blocks.TUFF_WALL)
                .add(Blocks.TUFF_BRICKS)
                .add(Blocks.TUFF_BRICK_SLAB)
                .add(Blocks.TUFF_BRICK_STAIRS)
                .add(Blocks.TUFF_BRICK_WALL)
                .add(Blocks.CHISELED_TUFF)
                .add(Blocks.CHISELED_TUFF_BRICKS)

                .add(Blocks.POLISHED_DEEPSLATE)
                .add(Blocks.DEEPSLATE_BRICKS)
                .add(Blocks.DEEPSLATE_TILES)
                .add(Blocks.CRACKED_DEEPSLATE_BRICKS)
                .add(Blocks.CRACKED_DEEPSLATE_TILES)
                .add(Blocks.CHISELED_DEEPSLATE)
                .add(Blocks.COBBLED_DEEPSLATE_SLAB)
                .add(Blocks.POLISHED_DEEPSLATE_SLAB)
                .add(Blocks.DEEPSLATE_BRICK_SLAB)
                .add(Blocks.DEEPSLATE_TILE_SLAB)
                .add(Blocks.COBBLED_DEEPSLATE_STAIRS)
                .add(Blocks.POLISHED_DEEPSLATE_STAIRS)
                .add(Blocks.DEEPSLATE_BRICK_STAIRS)
                .add(Blocks.DEEPSLATE_TILE_STAIRS)
                .add(Blocks.COBBLED_DEEPSLATE_WALL)
                .add(Blocks.POLISHED_DEEPSLATE_WALL)
                .add(Blocks.DEEPSLATE_BRICK_WALL)
                .add(Blocks.DEEPSLATE_TILE_WALL)

                .add(Blocks.SANDSTONE)
                .add(Blocks.CHISELED_SANDSTONE)
                .add(Blocks.SMOOTH_SANDSTONE)
                .add(Blocks.SANDSTONE_SLAB)
                .add(Blocks.SMOOTH_SANDSTONE_SLAB)
                .add(Blocks.SANDSTONE_STAIRS)
                .add(Blocks.SMOOTH_SANDSTONE_STAIRS)
                .add(Blocks.SANDSTONE_WALL)

                .add(Blocks.RED_SANDSTONE)
                .add(Blocks.CHISELED_RED_SANDSTONE)
                .add(Blocks.SMOOTH_RED_SANDSTONE)
                .add(Blocks.RED_SANDSTONE_SLAB)
                .add(Blocks.SMOOTH_RED_SANDSTONE_SLAB)
                .add(Blocks.RED_SANDSTONE_STAIRS)
                .add(Blocks.SMOOTH_RED_SANDSTONE_STAIRS)
                .add(Blocks.RED_SANDSTONE_WALL);
        getOrCreateTagBuilder(STONE_BLOCK)
                .add(Blocks.STONE)
                .add(SnailBlocks.SCRATCHED_STONE)
                .add(SnailBlocks.CRACKED_STONE)
                .add(Blocks.COBBLESTONE)
                .add(SnailBlocks.FRACTURED_STONE)
                .add(SnailBlocks.CRUMBLED_STONE)
                .add(Blocks.DEEPSLATE)
                .add(SnailBlocks.SCRATCHED_DEEPSLATE)
                .add(SnailBlocks.CRACKED_DEEPSLATE)
                .add(Blocks.COBBLED_DEEPSLATE)
                .add(SnailBlocks.FRACTURED_DEEPSLATE)
                .add(SnailBlocks.CRUMBLED_DEEPSLATE);
        getOrCreateTagBuilder(CRUMBLED_STONE_BLOCK)
                .add(SnailBlocks.CRUMBLED_STONE)
                .add(SnailBlocks.CRUMBLED_DEEPSLATE);

        getOrCreateTagBuilder(STRIPPED_LOGS_AND_WOODS)
                .forceAddTag(ConventionalBlockTags.STRIPPED_LOGS)
                .forceAddTag(ConventionalBlockTags.STRIPPED_WOODS);

        getOrCreateTagBuilder(TRIMMED_LOGS)
                .add(SnailBlocks.TRIMMED_OAK_LOG)
                .add(SnailBlocks.TRIMMED_SPRUCE_LOG)
                .add(SnailBlocks.TRIMMED_BIRCH_LOG)
                .add(SnailBlocks.TRIMMED_JUNGLE_LOG)
                .add(SnailBlocks.TRIMMED_ACACIA_LOG)
                .add(SnailBlocks.TRIMMED_DARK_OAK_LOG)
                .add(SnailBlocks.TRIMMED_MANGROVE_LOG)
                .add(SnailBlocks.TRIMMED_CHERRY_LOG)
                .add(SnailBlocks.TRIMMED_PALE_OAK_LOG)
                .add(SnailBlocks.TRIMMED_CRIMSON_STEM)
                .add(SnailBlocks.TRIMMED_WARPED_STEM);

        getOrCreateTagBuilder(CRACKED_LOGS)
                .add(SnailBlocks.CRACKED_OAK_LOG)
                .add(SnailBlocks.CRACKED_SPRUCE_LOG)
                .add(SnailBlocks.CRACKED_BIRCH_LOG)
                .add(SnailBlocks.CRACKED_JUNGLE_LOG)
                .add(SnailBlocks.CRACKED_ACACIA_LOG)
                .add(SnailBlocks.CRACKED_DARK_OAK_LOG)
                .add(SnailBlocks.CRACKED_MANGROVE_LOG)
                .add(SnailBlocks.CRACKED_CHERRY_LOG)
                .add(SnailBlocks.CRACKED_PALE_OAK_LOG)
                .add(SnailBlocks.CRACKED_CRIMSON_STEM)
                .add(SnailBlocks.CRACKED_WARPED_STEM);

        getOrCreateTagBuilder(DAMAGED_LOGS)
                .add(SnailBlocks.DAMAGED_OAK_LOG)
                .add(SnailBlocks.DAMAGED_SPRUCE_LOG)
                .add(SnailBlocks.DAMAGED_BIRCH_LOG)
                .add(SnailBlocks.DAMAGED_JUNGLE_LOG)
                .add(SnailBlocks.DAMAGED_ACACIA_LOG)
                .add(SnailBlocks.DAMAGED_DARK_OAK_LOG)
                .add(SnailBlocks.DAMAGED_MANGROVE_LOG)
                .add(SnailBlocks.DAMAGED_CHERRY_LOG)
                .add(SnailBlocks.DAMAGED_PALE_OAK_LOG)
                .add(SnailBlocks.DAMAGED_CRIMSON_STEM)
                .add(SnailBlocks.DAMAGED_WARPED_STEM);

        getOrCreateTagBuilder(DAMAGED_PLANKS)
                .add(SnailBlocks.DAMAGED_OAK_PLANKS)
                .add(SnailBlocks.DAMAGED_SPRUCE_PLANKS)
                .add(SnailBlocks.DAMAGED_BIRCH_PLANKS)
                .add(SnailBlocks.DAMAGED_JUNGLE_PLANKS)
                .add(SnailBlocks.DAMAGED_ACACIA_PLANKS)
                .add(SnailBlocks.DAMAGED_DARK_OAK_PLANKS)
                .add(SnailBlocks.DAMAGED_MANGROVE_PLANKS)
                .add(SnailBlocks.DAMAGED_CHERRY_PLANKS)
                .add(SnailBlocks.DAMAGED_PALE_OAK_PLANKS)
                .add(SnailBlocks.DAMAGED_CRIMSON_PLANKS)
                .add(SnailBlocks.DAMAGED_WARPED_PLANKS);

        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(SnailBlocks.DAMAGED_OAK_PLANKS)
                .add(SnailBlocks.DAMAGED_SPRUCE_PLANKS)
                .add(SnailBlocks.DAMAGED_BIRCH_PLANKS)
                .add(SnailBlocks.DAMAGED_JUNGLE_PLANKS)
                .add(SnailBlocks.DAMAGED_ACACIA_PLANKS)
                .add(SnailBlocks.DAMAGED_DARK_OAK_PLANKS)
                .add(SnailBlocks.DAMAGED_MANGROVE_PLANKS)
                .add(SnailBlocks.DAMAGED_CHERRY_PLANKS)
                .add(SnailBlocks.DAMAGED_PALE_OAK_PLANKS)
                .add(SnailBlocks.DAMAGED_CRIMSON_PLANKS)
                .add(SnailBlocks.DAMAGED_WARPED_PLANKS);

        getOrCreateTagBuilder(BlockTags.LOGS)
                .add(SnailBlocks.TRIMMED_OAK_LOG)
                .add(SnailBlocks.TRIMMED_SPRUCE_LOG)
                .add(SnailBlocks.TRIMMED_BIRCH_LOG)
                .add(SnailBlocks.TRIMMED_JUNGLE_LOG)
                .add(SnailBlocks.TRIMMED_ACACIA_LOG)
                .add(SnailBlocks.TRIMMED_DARK_OAK_LOG)
                .add(SnailBlocks.TRIMMED_MANGROVE_LOG)
                .add(SnailBlocks.TRIMMED_CHERRY_LOG)
                .add(SnailBlocks.TRIMMED_PALE_OAK_LOG)
                .add(SnailBlocks.TRIMMED_CRIMSON_STEM)
                .add(SnailBlocks.TRIMMED_WARPED_STEM)

                .add(SnailBlocks.CRACKED_OAK_LOG)
                .add(SnailBlocks.CRACKED_SPRUCE_LOG)
                .add(SnailBlocks.CRACKED_BIRCH_LOG)
                .add(SnailBlocks.CRACKED_JUNGLE_LOG)
                .add(SnailBlocks.CRACKED_ACACIA_LOG)
                .add(SnailBlocks.CRACKED_DARK_OAK_LOG)
                .add(SnailBlocks.CRACKED_MANGROVE_LOG)
                .add(SnailBlocks.CRACKED_CHERRY_LOG)
                .add(SnailBlocks.CRACKED_PALE_OAK_LOG)
                .add(SnailBlocks.CRACKED_CRIMSON_STEM)
                .add(SnailBlocks.CRACKED_WARPED_STEM)

                .add(SnailBlocks.DAMAGED_OAK_LOG)
                .add(SnailBlocks.DAMAGED_SPRUCE_LOG)
                .add(SnailBlocks.DAMAGED_BIRCH_LOG)
                .add(SnailBlocks.DAMAGED_JUNGLE_LOG)
                .add(SnailBlocks.DAMAGED_ACACIA_LOG)
                .add(SnailBlocks.DAMAGED_DARK_OAK_LOG)
                .add(SnailBlocks.DAMAGED_MANGROVE_LOG)
                .add(SnailBlocks.DAMAGED_CHERRY_LOG)
                .add(SnailBlocks.DAMAGED_PALE_OAK_LOG)
                .add(SnailBlocks.DAMAGED_CRIMSON_STEM)
                .add(SnailBlocks.DAMAGED_WARPED_STEM)

                .add(SnailBlocks.TAPPED_SPRUCE_LOG)
                .add(SnailBlocks.TAPPED_BIRCH_LOG)
                .add(SnailBlocks.TAPPED_PALE_OAK_LOG)

                .add(SnailBlocks.DECAYED_SPRUCE_LOG)
                .add(SnailBlocks.DECAYED_BIRCH_LOG)
                .add(SnailBlocks.DECAYED_PALE_OAK_LOG);
        getOrCreateTagBuilder(BlockTags.SNAPS_GOAT_HORN)
                .add(SnailBlocks.TRIMMED_OAK_LOG)
                .add(SnailBlocks.TRIMMED_SPRUCE_LOG)
                .add(SnailBlocks.TRIMMED_BIRCH_LOG)
                .add(SnailBlocks.TRIMMED_JUNGLE_LOG)
                .add(SnailBlocks.TRIMMED_ACACIA_LOG)
                .add(SnailBlocks.TRIMMED_DARK_OAK_LOG)
                .add(SnailBlocks.TRIMMED_MANGROVE_LOG)
                .add(SnailBlocks.TRIMMED_CHERRY_LOG)
                .add(SnailBlocks.TRIMMED_PALE_OAK_LOG)
                .add(SnailBlocks.TRIMMED_CRIMSON_STEM)
                .add(SnailBlocks.TRIMMED_WARPED_STEM)

                .add(SnailBlocks.CRACKED_OAK_LOG)
                .add(SnailBlocks.CRACKED_SPRUCE_LOG)
                .add(SnailBlocks.CRACKED_BIRCH_LOG)
                .add(SnailBlocks.CRACKED_JUNGLE_LOG)
                .add(SnailBlocks.CRACKED_ACACIA_LOG)
                .add(SnailBlocks.CRACKED_DARK_OAK_LOG)
                .add(SnailBlocks.CRACKED_MANGROVE_LOG)
                .add(SnailBlocks.CRACKED_CHERRY_LOG)
                .add(SnailBlocks.CRACKED_PALE_OAK_LOG)
                .add(SnailBlocks.CRACKED_CRIMSON_STEM)
                .add(SnailBlocks.CRACKED_WARPED_STEM);
        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(SnailBlocks.TRIMMED_OAK_LOG)
                .add(SnailBlocks.TRIMMED_SPRUCE_LOG)
                .add(SnailBlocks.TRIMMED_BIRCH_LOG)
                .add(SnailBlocks.TRIMMED_JUNGLE_LOG)
                .add(SnailBlocks.TRIMMED_ACACIA_LOG)
                .add(SnailBlocks.TRIMMED_DARK_OAK_LOG)
                .add(SnailBlocks.TRIMMED_MANGROVE_LOG)
                .add(SnailBlocks.TRIMMED_CHERRY_LOG)
                .add(SnailBlocks.TRIMMED_PALE_OAK_LOG)

                .add(SnailBlocks.CRACKED_OAK_LOG)
                .add(SnailBlocks.CRACKED_SPRUCE_LOG)
                .add(SnailBlocks.CRACKED_BIRCH_LOG)
                .add(SnailBlocks.CRACKED_JUNGLE_LOG)
                .add(SnailBlocks.CRACKED_ACACIA_LOG)
                .add(SnailBlocks.CRACKED_DARK_OAK_LOG)
                .add(SnailBlocks.CRACKED_MANGROVE_LOG)
                .add(SnailBlocks.CRACKED_CHERRY_LOG)
                .add(SnailBlocks.CRACKED_PALE_OAK_LOG)

                .add(SnailBlocks.DAMAGED_OAK_LOG)
                .add(SnailBlocks.DAMAGED_SPRUCE_LOG)
                .add(SnailBlocks.DAMAGED_BIRCH_LOG)
                .add(SnailBlocks.DAMAGED_JUNGLE_LOG)
                .add(SnailBlocks.DAMAGED_ACACIA_LOG)
                .add(SnailBlocks.DAMAGED_DARK_OAK_LOG)
                .add(SnailBlocks.DAMAGED_MANGROVE_LOG)
                .add(SnailBlocks.DAMAGED_CHERRY_LOG)
                .add(SnailBlocks.DAMAGED_PALE_OAK_LOG);
        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(SnailBlocks.CAMPFIRE_BASE)
                .add(SnailBlocks.UNTIED_STICK_BUNDLE)
                .add(SnailBlocks.STICK_BUNDLE)
                .add(SnailBlocks.ARMOR_FORGE)
                .add(SnailBlocks.SMALL_BARREL)
                .add(SnailBlocks.SAW_TABLE);
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(SnailBlocks.BRICK_OVEN_BASE)
                .add(SnailBlocks.BRICK_OVEN)

                .add(SnailBlocks.SCRATCHED_STONE)
                .add(SnailBlocks.CRACKED_STONE)
                .add(SnailBlocks.FRACTURED_STONE)
                .add(SnailBlocks.CRUMBLED_STONE)

                .add(SnailBlocks.SCRATCHED_DEEPSLATE)
                .add(SnailBlocks.CRACKED_DEEPSLATE)
                .add(SnailBlocks.FRACTURED_DEEPSLATE)
                .add(SnailBlocks.CRUMBLED_DEEPSLATE)

                .add(SnailBlocks.BRICK_FURNACE_BASE)
                .add(SnailBlocks.BRICK_FURNACE)

                .add(SnailBlocks.DEEPSLATE_TIN_ORE)
                .add(SnailBlocks.TIN_ORE)

                .add(SnailBlocks.GRAPHITE_ORE)
                .add(SnailBlocks.DEEPSLATE_GRAPHITE_ORE);

        getOrCreateTagBuilder(ORES)
                .add(Blocks.COAL_ORE)
                .add(Blocks.COPPER_ORE)
                .add(Blocks.IRON_ORE)
                .add(Blocks.LAPIS_ORE)
                .add(Blocks.REDSTONE_ORE)
                .add(Blocks.GOLD_ORE)
                .add(Blocks.EMERALD_ORE)
                .add(Blocks.DIAMOND_ORE)
                .add(SnailBlocks.TIN_ORE)
                .add(SnailBlocks.GRAPHITE_ORE);

        getOrCreateTagBuilder(DEEPSLATE_ORES)
                .add(Blocks.DEEPSLATE_COAL_ORE)
                .add(Blocks.DEEPSLATE_COPPER_ORE)
                .add(Blocks.DEEPSLATE_IRON_ORE)
                .add(Blocks.DEEPSLATE_LAPIS_ORE)
                .add(Blocks.DEEPSLATE_REDSTONE_ORE)
                .add(Blocks.DEEPSLATE_GOLD_ORE)
                .add(Blocks.DEEPSLATE_EMERALD_ORE)
                .add(Blocks.DEEPSLATE_DIAMOND_ORE)
                .add(SnailBlocks.DEEPSLATE_TIN_ORE)
                .add(SnailBlocks.DEEPSLATE_GRAPHITE_ORE);
    }
    @Override
    protected FabricTagProvider<Block>.FabricTagBuilder getOrCreateTagBuilder(TagKey<Block> tag) {
        return super.getOrCreateTagBuilder(tag);
    }
}
