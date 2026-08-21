package com.iafenvoy.iceandfire.item.block;

import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.iceandfire.item.block.util.DragonProof;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DragonScalesBlock extends Block implements DragonProof {
    final DragonColor type;

    public DragonScalesBlock(DragonColor type) {
        super(Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).dynamicShape().strength(30F, 500).sound(SoundType.STONE).requiresCorrectToolForDrops());
        this.type = type;
    }

    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull TooltipDisplay display, Consumer<Component> tooltip, @NotNull TooltipFlag options) {
        tooltip.accept(Component.translatable("dragon." + this.type.getName()).withStyle(this.type.getColorFormatting()));
    }
}
