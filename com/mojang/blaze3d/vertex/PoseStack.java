package com.mojang.blaze3d.vertex;

import com.mojang.math.MatrixUtil;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@OnlyIn(Dist.CLIENT)
public class PoseStack {
    private final List<PoseStack.Pose> poses = new ArrayList<>(16);
    private int lastIndex;

    public PoseStack() {
        this.poses.add(new PoseStack.Pose());
    }

    public void translate(double xo, double yo, double zo) {
        this.translate((float)xo, (float)yo, (float)zo);
    }

    public void translate(float xo, float yo, float zo) {
        this.last().translate(xo, yo, zo);
    }

    public void translate(Vec3 offset) {
        this.translate(offset.x, offset.y, offset.z);
    }

    public void scale(float xScale, float yScale, float zScale) {
        this.last().scale(xScale, yScale, zScale);
    }

    public void mulPose(Quaternionfc by) {
        this.last().rotate(by);
    }

    public void rotateAround(Quaternionfc rotation, float pivotX, float pivotY, float pivotZ) {
        this.last().rotateAround(rotation, pivotX, pivotY, pivotZ);
    }

    public void pushPose() {
        PoseStack.Pose lastPose = this.last();
        this.lastIndex++;
        if (this.lastIndex >= this.poses.size()) {
            this.poses.add(lastPose.copy());
        } else {
            this.poses.get(this.lastIndex).set(lastPose);
        }
    }

    public void popPose() {
        if (this.lastIndex == 0) {
            throw new NoSuchElementException();
        } else {
            this.lastIndex--;
        }
    }

    public PoseStack.Pose last() {
        return this.poses.get(this.lastIndex);
    }

    public boolean isEmpty() {
        return this.lastIndex == 0;
    }

    public void setIdentity() {
        this.last().setIdentity();
    }

    public void mulPose(Matrix4fc matrix) {
        this.last().mulPose(matrix);
    }

    public void mulPose(Transformation matrix) {
        this.last().mulPose(matrix);
    }

    @OnlyIn(Dist.CLIENT)
    public static final class Pose {
        private final Matrix4f pose = new Matrix4f();
        private final Matrix3f normal = new Matrix3f();
        private boolean trustedNormals = true;

        private void computeNormalMatrix() {
            this.normal.set(this.pose).invert().transpose();
            this.trustedNormals = false;
        }

        public void set(PoseStack.Pose pose) {
            this.pose.set(pose.pose);
            this.normal.set(pose.normal);
            this.trustedNormals = pose.trustedNormals;
        }

        public Matrix4f pose() {
            return this.pose;
        }

        public Matrix3f normal() {
            return this.normal;
        }

        public Vector3f transformNormal(Vector3fc normal, Vector3f destination) {
            return this.transformNormal(normal.x(), normal.y(), normal.z(), destination);
        }

        public Vector3f transformNormal(float x, float y, float z, Vector3f destination) {
            Vector3f result = this.normal.transform(x, y, z, destination);
            return this.trustedNormals ? result : result.normalize();
        }

        public Matrix4f translate(float xo, float yo, float zo) {
            return this.pose.translate(xo, yo, zo);
        }

        public void scale(float xScale, float yScale, float zScale) {
            this.pose.scale(xScale, yScale, zScale);
            if (Math.abs(xScale) == Math.abs(yScale) && Math.abs(yScale) == Math.abs(zScale)) {
                if (xScale < 0.0F || yScale < 0.0F || zScale < 0.0F) {
                    this.normal.scale(Math.signum(xScale), Math.signum(yScale), Math.signum(zScale));
                }
            } else {
                this.normal.scale(1.0F / xScale, 1.0F / yScale, 1.0F / zScale);
                this.trustedNormals = false;
            }
        }

        public void rotate(Quaternionfc by) {
            this.pose.rotate(by);
            this.normal.rotate(by);
        }

        public void rotateAround(Quaternionfc rotation, float pivotX, float pivotY, float pivotZ) {
            this.pose.rotateAround(rotation, pivotX, pivotY, pivotZ);
            this.normal.rotate(rotation);
        }

        public void setIdentity() {
            this.pose.identity();
            this.normal.identity();
            this.trustedNormals = true;
        }

        public void mulPose(Matrix4fc matrix) {
            this.pose.mul(matrix);
            if (!MatrixUtil.isPureTranslation(matrix)) {
                if (MatrixUtil.checkPropertyRaw(matrix, 16)) {
                    this.normal.mul(new Matrix3f(matrix));
                } else {
                    this.computeNormalMatrix();
                }
            }
        }

        public void mulPose(Transformation transformation) {
            this.mulPose(transformation.getMatrix());
        }

        public PoseStack.Pose copy() {
            PoseStack.Pose pose = new PoseStack.Pose();
            pose.set(this);
            return pose;
        }
    }
}
