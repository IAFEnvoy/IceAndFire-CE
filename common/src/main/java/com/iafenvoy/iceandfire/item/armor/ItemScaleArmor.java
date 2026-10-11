package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.config.IafCommonConfig;
import com.iafenvoy.iceandfire.data.DragonArmor;
import com.iafenvoy.iceandfire.data.DragonColor;
import com.iafenvoy.uranus.object.item.CustomArmorMaterial;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public class ItemScaleArmor extends ArmorItem implements IProtectAgainstDragonItem {
    public final DragonArmor armorType;
    public final DragonColor eggType;

    public ItemScaleArmor(DragonColor eggType, DragonArmor armorType, CustomArmorMaterial material, Type slot) {
        super(material, slot, new Settings());
        this.armorType = armorType;
        this.eggType = eggType;
    }

    @Override
    public String getTranslationKey() {
        return switch (this.type) {
            case HELMET -> "item.iceandfire.dragon_helmet";
            case CHESTPLATE -> "item.iceandfire.dragon_chestplate";
            case LEGGINGS -> "item.iceandfire.dragon_leggings";
            case BOOTS -> "item.iceandfire.dragon_boots";
        };
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        tooltip.add(Text.translatable("dragon." + this.eggType.name().toLowerCase(Locale.ROOT)).formatted(this.eggType.color()));
        if (IafCommonConfig.INSTANCE.armors.dragonScaleBreathReduction.getValue())
            tooltip.add(Text.translatable("item.dragonscales_armor.desc").formatted(Formatting.GRAY));
    }
}
