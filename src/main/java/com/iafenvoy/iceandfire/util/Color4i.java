package com.iafenvoy.iceandfire.util;

import java.awt.*;

public record Color4i(int r, int g, int b, int a) {
    private static final float MUL = 255f;

    public Color4i(double r, double g, double b, double a) {
        this((int) (r * MUL), (int) (g * MUL), (int) (b * MUL), (int) (a * MUL));
    }

    public Color4i(int value) {
        this(value >> 24 & 0xFF, value >> 16 & 0xFF, value >> 8 & 0xFF, value & 0xFF);
    }

    public static Color4i copy(Color4i another) {
        return new Color4i(another.r, another.g, another.b, another.a);
    }

    public static Color4i copy(Color4i another, int alpha) {
        return new Color4i(another.r, another.g, another.b, alpha);
    }

    public static Color4i fromHSV(float h, float s, float v) {
        Color color = Color.getHSBColor(h, s, v);
        return new Color4i(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public float getR() {
        return this.r / MUL;
    }

    public float getG() {
        return this.g / MUL;
    }

    public float getB() {
        return this.b / MUL;
    }

    public float getA() {
        return this.a / MUL;
    }

    public int getIntValue() {
        return this.a << 24 | this.r << 16 | this.g << 8 | this.b;
    }
}
