package net.minecraft.client.renderer.rendertype;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTypes {
    static final BiFunction<Identifier, Boolean, RenderType> OUTLINE = Util.memoize(
        (texture, cullState) -> RenderType.create(
            "outline",
            RenderSetup.builder(cullState ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL)
                .withTexture("Sampler0", texture)
                .setOutputTarget(OutputTarget.OUTLINE_TARGET)
                .setOutline(RenderSetup.OutlineProperty.IS_OUTLINE)
                .createRenderSetup()
        )
    );
    private static final RenderType SOLID_MOVING_BLOCK = RenderType.create("solid_moving_block", createMovingBlockSetup(RenderPipelines.SOLID_BLOCK, false));
    private static final RenderType CUTOUT_MOVING_BLOCK = RenderType.create("cutout_moving_block", createMovingBlockSetup(RenderPipelines.CUTOUT_BLOCK, false));
    private static final RenderType TRANSLUCENT_MOVING_BLOCK = RenderType.create(
        "translucent_moving_block", createMovingBlockSetup(RenderPipelines.TRANSLUCENT_BLOCK, true)
    );
    private static final Function<Identifier, RenderType> ARMOR_CUTOUT_NO_CULL = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ARMOR_CUTOUT_NO_CULL)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("armor_cutout_no_cull", state);
        }
    );
    private static final Function<Identifier, RenderType> ARMOR_TRANSLUCENT = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ARMOR_TRANSLUCENT)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("armor_translucent", state);
        }
    );
    private static final Function<Identifier, RenderType> ENTITY_SOLID = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_SOLID)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("entity_solid", state);
        }
    );
    private static final Function<Identifier, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_SOLID_Z_OFFSET_FORWARD)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING_FORWARD)
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("entity_solid_z_offset_forward", state);
        }
    );
    private static final Function<Identifier, RenderType> ENTITY_CUTOUT_CULL = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT_CULL)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("entity_cutout_cull", state);
        }
    );
    private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT = Util.memoize(
        (texture, affectsOutline) -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
                .createRenderSetup();
            return RenderType.create("entity_cutout", state);
        }
    );
    private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_CUTOUT_Z_OFFSET = Util.memoize(
        (texture, affectsOutline) -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT_Z_OFFSET)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .affectsCrumbling()
                .setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
                .createRenderSetup();
            return RenderType.create("entity_cutout_z_offset", state);
        }
    );
    private static final BiFunction<Identifier, Identifier, RenderType> ENTITY_CUTOUT_DISSOLVE = Util.memoize(
        (texture, maskTexture) -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT_DISSOLVE)
                .withTexture("Sampler0", texture)
                .withTexture("DissolveMaskSampler", maskTexture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("entity_cutout_dissolve", state);
        }
    );
    private static final Function<Identifier, RenderType> ENTITY_TRANSLUCENT_CULL_ITEM_TARGET = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_CULL)
                .withTexture("Sampler0", texture)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("entity_translucent_cull_item_target", state);
        }
    );
    private static final Function<Identifier, RenderType> ITEM_CUTOUT = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ITEM_CUTOUT)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .affectsCrumbling()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("item_cutout", state);
        }
    );
    private static final Function<Identifier, RenderType> ITEM_TRANSLUCENT = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ITEM_TRANSLUCENT)
                .withTexture("Sampler0", texture)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .useLightmap()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                .createRenderSetup();
            return RenderType.create("item_translucent", state);
        }
    );
    private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(
        (texture, affectsOutline) -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
                .createRenderSetup();
            return RenderType.create("entity_translucent", state);
        }
    );
    private static final BiFunction<Identifier, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
        (texture, affectsOutline) -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE)
                .withTexture("Sampler0", texture)
                .useOverlay()
                .affectsCrumbling()
                .sortOnUpload()
                .setOutline(affectsOutline ? RenderSetup.OutlineProperty.AFFECTS_OUTLINE : RenderSetup.OutlineProperty.NONE)
                .createRenderSetup();
            return RenderType.create("entity_translucent_emissive", state);
        }
    );
    private static final Function<Identifier, RenderType> END_CRYSTAL_BEAM = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.END_CRYSTAL_BEAM)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .setOutline(RenderSetup.OutlineProperty.NONE)
                .createRenderSetup();
            return RenderType.create("end_crystal_beam", state);
        }
    );
    private static final BiFunction<Identifier, Boolean, RenderType> BEACON_BEAM = Util.memoize(
        (texture, translucent) -> {
            RenderSetup state = RenderSetup.builder(translucent ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE)
                .withTexture("Sampler0", texture)
                .sortOnUpload()
                .createRenderSetup();
            return RenderType.create("beacon_beam", state);
        }
    );
    private static final Function<Identifier, RenderType> BANNER_PATTERN = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.BANNER_PATTERN)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .sortOnUpload()
                .createRenderSetup();
            return RenderType.create("banner_pattern", state);
        }
    );
    private static final Function<Identifier, RenderType> ENTITY_SHADOW = Util.memoize(
        texture -> {
            RenderSetup state = RenderSetup.builder(RenderPipelines.ENTITY_SHADOW)
                .withTexture("Sampler0", texture)
                .useLightmap()
                .useOverlay()
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .createRenderSetup();
            return RenderType.create("entity_shadow", state);
        }
    );
    private static final Function<Identifier, RenderType> EYES = Util.memoize(
        texture -> RenderType.create("eyes", RenderSetup.builder(RenderPipelines.EYES).withTexture("Sampler0", texture).sortOnUpload().createRenderSetup())
    );
    private static final RenderType LEASH = RenderType.create("leash", RenderSetup.builder(RenderPipelines.LEASH).useLightmap().createRenderSetup());
    private static final RenderType WATER_MASK = RenderType.create("water_mask", RenderSetup.builder(RenderPipelines.WATER_MASK).createRenderSetup());
    private static final RenderType ARMOR_ENTITY_GLINT = RenderType.create(
        "armor_entity_glint",
        RenderSetup.builder(RenderPipelines.GLINT)
            .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ARMOR)
            .setTextureTransform(TextureTransform.ARMOR_ENTITY_GLINT_TEXTURING)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup()
    );
    private static final RenderType GLINT_TRANSLUCENT = RenderType.create(
        "glint_translucent",
        RenderSetup.builder(RenderPipelines.GLINT)
            .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
            .setTextureTransform(TextureTransform.GLINT_TEXTURING)
            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
            .createRenderSetup()
    );
    private static final RenderType GLINT = RenderType.create(
        "glint",
        RenderSetup.builder(RenderPipelines.GLINT)
            .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
            .setTextureTransform(TextureTransform.GLINT_TEXTURING)
            .createRenderSetup()
    );
    private static final RenderType ENTITY_GLINT = RenderType.create(
        "entity_glint",
        RenderSetup.builder(RenderPipelines.GLINT)
            .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
            .setTextureTransform(TextureTransform.ENTITY_GLINT_TEXTURING)
            .createRenderSetup()
    );
    private static final Function<Identifier, RenderType> CRUMBLING = Util.memoize(
        texture -> RenderType.create(
            "crumbling", RenderSetup.builder(RenderPipelines.CRUMBLING).withTexture("Sampler0", texture).sortOnUpload().createRenderSetup()
        )
    );
    private static final Function<Identifier, RenderType> TEXT = Util.memoize(
        texture -> RenderType.create(
            "text", RenderSetup.builder(RenderPipelines.TEXT).withTexture("Sampler0", texture).useLightmap().bufferSize(786432).createRenderSetup()
        )
    );
    private static final RenderType TEXT_BACKGROUND = RenderType.create(
        "text_background", RenderSetup.builder(RenderPipelines.TEXT_BACKGROUND).useLightmap().sortOnUpload().createRenderSetup()
    );
    private static final Function<Identifier, RenderType> TEXT_INTENSITY = Util.memoize(
        texture -> RenderType.create(
            "text_intensity",
            RenderSetup.builder(RenderPipelines.TEXT_INTENSITY).withTexture("Sampler0", texture).useLightmap().bufferSize(786432).createRenderSetup()
        )
    );
    private static final Function<Identifier, RenderType> TEXT_POLYGON_OFFSET = Util.memoize(
        texture -> RenderType.create(
            "text_polygon_offset",
            RenderSetup.builder(RenderPipelines.TEXT_POLYGON_OFFSET).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()
        )
    );
    private static final Function<Identifier, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(
        texture -> RenderType.create(
            "text_intensity_polygon_offset",
            RenderSetup.builder(RenderPipelines.TEXT_INTENSITY).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()
        )
    );
    private static final Function<Identifier, RenderType> TEXT_SEE_THROUGH = Util.memoize(
        texture -> RenderType.create(
            "text_see_through", RenderSetup.builder(RenderPipelines.TEXT_SEE_THROUGH).withTexture("Sampler0", texture).useLightmap().createRenderSetup()
        )
    );
    private static final RenderType TEXT_BACKGROUND_SEE_THROUGH = RenderType.create(
        "text_background_see_through", RenderSetup.builder(RenderPipelines.TEXT_BACKGROUND_SEE_THROUGH).useLightmap().sortOnUpload().createRenderSetup()
    );
    private static final Function<Identifier, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(
        texture -> RenderType.create(
            "text_intensity_see_through",
            RenderSetup.builder(RenderPipelines.TEXT_INTENSITY_SEE_THROUGH).withTexture("Sampler0", texture).useLightmap().sortOnUpload().createRenderSetup()
        )
    );
    private static final RenderType LIGHTNING = RenderType.create(
        "lightning", RenderSetup.builder(RenderPipelines.LIGHTNING).setOutputTarget(OutputTarget.WEATHER_TARGET).sortOnUpload().createRenderSetup()
    );
    private static final RenderType DRAGON_RAYS = RenderType.create("dragon_rays", RenderSetup.builder(RenderPipelines.DRAGON_RAYS).createRenderSetup());
    private static final RenderType DRAGON_RAYS_DEPTH = RenderType.create(
        "dragon_rays_depth", RenderSetup.builder(RenderPipelines.DRAGON_RAYS_DEPTH).createRenderSetup()
    );
    private static final RenderType END_PORTAL = RenderType.create(
        "end_portal",
        RenderSetup.builder(RenderPipelines.END_PORTAL)
            .withTexture("Sampler0", AbstractEndPortalRenderer.END_SKY_LOCATION)
            .withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION)
            .createRenderSetup()
    );
    private static final RenderType END_GATEWAY = RenderType.create(
        "end_gateway",
        RenderSetup.builder(RenderPipelines.END_GATEWAY)
            .withTexture("Sampler0", AbstractEndPortalRenderer.END_SKY_LOCATION)
            .withTexture("Sampler1", AbstractEndPortalRenderer.END_PORTAL_LOCATION)
            .createRenderSetup()
    );
    public static final RenderType LINES = RenderType.create(
        "lines",
        RenderSetup.builder(RenderPipelines.LINES)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
            .createRenderSetup()
    );
    public static final RenderType LINES_TRANSLUCENT = RenderType.create(
        "lines_translucent",
        RenderSetup.builder(RenderPipelines.LINES_TRANSLUCENT)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
            .createRenderSetup()
    );
    public static final RenderType SECONDARY_BLOCK_OUTLINE = RenderType.create(
        "secondary_block_outline",
        RenderSetup.builder(RenderPipelines.SECONDARY_BLOCK_OUTLINE)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
            .createRenderSetup()
    );
    private static final RenderType DEBUG_FILLED_BOX = RenderType.create(
        "debug_filled_box",
        RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX).sortOnUpload().setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING).createRenderSetup()
    );
    private static final RenderType DEBUG_POINT = RenderType.create("debug_point", RenderSetup.builder(RenderPipelines.DEBUG_POINTS).createRenderSetup());
    private static final RenderType DEBUG_QUADS = RenderType.create(
        "debug_quads", RenderSetup.builder(RenderPipelines.DEBUG_QUADS).sortOnUpload().createRenderSetup()
    );
    private static final RenderType DEBUG_TRIANGLE_FAN = RenderType.create(
        "debug_triangle_fan", RenderSetup.builder(RenderPipelines.DEBUG_TRIANGLE_FAN).sortOnUpload().createRenderSetup()
    );
    private static final Function<Identifier, RenderType> BLOCK_SCREEN_EFFECT = Util.memoize(
        texture -> RenderType.create(
            "block_screen_effect", RenderSetup.builder(RenderPipelines.BLOCK_SCREEN_EFFECT).withTexture("Sampler0", texture).createRenderSetup()
        )
    );
    private static final Function<Identifier, RenderType> FIRE_SCREEN_EFFECT = Util.memoize(
        texture -> RenderType.create(
            "fire_screen_effect", RenderSetup.builder(RenderPipelines.FIRE_SCREEN_EFFECT).withTexture("Sampler0", texture).createRenderSetup()
        )
    );

    private static RenderSetup createMovingBlockSetup(RenderPipeline pipeline, boolean translucent) {
        RenderSetup.RenderSetupBuilder setup = RenderSetup.builder(pipeline)
            .useLightmap()
            .withTexture(
                "Sampler0",
                TextureAtlas.LOCATION_BLOCKS,
                () -> RenderSystem.getSamplerCache()
                    .getSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.NEAREST, true)
            )
            .affectsCrumbling()
            .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE);
        if (translucent) {
            setup.sortOnUpload();
            setup.setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET);
        }

        return setup.createRenderSetup();
    }

    public static RenderType solidMovingBlock() {
        return SOLID_MOVING_BLOCK;
    }

    public static RenderType cutoutMovingBlock() {
        return CUTOUT_MOVING_BLOCK;
    }

    public static RenderType translucentMovingBlock() {
        return TRANSLUCENT_MOVING_BLOCK;
    }

    public static RenderType armorCutoutNoCull(Identifier texture) {
        return ARMOR_CUTOUT_NO_CULL.apply(texture);
    }

    public static RenderType createArmorDecalCutoutNoCull(Identifier texture) {
        RenderSetup state = RenderSetup.builder(RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL)
            .withTexture("Sampler0", texture)
            .useLightmap()
            .useOverlay()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .affectsCrumbling()
            .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
            .createRenderSetup();
        return RenderType.create("armor_decal_cutout_no_cull", state);
    }

    public static RenderType armorTranslucent(Identifier texture) {
        return ARMOR_TRANSLUCENT.apply(texture);
    }

    public static RenderType entitySolid(Identifier texture) {
        return ENTITY_SOLID.apply(texture);
    }

    public static RenderType entitySolidZOffsetForward(Identifier texture) {
        return ENTITY_SOLID_Z_OFFSET_FORWARD.apply(texture);
    }

    public static RenderType entityCutoutCull(Identifier texture) {
        return ENTITY_CUTOUT_CULL.apply(texture);
    }

    public static RenderType entityCutout(Identifier texture, boolean affectsOutline) {
        return ENTITY_CUTOUT.apply(texture, affectsOutline);
    }

    public static RenderType entityCutout(Identifier texture) {
        return entityCutout(texture, true);
    }

    public static RenderType entityCutoutZOffset(Identifier texture, boolean affectsOutline) {
        return ENTITY_CUTOUT_Z_OFFSET.apply(texture, affectsOutline);
    }

    public static RenderType entityCutoutZOffset(Identifier texture) {
        return entityCutoutZOffset(texture, true);
    }

    public static RenderType entityCutoutDissolve(Identifier texture, Identifier maskTexture) {
        return ENTITY_CUTOUT_DISSOLVE.apply(texture, maskTexture);
    }

    public static RenderType entityTranslucentCullItemTarget(Identifier texture) {
        return ENTITY_TRANSLUCENT_CULL_ITEM_TARGET.apply(texture);
    }

    public static RenderType itemCutout(Identifier texture) {
        return ITEM_CUTOUT.apply(texture);
    }

    public static RenderType itemTranslucent(Identifier texture) {
        return ITEM_TRANSLUCENT.apply(texture);
    }

    public static RenderType entityTranslucent(Identifier texture, boolean affectsOutline) {
        return ENTITY_TRANSLUCENT.apply(texture, affectsOutline);
    }

    public static RenderType entityTranslucent(Identifier texture) {
        return entityTranslucent(texture, true);
    }

    public static RenderType entityTranslucentEmissive(Identifier texture, boolean affectsOutline) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, affectsOutline);
    }

    public static RenderType entityTranslucentEmissive(Identifier texture) {
        return entityTranslucentEmissive(texture, true);
    }

    public static RenderType endCrystalBeam(Identifier texture) {
        return END_CRYSTAL_BEAM.apply(texture);
    }

    public static RenderType beaconBeam(Identifier texture, boolean translucent) {
        return BEACON_BEAM.apply(texture, translucent);
    }

    public static RenderType bannerPattern(Identifier texture) {
        return BANNER_PATTERN.apply(texture);
    }

    public static RenderType entityShadow(Identifier texture) {
        return ENTITY_SHADOW.apply(texture);
    }

    public static RenderType eyes(Identifier texture) {
        return EYES.apply(texture);
    }

    public static RenderType breezeEyes(Identifier texture) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(texture, false);
    }

    public static RenderType breezeWind(Identifier texture, float uOffset, float vOffset) {
        return RenderType.create(
            "breeze_wind",
            RenderSetup.builder(RenderPipelines.BREEZE_WIND)
                .withTexture("Sampler0", texture)
                .setTextureTransform(new TextureTransform.OffsetTextureTransform(uOffset, vOffset))
                .useLightmap()
                .sortOnUpload()
                .createRenderSetup()
        );
    }

    public static RenderType energySwirl(Identifier texture, float uOffset, float vOffset) {
        return RenderType.create(
            "energy_swirl",
            RenderSetup.builder(RenderPipelines.ENERGY_SWIRL)
                .withTexture("Sampler0", texture)
                .setTextureTransform(new TextureTransform.OffsetTextureTransform(uOffset, vOffset))
                .useLightmap()
                .useOverlay()
                .sortOnUpload()
                .createRenderSetup()
        );
    }

    public static RenderType leash() {
        return LEASH;
    }

    public static RenderType waterMask() {
        return WATER_MASK;
    }

    public static RenderType outline(Identifier texture) {
        return OUTLINE.apply(texture, false);
    }

    public static RenderType armorEntityGlint() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderType glintTranslucent() {
        return GLINT_TRANSLUCENT;
    }

    public static RenderType glint() {
        return GLINT;
    }

    public static RenderType entityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderType crumbling(Identifier texture) {
        return CRUMBLING.apply(texture);
    }

    public static RenderType text(Identifier texture) {
        return TEXT.apply(texture);
    }

    public static RenderType textBackground() {
        return TEXT_BACKGROUND;
    }

    public static RenderType textIntensity(Identifier texture) {
        return TEXT_INTENSITY.apply(texture);
    }

    public static RenderType textPolygonOffset(Identifier texture) {
        return TEXT_POLYGON_OFFSET.apply(texture);
    }

    public static RenderType textIntensityPolygonOffset(Identifier texture) {
        return TEXT_INTENSITY_POLYGON_OFFSET.apply(texture);
    }

    public static RenderType textSeeThrough(Identifier texture) {
        return TEXT_SEE_THROUGH.apply(texture);
    }

    public static RenderType textBackgroundSeeThrough() {
        return TEXT_BACKGROUND_SEE_THROUGH;
    }

    public static RenderType textIntensitySeeThrough(Identifier texture) {
        return TEXT_INTENSITY_SEE_THROUGH.apply(texture);
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }

    public static RenderType dragonRays() {
        return DRAGON_RAYS;
    }

    public static RenderType dragonRaysDepth() {
        return DRAGON_RAYS_DEPTH;
    }

    public static RenderType endPortal() {
        return END_PORTAL;
    }

    public static RenderType endGateway() {
        return END_GATEWAY;
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType linesTranslucent() {
        return LINES_TRANSLUCENT;
    }

    public static RenderType secondaryBlockOutline() {
        return SECONDARY_BLOCK_OUTLINE;
    }

    public static RenderType debugFilledBox() {
        return DEBUG_FILLED_BOX;
    }

    public static RenderType debugPoint() {
        return DEBUG_POINT;
    }

    public static RenderType debugQuads() {
        return DEBUG_QUADS;
    }

    public static RenderType debugTriangleFan() {
        return DEBUG_TRIANGLE_FAN;
    }

    public static RenderType blockScreenEffect(Identifier texture) {
        return BLOCK_SCREEN_EFFECT.apply(texture);
    }

    public static RenderType fireScreenEffect(Identifier texture) {
        return FIRE_SCREEN_EFFECT.apply(texture);
    }
}
