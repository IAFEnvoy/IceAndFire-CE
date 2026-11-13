package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelFireArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelIceArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelLightningArmorModel;
import com.iafenvoy.uranus.client.render.armor.IArmorRendererBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Exercises registered armor providers and measures their actual emitted, inflated vertices. */
public final class ArmorModelRegression {
    public static int run() {
        dragonsteel("fire", List.of(IafItems.DRAGONSTEEL_FIRE_HELMET.get(), IafItems.DRAGONSTEEL_FIRE_CHESTPLATE.get(),
                IafItems.DRAGONSTEEL_FIRE_LEGGINGS.get(), IafItems.DRAGONSTEEL_FIRE_BOOTS.get()),
                DragonSteelFireArmorModel.createMesh(CubeDeformation.NONE, 0, false));
        dragonsteel("ice", List.of(IafItems.DRAGONSTEEL_ICE_HELMET.get(), IafItems.DRAGONSTEEL_ICE_CHESTPLATE.get(),
                IafItems.DRAGONSTEEL_ICE_LEGGINGS.get(), IafItems.DRAGONSTEEL_ICE_BOOTS.get()),
                DragonSteelIceArmorModel.createMesh(CubeDeformation.NONE, 0, false));
        dragonsteel("lightning", List.of(IafItems.DRAGONSTEEL_LIGHTNING_HELMET.get(), IafItems.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get(),
                IafItems.DRAGONSTEEL_LIGHTNING_LEGGINGS.get(), IafItems.DRAGONSTEEL_LIGHTNING_BOOTS.get()),
                DragonSteelLightningArmorModel.createMesh(CubeDeformation.NONE, 0, false));
        int count = 12;
        for (var serpent : IafRegistries.SEA_SERPENT_TYPE) {
            Map<String, float[]> mesh = armor(serpent.chestplate.get(), EquipmentSlot.CHEST);
            shoulder(mesh, "right_arm", "shoulderR", 0.5F);
            shoulder(mesh, "left_arm", "shoulderL", 0.5F);
            count++;
        }
        return count;
    }

    private static void dragonsteel(String name, List<Item> items, MeshDefinition original) {
        Map<String, float[]> authored = bounds("/head", original.getRoot().bake(64, 64).getChild("head"));
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            EquipmentSlot slot = slots[i];
            Map<String, float[]> mesh = armor(items.get(i), slot);
            if (slot == EquipmentSlot.HEAD) {
                float[] shell = required(mesh, "/hat");
                near(shell[2], -4.88F, "helmet still has skin clearance");
                for (String visor : List.of("visor1", "visor2"))
                    near(shell[2] - required(mesh, "/head/" + visor)[2], 0.2F, name + " visor front clearance");
                authored.forEach((path, expected) -> {
                    if (path.startsWith("/head/Horn")) {
                        float[] actual = required(mesh, path);
                        for (int axis = 0; axis < 6; axis++) near(actual[axis], expected[axis], name + " horn " + path);
                    }
                });
            } else if (slot == EquipmentSlot.CHEST) {
                shoulder(mesh, "right_arm", "sleeveRight", 0.4F);
                shoulder(mesh, "left_arm", "sleeveLeft", 0.4F);
            } else {
                for (String side : List.of("right", "left")) {
                    float[] leg = required(mesh, "/" + side + "_leg");
                    float[] plate = required(mesh, "/" + side + "_leg/robeLower" + (side.equals("right") ? "Right" : "Left"));
                    near(leg[2] - plate[2], 0.5F, name + " robe front clearance");
                    near(plate[5] - leg[5], 0.5F, name + " robe back clearance");
                }
            }
        }
    }

    private static void shoulder(Map<String, float[]> mesh, String arm, String plate, float clearance) {
        near(required(mesh, "/" + arm)[2] - required(mesh, "/" + arm + "/" + plate)[2], clearance, plate + " front clearance");
    }

    private static Map<String, float[]> armor(Item item, EquipmentSlot slot) {
        HumanoidModel<?> model = IArmorRendererBase.RENDERERS.get(item).getHumanoidArmorModel(null, new ItemStack(item), slot, null);
        if (model == null) throw new AssertionError("Missing armor model for " + item);
        Map<String, float[]> result = new LinkedHashMap<>();
        result.putAll(bounds("/head", model.head));
        result.putAll(bounds("/hat", model.hat));
        result.putAll(bounds("/right_arm", model.rightArm));
        result.putAll(bounds("/left_arm", model.leftArm));
        result.putAll(bounds("/right_leg", model.rightLeg));
        result.putAll(bounds("/left_leg", model.leftLeg));
        return result;
    }

    private static Map<String, float[]> bounds(String prefix, ModelPart root) {
        Map<String, float[]> result = new LinkedHashMap<>();
        root.visit(new PoseStack(), (pose, path, index, cube) -> {
            float[] box = result.computeIfAbsent(prefix + path, key -> new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                    Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY});
            VertexConsumer sink = (VertexConsumer) Proxy.newProxyInstance(VertexConsumer.class.getClassLoader(),
                    new Class<?>[]{VertexConsumer.class}, (proxy, method, args) -> {
                        if (method.getName().equals("addVertex")) {
                            for (int axis = 0; axis < 3; axis++) {
                                float value = ((Number) args[axis]).floatValue() * 16;
                                box[axis] = Math.min(box[axis], value);
                                box[axis + 3] = Math.max(box[axis + 3], value);
                            }
                        }
                        return method.getReturnType() == VertexConsumer.class ? proxy : null;
                    });
            cube.compile(pose, sink, 0, 0, -1);
        });
        return result;
    }

    private static float[] required(Map<String, float[]> mesh, String path) {
        float[] box = mesh.get(path);
        if (box == null) throw new AssertionError("Missing armor geometry " + path);
        return box;
    }

    private static void near(float actual, float expected, String message) {
        if (!Float.isFinite(actual) || Math.abs(actual - expected) > 0.001F)
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
    }
}
