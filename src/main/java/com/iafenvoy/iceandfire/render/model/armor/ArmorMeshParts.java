package com.iafenvoy.iceandfire.render.model.armor;

import com.iafenvoy.uranus.client.render.armor.ArmorModelBase;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.Set;

/**
 * Minecraft 26.1 moved {@code hat} under {@code head} and renders a full humanoid
 * armor mesh unless unused cubes are stripped per slot (vanilla
 * {@code retainPartsAndChildren}). Custom IAF models still describe the whole
 * body, so a helmet would otherwise draw chest/legs/boots as well.
 */
public final class ArmorMeshParts {
    /**
     * Same values as Uranus {@code ArmorModelBase} inner/outer offsets.
     */
    private static final float INNER_OFFSET = 0.38F;
    private static final float OUTER_OFFSET = 0.45F;

    private ArmorMeshParts() {
    }

    public static CubeDeformation deformation(boolean inner) {
        return CubeDeformation.NONE.extend(inner ? INNER_OFFSET : OUTER_OFFSET);
    }

    static PartDefinition helmetDecor(PartDefinition root) {
        PartDefinition head = root.getChild("head");
        if (head != null && head.getChild("hat") == null) {
            head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        }
        return head != null ? head : root;
    }

    public static ArmorModelBase bakeSlot(MeshDefinition mesh, EquipmentSlot slot) {
        PartDefinition root = mesh.getRoot();
        switch (slot) {
            case HEAD -> root.retainPartsAndChildren(Set.of("head"));
            case CHEST -> root.retainPartsAndChildren(Set.of("body", "left_arm", "right_arm"));
            case LEGS -> root.retainPartsAndChildren(Set.of("body", "left_leg", "right_leg"));
            case FEET -> root.retainPartsAndChildren(Set.of("left_leg", "right_leg"));
            default -> {
            }
        }
        return new ArmorModelBase(root.bake(64, 64));
    }
}
