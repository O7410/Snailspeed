package net.numericalk.snailspeed.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.numericalk.snailspeed.Snailspeed;
import net.numericalk.snailspeed.blocks.entity.custom.ArmorForgeBlockEntity;
import net.numericalk.snailspeed.blocks.entity.custom.SawTableBlockEntity;
import net.numericalk.snailspeed.networking.packets.ArmorSelectPayload;
import net.numericalk.snailspeed.networking.packets.SawSelectRecipePayload;
import net.numericalk.snailspeed.utils.enums.ArmorPiece;
import net.numericalk.snailspeed.utils.enums.SawCraftable;

public class SnailNetworkingBrain {
    public static final Identifier ARMOR_FORGE_ID = Identifier.of(Snailspeed.MOD_ID, "armor_forge_crafting");
    public static final Identifier SAW_RECIPE_ID = Identifier.of(Snailspeed.MOD_ID, "saw_table_crafting");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(
                ArmorSelectPayload.ARMOR_SELECT_PACKET_ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    BlockPos pos = payload.pos();
                    ArmorPiece selected = payload.armorPiece();

                    player.server.execute(() -> {
                        if (player.getWorld().getBlockEntity(pos) instanceof ArmorForgeBlockEntity blockEntity) {
                            blockEntity.setSelectedPiece(selected);
                        }
                    });
                }
        );

        ServerPlayNetworking.registerGlobalReceiver(
                SawSelectRecipePayload.SAW_CRAFTABLE_RECIPE_PAYLOAD,
                ((sawSelectRecipePayload, context) -> {
                    ServerPlayerEntity player = context.player();
                    BlockPos pos = sawSelectRecipePayload.pos();
                    SawCraftable selected = sawSelectRecipePayload.sawCraftable();

                    player.server.execute(() -> {
                        if (player.getWorld().getBlockEntity(pos) instanceof SawTableBlockEntity blockEntity) {
                            blockEntity.setSelectedPiece(selected);
                        }
                    });
                })
        );

    }
    public static void registerS2CPacket() {

    }
}
