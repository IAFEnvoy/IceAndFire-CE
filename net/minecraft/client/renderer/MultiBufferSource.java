package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import java.util.HashMap;
import java.util.Map;
import java.util.SequencedMap;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jspecify.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public interface MultiBufferSource {
    static MultiBufferSource.BufferSource immediate(ByteBufferBuilder buffer) {
        return immediateWithBuffers(Object2ObjectSortedMaps.emptyMap(), buffer);
    }

    static MultiBufferSource.BufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers, ByteBufferBuilder sharedBuffer) {
        return new MultiBufferSource.BufferSource(sharedBuffer, fixedBuffers);
    }

    VertexConsumer getBuffer(final RenderType renderType);

    @OnlyIn(Dist.CLIENT)
    public static class BufferSource implements MultiBufferSource {
        protected final ByteBufferBuilder sharedBuffer;
        protected final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers;
        protected final Map<RenderType, BufferBuilder> startedBuilders = new HashMap<>();
        protected @Nullable RenderType lastSharedType;

        protected BufferSource(ByteBufferBuilder sharedBuffer, SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
            this.sharedBuffer = sharedBuffer;
            this.fixedBuffers = fixedBuffers;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            BufferBuilder builder = this.startedBuilders.get(renderType);
            if (builder != null && !renderType.canConsolidateConsecutiveGeometry()) {
                this.endBatch(renderType, builder);
                builder = null;
            }

            if (builder != null) {
                return builder;
            } else {
                ByteBufferBuilder fixedBuffer = this.fixedBuffers.get(renderType);
                if (fixedBuffer != null) {
                    builder = new BufferBuilder(fixedBuffer, renderType.mode(), renderType.format());
                } else {
                    if (this.lastSharedType != null) {
                        this.endBatch(this.lastSharedType);
                    }

                    builder = new BufferBuilder(this.sharedBuffer, renderType.mode(), renderType.format());
                    this.lastSharedType = renderType;
                }

                this.startedBuilders.put(renderType, builder);
                return builder;
            }
        }

        public void endLastBatch() {
            if (this.lastSharedType != null) {
                this.endBatch(this.lastSharedType);
                this.lastSharedType = null;
            }
        }

        public void endBatch() {
            this.endLastBatch();

            for (RenderType renderType : this.fixedBuffers.keySet()) {
                this.endBatch(renderType);
            }
        }

        public void endBatch(RenderType type) {
            BufferBuilder builder = this.startedBuilders.remove(type);
            if (builder != null) {
                this.endBatch(type, builder);
            }
        }

        private void endBatch(RenderType type, BufferBuilder builder) {
            MeshData mesh = builder.build();
            if (mesh != null) {
                if (type.sortOnUpload()) {
                    ByteBufferBuilder buffer = this.fixedBuffers.getOrDefault(type, this.sharedBuffer);
                    mesh.sortQuads(buffer, RenderSystem.getProjectionType().vertexSorting());
                }

                type.draw(mesh);
            }

            if (type.equals(this.lastSharedType)) {
                this.lastSharedType = null;
            }
        }
    }
}
