package com.iafenvoy.iceandfire.regression;

import com.iafenvoy.iceandfire.registry.IafItems;
import com.iafenvoy.iceandfire.registry.IafRegistries;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelFireArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelIceArmorModel;
import com.iafenvoy.iceandfire.render.model.armor.DragonSteelLightningArmorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Checks actual registered client armor models, including slot clipping and inflated polygon vertices. */
final class ArmorModelRegression {
    static void run(List<String> passed) {
        dragonsteel("fire", List.of(IafItems.DRAGONSTEEL_FIRE_HELMET.get(), IafItems.DRAGONSTEEL_FIRE_CHESTPLATE.get(),
                IafItems.DRAGONSTEEL_FIRE_LEGGINGS.get(), IafItems.DRAGONSTEEL_FIRE_BOOTS.get()),
                DragonSteelFireArmorModel.createMesh(CubeDeformation.NONE, 0, false), passed);
        dragonsteel("ice", List.of(IafItems.DRAGONSTEEL_ICE_HELMET.get(), IafItems.DRAGONSTEEL_ICE_CHESTPLATE.get(),
                IafItems.DRAGONSTEEL_ICE_LEGGINGS.get(), IafItems.DRAGONSTEEL_ICE_BOOTS.get()),
                DragonSteelIceArmorModel.createMesh(CubeDeformation.NONE, 0, false), passed);
        dragonsteel("lightning", List.of(IafItems.DRAGONSTEEL_LIGHTNING_HELMET.get(), IafItems.DRAGONSTEEL_LIGHTNING_CHESTPLATE.get(),
                IafItems.DRAGONSTEEL_LIGHTNING_LEGGINGS.get(), IafItems.DRAGONSTEEL_LIGHTNING_BOOTS.get()),
                DragonSteelLightningArmorModel.createMesh(CubeDeformation.NONE, 0, false), passed);
        for (var serpent : IafRegistries.SEA_SERPENT_TYPE) {
            Map<String, float[]> mesh = armor(serpent.chestplate.get(), EquipmentSlot.CHEST);
            shoulder(mesh, "right_arm", "shoulderR", 0.5F);
            shoulder(mesh, "left_arm", "shoulderL", 0.5F);
            passed.add("serpent_shoulder_" + serpent.getName());
        }
    }

    private static void dragonsteel(String name, List<Item> items, MeshDefinition original, List<String> passed) {
        Map<String, float[]> authored = bounds(original.getRoot().bake(64, 64));
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < slots.length; i++) {
            EquipmentSlot slot = slots[i];
            Map<String, float[]> mesh = armor(items.get(i), slot);
            if (slot == EquipmentSlot.HEAD) {
                float[] shell = required(mesh, "/head/hat");
                near(shell[2], -4.95F, "helmet still has skin clearance");
                for (String visor : List.of("visor1", "visor2")) {
                    float[] plate = required(mesh, "/head/" + visor);
                    near(shell[2] - plate[2], 0.2F, name + " visor front clearance");
                }
                // Horns must not become thicker or move as a side effect of padding the attached plates.
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
            for (String path : mesh.keySet()) {
                boolean expected = switch (slot) {
                    case HEAD -> path.startsWith("/head");
                    case CHEST -> path.startsWith("/body") || path.startsWith("/right_arm") || path.startsWith("/left_arm");
                    default -> path.startsWith("/body") && slot == EquipmentSlot.LEGS
                            || path.startsWith("/right_leg") || path.startsWith("/left_leg");
                };
                if (!expected) throw new AssertionError(name + " " + slot + " rendered extra armor part " + path);
            }
            passed.add("dragonsteel_" + name + "_" + slot.getName());
        }
    }

    private static void shoulder(Map<String, float[]> mesh, String arm, String plate, float clearance) {
        near(required(mesh, "/" + arm)[2] - required(mesh, "/" + arm + "/" + plate)[2], clearance, plate + " front clearance");
    }

    private static Map<String, float[]> armor(Item item, EquipmentSlot slot) {
        Model<?> model = IClientItemExtensions.of(item).getHumanoidArmorModel(new ItemStack(item),
                slot == EquipmentSlot.LEGS ? EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS : EquipmentClientInfo.LayerType.HUMANOID,
                new Model.Simple(new ModelPart(List.of(), Map.of()), net.minecraft.client.renderer.rendertype.RenderTypes::entityCutout));
        return bounds(model.root());
    }

    private static Map<String, float[]> bounds(ModelPart root) {
        Map<String, float[]> result = new LinkedHashMap<>();
        root.visit(new PoseStack(), (pose, path, index, cube) -> {
            float[] box = result.computeIfAbsent(path, key -> new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                    Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY});
            for (var face : cube.polygons) for (var vertex : face.vertices()) {
                Vector3f position = pose.pose().transformPosition(vertex.worldX(), vertex.worldY(), vertex.worldZ(), new Vector3f()).mul(16);
                for (int axis = 0; axis < 3; axis++) {
                    box[axis] = Math.min(box[axis], position.get(axis));
                    box[axis + 3] = Math.max(box[axis + 3], position.get(axis));
                }
            }
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
