package com.iafenvoy.iceandfire.item.armor;

import com.iafenvoy.iceandfire.IceAndFire;
import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.uranus.client.render.armor.IArmorTextureProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemModArmor extends ArmorItem implements IArmorTextureProvider {
    public ItemModArmor(ArmorMaterial material, Type slot) {
        super(material, slot, new Settings());
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (this == IafItems.EARPLUGS.get()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1)
                return "item.iceandfire.air_pods";
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public Identifier getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.material == IafItems.SHEEP_ARMOR_MATERIAL)
            return Identifier.of(IceAndFire.MOD_ID, "textures/models/armor/" + (slot == EquipmentSlot.LEGS ? "sheep_disguise_layer_2" : "sheep_disguise_layer_1") + ".png");
        if (this.material == IafItems.EARPLUGS_ARMOR_MATERIAL)
            return Identifier.of(IceAndFire.MOD_ID, "textures/models/armor/earplugs_layer_1.png");
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if (this == IafItems.EARPLUGS.get()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            if (calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DATE) == 1)
                tooltip.add(Text.translatable("item.iceandfire.air_pods.desc").formatted(Formatting.GREEN));
        }
        super.appendTooltip(stack, worldIn, tooltip, flagIn);
    }
}
