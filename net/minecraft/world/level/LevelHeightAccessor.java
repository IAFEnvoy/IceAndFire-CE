package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LevelHeightAccessor {
    int getHeight();

    int getMinY();

    default int getMaxY() {
        return this.getMinY() + this.getHeight() - 1;
    }

    default int getSectionsCount() {
        return this.getMaxSectionY() - this.getMinSectionY() + 1;
    }

    default int getMinSectionY() {
        return SectionPos.blockToSectionCoord(this.getMinY());
    }

    default int getMaxSectionY() {
        return SectionPos.blockToSectionCoord(this.getMaxY());
    }

    default boolean isInsideBuildHeight(BlockPos pos) {
        return this.isInsideBuildHeight(pos.getY());
    }

    default boolean isInsideBuildHeight(int blockY) {
        return blockY >= this.getMinY() && blockY <= this.getMaxY();
    }

    default boolean isOutsideBuildHeight(BlockPos pos) {
        return this.isOutsideBuildHeight(pos.getY());
    }

    default boolean isOutsideBuildHeight(int blockY) {
        return blockY < this.getMinY() || blockY > this.getMaxY();
    }

    default int getSectionIndex(int blockY) {
        return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(blockY));
    }

    default int getSectionIndexFromSectionY(int sectionY) {
        return sectionY - this.getMinSectionY();
    }

    default int getSectionYFromSectionIndex(int sectionIndex) {
        return sectionIndex + this.getMinSectionY();
    }

    static LevelHeightAccessor create(int minY, int height) {
        return new LevelHeightAccessor() {
            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public int getMinY() {
                return minY;
            }
        };
    }
}
