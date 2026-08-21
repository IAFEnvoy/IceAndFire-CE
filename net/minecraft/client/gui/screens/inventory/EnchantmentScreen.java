package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
    private static final Identifier[] ENABLED_LEVEL_SPRITES = new Identifier[]{
        Identifier.withDefaultNamespace("container/enchanting_table/level_1"),
        Identifier.withDefaultNamespace("container/enchanting_table/level_2"),
        Identifier.withDefaultNamespace("container/enchanting_table/level_3")
    };
    private static final Identifier[] DISABLED_LEVEL_SPRITES = new Identifier[]{
        Identifier.withDefaultNamespace("container/enchanting_table/level_1_disabled"),
        Identifier.withDefaultNamespace("container/enchanting_table/level_2_disabled"),
        Identifier.withDefaultNamespace("container/enchanting_table/level_3_disabled")
    };
    private static final Identifier ENCHANTMENT_SLOT_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot_disabled");
    private static final Identifier ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace(
        "container/enchanting_table/enchantment_slot_highlighted"
    );
    private static final Identifier ENCHANTMENT_SLOT_SPRITE = Identifier.withDefaultNamespace("container/enchanting_table/enchantment_slot");
    private static final Identifier ENCHANTING_TABLE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/enchanting_table.png");
    private static final Identifier ENCHANTING_BOOK_LOCATION = Identifier.withDefaultNamespace("textures/entity/enchantment/enchanting_table_book.png");
    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.minecraft.player.experienceDisplayStartTick = this.minecraft.player.tickCount;
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;

        for (int i = 0; i < 3; i++) {
            double xx = event.x() - (xo + 60);
            double yy = event.y() - (yo + 14 + 19 * i);
            if (xx >= 0.0 && yy >= 0.0 && xx < 108.0 && yy < 19.0 && this.menu.clickMenuButton(this.minecraft.player, i)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, ENCHANTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        this.extractBook(graphics, xo, yo);
        EnchantmentNames.getInstance().initSeed(this.menu.getEnchantmentSeed());
        int goldCount = this.menu.getGoldCount();

        for (int i = 0; i < 3; i++) {
            int leftPos = xo + 60;
            int leftPosText = leftPos + 20;
            int cost = this.menu.costs[i];
            if (cost == 0) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, leftPos, yo + 14 + 19 * i, 108, 19);
            } else {
                String costText = cost + "";
                int textWidth = 86 - this.font.width(costText);
                FormattedText message = EnchantmentNames.getInstance().getRandomName(this.font, textWidth);
                int col = -9937334;
                if (((goldCount < i + 1 || this.minecraft.player.experienceLevel < cost) && !this.minecraft.player.getAbilities().instabuild) || this.menu.enchantClue[i] == -1) { // Forge: render buttons as disabled when enchantable but enchantability not met on lower levels
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_DISABLED_SPRITE, leftPos, yo + 14 + 19 * i, 108, 19);
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, DISABLED_LEVEL_SPRITES[i], leftPos + 1, yo + 15 + 19 * i, 16, 16);
                    graphics.textWithWordWrap(this.font, message, leftPosText, yo + 16 + 19 * i, textWidth, ARGB.opaque((col & 16711422) >> 1), false);
                    col = -12550384;
                } else {
                    int xx = mouseX - (xo + 60);
                    int yy = mouseY - (yo + 14 + 19 * i);
                    if (xx >= 0 && yy >= 0 && xx < 108 && yy < 19) {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_HIGHLIGHTED_SPRITE, leftPos, yo + 14 + 19 * i, 108, 19);
                        graphics.requestCursor(CursorTypes.POINTING_HAND);
                        col = -128;
                    } else {
                        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENCHANTMENT_SLOT_SPRITE, leftPos, yo + 14 + 19 * i, 108, 19);
                    }

                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ENABLED_LEVEL_SPRITES[i], leftPos + 1, yo + 15 + 19 * i, 16, 16);
                    graphics.textWithWordWrap(this.font, message, leftPosText, yo + 16 + 19 * i, textWidth, col, false);
                    col = -8323296;
                }

                graphics.text(this.font, costText, leftPosText + 86 - this.font.width(costText), yo + 16 + 19 * i + 7, col);
            }
        }
    }

    private void extractBook(GuiGraphicsExtractor graphics, int left, int top) {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        float open = Mth.lerp(a, this.oOpen, this.open);
        float flip = Mth.lerp(a, this.oFlip, this.flip);
        int x0 = left + 14;
        int y0 = top + 14;
        int x1 = x0 + 38;
        int y1 = y0 + 31;
        graphics.book(this.bookModel, ENCHANTING_BOOK_LOCATION, 40.0F, open, flip, x0, y0, x1, y1);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float ignored) {
        float a = this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        super.extractRenderState(graphics, mouseX, mouseY, a);
        boolean infiniteMaterials = this.minecraft.player.hasInfiniteMaterials();
        int gold = this.menu.getGoldCount();

        for (int i = 0; i < 3; i++) {
            int minLevel = this.menu.costs[i];
            Optional<Holder.Reference<Enchantment>> enchant = this.minecraft
                .level
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(this.menu.enchantClue[i]);
            if (true) {
                int enchantLevel = this.menu.levelClue[i];
                int cost = i + 1;
                if (this.isHovering(60, 14 + 19 * i, 108, 17, mouseX, mouseY) && minLevel > 0) {
                    List<Component> texts = Lists.newArrayList();
                    texts.add(
                        Component.translatable("container.enchant.clue", enchant.isEmpty() ? "" : Enchantment.getFullname(enchant.get(), enchantLevel)).withStyle(ChatFormatting.WHITE)
                    );
                    if (enchant.isEmpty()) {
                        texts.add(Component.literal(""));
                        texts.add(Component.translatable("neoforge.container.enchant.limitedEnchantability").withStyle(ChatFormatting.RED));
                    } else if (!infiniteMaterials) {
                        texts.add(CommonComponents.EMPTY);
                        if (this.minecraft.player.experienceLevel < minLevel) {
                            texts.add(Component.translatable("container.enchant.level.requirement", this.menu.costs[i]).withStyle(ChatFormatting.RED));
                        } else {
                            MutableComponent lapisCost;
                            if (cost == 1) {
                                lapisCost = Component.translatable("container.enchant.lapis.one");
                            } else {
                                lapisCost = Component.translatable("container.enchant.lapis.many", cost);
                            }

                            texts.add(lapisCost.withStyle(gold >= cost ? ChatFormatting.GRAY : ChatFormatting.RED));
                            MutableComponent levelCost;
                            if (cost == 1) {
                                levelCost = Component.translatable("container.enchant.level.one");
                            } else {
                                levelCost = Component.translatable("container.enchant.level.many", cost);
                            }

                            texts.add(levelCost.withStyle(ChatFormatting.GRAY));
                        }
                    }

                    graphics.setComponentTooltipForNextFrame(this.font, texts, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    public void tickBook() {
        ItemStack current = this.menu.getSlot(0).getItem();
        if (!ItemStack.matches(current, this.last)) {
            this.last = current;

            do {
                this.flipT = this.flipT + (this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
        }

        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean shouldBeOpen = false;

        for (int i = 0; i < 3; i++) {
            if (this.menu.costs[i] != 0) {
                shouldBeOpen = true;
                break;
            }
        }

        if (shouldBeOpen) {
            this.open += 0.2F;
        } else {
            this.open -= 0.2F;
        }

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float diff = (this.flipT - this.flip) * 0.4F;
        float max = 0.2F;
        diff = Mth.clamp(diff, -0.2F, 0.2F);
        this.flipA = this.flipA + (diff - this.flipA) * 0.9F;
        this.flip = this.flip + this.flipA;
    }
}
