package net.numericalk.snailspeed.blocks.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.numericalk.snailspeed.Snailspeed;
import net.numericalk.snailspeed.blocks.custom.CampfireBlock;
import net.numericalk.snailspeed.blocks.entity.custom.BrickOvenBlockEntity;

public class BrickOvenBlockEntityRenderer implements BlockEntityRenderer<BrickOvenBlockEntity> {
    public BrickOvenBlockEntityRenderer(BlockEntityRendererFactory.Context context) {

    }

    @Override
    public void render(BrickOvenBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        Direction facing = entity.getCachedState().get(CampfireBlock.FACING);
        renderStackWith(entity.getStack(0), 0.75f, 0.85f, entity, matrices, itemRenderer, vertexConsumers);
        renderStackWith(entity.getStack(1), 0.75f, 0.35f, entity, matrices, itemRenderer, vertexConsumers);
        renderStackWith(entity.getStack(2), 0.25f, 0.85f, entity, matrices, itemRenderer, vertexConsumers);
        renderStackWith(entity.getStack(3), 0.25f, 0.35f, entity, matrices, itemRenderer, vertexConsumers);
        renderStackWith(entity.getStack(4), 0.5f, 0.6f, entity, matrices, itemRenderer, vertexConsumers);
    }

    private void renderStackWith(ItemStack input, float x, float z, BrickOvenBlockEntity entity, MatrixStack matrices, ItemRenderer itemRenderer, VertexConsumerProvider vertexConsumers) {
        if (input.isEmpty()) return;
        matrices.push();
        matrices.translate(x, 0.5f, z);
        matrices.scale(0.75f, 0.75f, 0.75f);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation((float) Math.PI / 2));

        itemRenderer.renderItem(input, ModelTransformationMode.GROUND,
                Snailspeed.getLightLevel(entity.getWorld(), entity.getPos()),
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
        matrices.pop();
    }
}
