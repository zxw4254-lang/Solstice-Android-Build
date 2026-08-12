package com.mlxingc.solstice;

public class SolsticeUI {
    static {
        System.loadLibrary("solstice");
    }

    public static native void Init();
    public static native void Resize(int width, int height);
    public static native void Render();
    public static native void Notify(String text, int type, float duration);
    public static native void SetGlow(boolean enabled);
    public static native void SetGlowDensity(int density);
    public static native void SetGlowRadius(float radius);
    public static native void SetRightOffset(float offset);
    public static native void SetDisplay(int mode);
    public static native void SetTextShadow(boolean enabled);
    public static native void SetShadowOffset(float offset);
    public static native void SetWatermarkText(String text);
    public static native void ToggleModule(String name, String mode, boolean enabled);
    public static native void SetFontSize(float size);
}
