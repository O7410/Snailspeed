package net.numericalk.snailspeed.screen.custom;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.numericalk.snailspeed.Snailspeed;
import net.numericalk.snailspeed.networking.packets.SawSelectRecipePayload;
import net.numericalk.snailspeed.utils.enums.SawCraftable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SawTableScreen extends HandledScreen<SawTableScreenHandler> {
    private SawCraftable defaultSelectedPiece = SawCraftable.STAIRS;
    private final List<SawCraftableSelectButtonWidget> pieceButtons = new ArrayList<>();

    private static final Identifier GUI_TEXTURE = Identifier.of(Snailspeed.MOD_ID, "textures/gui/container/saw_table_gui.png");

    public SawTableScreen(SawTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        pieceButtons.clear();

        int startX = this.x + 52;
        int startY = this.y + 15; // Fixed: should be y, not x
        int spacingX = 16;
        int spacingY = 16;

        SawCraftable[] pieces = SawCraftable.values();
        int columns = 4;

        for (int i = 0; i < pieces.length; i++) {
            SawCraftable piece = pieces[i];
            int col = i % columns;
            int row = i / columns;

            int x = startX + col * spacingX;
            int y = startY + row * spacingY;

            SawCraftableSelectButtonWidget button = new SawCraftableSelectButtonWidget(x, y, piece, (selected) -> {
                this.defaultSelectedPiece = selected;
                updateSelection();
            });

            pieceButtons.add(button);
            this.addDrawableChild(button);
        }

        updateSelection();
    }


    public SawCraftable getDefaultSelectedPiece() {
        return defaultSelectedPiece;
    }

    private void updateSelection() {
        for (SawCraftableSelectButtonWidget button : pieceButtons) {
            button.setSelected(button.getPiece() == defaultSelectedPiece);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(RenderLayer::getGuiTextured, GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    public class SawCraftableSelectButtonWidget extends PressableWidget {
        private static final Identifier DEFAULT_FRAME = Identifier.of(Snailspeed.MOD_ID, "container/button");
        private static final Identifier SELECTED_FRAME = Identifier.of(Snailspeed.MOD_ID, "container/button_selected");
        private static final Map<SawCraftable, Identifier> ICONS = Map.ofEntries(
                Map.entry(SawCraftable.STAIRS, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/stairs_outline")),
                Map.entry(SawCraftable.SLAB, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/slab_outline")),
                Map.entry(SawCraftable.DOOR, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/door_outline")),
                Map.entry(SawCraftable.BED, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/bed_outline")),
                Map.entry(SawCraftable.FENCE, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/fence_outline")),
                Map.entry(SawCraftable.FENCE_GATE, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/fence_gate_outline")),
                Map.entry(SawCraftable.CHEST, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/chest_outline")),
                Map.entry(SawCraftable.BARREL, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/barrel_outline")),
                Map.entry(SawCraftable.PLANKS, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/planks_outline")),
                Map.entry(SawCraftable.HANGING_SIGN, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/hanging_sign_outline")),
                Map.entry(SawCraftable.SIGN, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/sign_outline")),
                Map.entry(SawCraftable.TRAPDOOR, Identifier.of(Snailspeed.MOD_ID, "container/saw_table/trapdoor_outline"))
        );

        private final SawCraftable piece;
        private final Consumer<SawCraftable> onClick;
        private boolean selected;

        public SawCraftableSelectButtonWidget(int x, int y, SawCraftable piece, Consumer<SawCraftable> onClick) {
            super(x, y, 16, 16, Text.empty());
            this.piece = piece;
            this.onClick = onClick;
        }

        @Override
        public void onPress() {
            this.onClick.accept(piece);

            updateSelection();

            ClientPlayNetworking.send(new SawSelectRecipePayload(handler.getBlockPos(), getDefaultSelectedPiece()));
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            Identifier icon = ICONS.get(piece);
            Identifier frame = this.selected ? SELECTED_FRAME : DEFAULT_FRAME;

            context.drawGuiTexture(RenderLayer::getGuiTextured, frame, this.getX(), this.getY(), this.width, this.height);
            int iconSize = 14;
            int iconX = this.getX() + (this.width - iconSize) / 2;
            int iconY = this.getY() + (this.height - iconSize) / 2;
            context.drawGuiTexture(RenderLayer::getGuiTextured, icon, iconX, iconY, iconSize, iconSize);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {

        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public SawCraftable getPiece() {
            return piece;
        }
    }

}
