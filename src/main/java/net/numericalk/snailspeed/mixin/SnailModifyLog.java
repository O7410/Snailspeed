package net.numericalk.snailspeed.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.Function;

@Mixin(Blocks.class)
public class SnailModifyLog {
    @Unique private static Map<String, FloatFloatPair> BLOCK_STRENGTHS;

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void createBlockStrengthMap(CallbackInfo ci) {
        BLOCK_STRENGTHS = Map.ofEntries(
            entry("oak_wood", 6.0f),
            entry("spruce_wood", 6.0f),
            entry("birch_wood", 6.0f),
            entry("jungle_wood", 6.0f),
            entry("acacia_wood", 6.0f),
            entry("dark_oak_wood", 6.0f),
            entry("mangrove_wood", 6.0f),
            entry("cherry_wood", 6.0f),
            entry("pale_oak_wood", 6.0f),
            entry("crimson_hyphae", 6.0f),
            entry("warped_hyphae", 6.0f),
            entry("oak_planks", 3.0f),
            entry("spruce_planks", 3.0f),
            entry("birch_planks", 3.0f),
            entry("jungle_planks", 3.0f),
            entry("acacia_planks", 3.0f),
            entry("dark_oak_planks", 3.0f),
            entry("mangrove_planks", 3.0f),
            entry("cherry_planks", 3.0f),
            entry("pale_oak_planks", 3.0f),
            entry("crimson_planks", 3.0f),
            entry("warped_planks", 3.0f),

            //CONSTANT

            entry("stripped_oak_wood", 6.0f),
            entry("stripped_spruce_wood", 6.0f),
            entry("stripped_birch_wood", 6.0f),
            entry("stripped_jungle_wood", 6.0f),
            entry("stripped_acacia_wood", 6.0f),
            entry("stripped_dark_oak_wood", 6.0f),
            entry("stripped_mangrove_wood", 6.0f),
            entry("stripped_cherry_wood", 6.0f),
            entry("stripped_pale_oak_wood", 6.0f),
            entry("stripped_crimson_hyphae", 6.0f),
            entry("stripped_warped_hyphae", 6.0f),

            //DIRT AND STONE

            entry("dirt", 4.0f),
            entry("coarse_dirt", 4.5f),
            entry("gravel", 3.0f),
            entry("sand", 3.0f),
            entry("snow_block", 3.0f),
            entry("grass_block", 3.0f),
            entry("farmland", 3.0f),
            entry("podzol", 3.0f),
            entry("rooted_dirt", 3.0f),
            entry("stone", 6.0f),
            entry("cobblestone", 6.5f, 6.0f),
            entry("mossy_cobblestone", 6.5f, 6.0f),
            entry("coal_ore", 7.0f, 3.0f),
            entry("deepslate_coal_ore", 7.5f, 3.0f),
            entry("copper_ore", 7.0f, 3.0f),
            entry("deepslate_copper_ore", 7.0f, 3.0f),
            entry("lapis_ore", 7.0f, 3.0f),
            entry("deepslate_lapis_ore", 7.5f, 3.0f),
            entry("iron_ore", 7.0f, 3.0f),
            entry("deepslate_iron_ore", 7.5f, 3.0f),
            entry("gold_ore", 7.0f, 3.0f),
            entry("deepslate_gold_ore", 7.0f, 3.0f),
            entry("redstone_ore", 7.0f, 3.0f),
            entry("deepslate_redstone_ore", 7.05f, 3.0f),
            entry("emerald_ore", 7.0f, 3.0f),
            entry("deepslate_emerald_ore", 7.0f, 3.0f),
            entry("diamond_ore", 7.0f, 3.0f),
            entry("deepslate_diamond_ore", 7.5f, 3.0f),
            entry("deepslate", 7.0f, 6.0f),
            entry("cobbled_deepslate", 7.5f, 6.0f)
        );
    }

    @Unique
    private static Map.Entry<String, FloatFloatPair> entry(String block, float hardness, float resistance) {
        return Map.entry(block, FloatFloatPair.of(hardness, resistance));
    }

    @Unique
    private static Map.Entry<String, FloatFloatPair> entry(String block, float strength) {
        return Map.entry(block, FloatFloatPair.of(strength, strength));
    }

    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/block/AbstractBlock$Settings;)Lnet/minecraft/block/Block;", at = @At("HEAD"))
    private static void changeStrength(String id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings, CallbackInfoReturnable<Block> cir) {
        FloatFloatPair customStrength = BLOCK_STRENGTHS.get(id);
        if (customStrength == null) {
            return;
        }
        settings.strength(customStrength.leftFloat(), customStrength.rightFloat());
    }

    @ModifyReturnValue(method = "createLogSettings", at = @At("RETURN"))
    private static AbstractBlock.Settings modifyLogSettings(AbstractBlock.Settings original) {
        return original
                .hardness(6.0F)
                .requiresTool();
    }

    @ModifyReturnValue(method = "createNetherStemSettings", at = @At("RETURN"))
    private static AbstractBlock.Settings modifyNetherStemSettings(AbstractBlock.Settings original) {
        return original
                .hardness(6.0F)
                .requiresTool();
    }
}
