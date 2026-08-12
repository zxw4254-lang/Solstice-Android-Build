package com.mlxingc.solstice;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Main extends Activity {

    private GLSurfaceView glView;
    private MaterialButton btnNotify;
    private Slider sliderDensity;
    private Slider sliderGlowRadius;
    private Slider sliderRightOffset;
    private SwitchMaterial swGlow;
    private SwitchMaterial swTextShadow;
    private Slider sliderShadowOffset;
    private Slider sliderFontSize;
    private MaterialButton btnDisplay;
    private TextInputEditText etModule;
    private MaterialButton btnAdd;
    private MaterialButton btnDel;
    private TextInputEditText etSetWatermarkText;
    private MaterialButton btnSetWatermarkText;

    private final String[] displayNames = {"Outline", "Bar", "Split", "None"};
    private int displayIndex = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTitle("Solstice");

        SolsticeUI.Init();

        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        glView = new GLSurfaceView(this);
        glView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        glView.setEGLContextClientVersion(3);
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        glView.setZOrderOnTop(true);
        glView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                SolsticeUI.Init();
            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                SolsticeUI.Resize(width, height);
            }

            @Override
            public void onDrawFrame(GL10 gl) {
                SolsticeUI.Render();
            }
        });
        root.addView(glView);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        btnNotify = new MaterialButton(this);
        btnNotify.setText("notify");
        btnNotify.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btnNotify.setOnClickListener(v -> SolsticeUI.Notify("哈哈哈哈哈哈呀呵呵呵呵呵 abcdefghijklmno", 0, 3.0f));
        layout.addView(btnNotify);

        layout.addView(createLabel("发光强度"));
        sliderDensity = new Slider(this);
        sliderDensity.setValueFrom(1f);
        sliderDensity.setValueTo(10f);
        sliderDensity.setStepSize(1f);
        sliderDensity.setValue(3f);
        sliderDensity.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        sliderDensity.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                SolsticeUI.SetGlowDensity(Math.round(value));
            }
        });
        layout.addView(sliderDensity);

        layout.addView(createLabel("发光半径"));
        sliderGlowRadius = new Slider(this);
        sliderGlowRadius.setValueFrom(0f);
        sliderGlowRadius.setValueTo(10f);
        sliderGlowRadius.setStepSize(0.1f);
        sliderGlowRadius.setValue(1.5f);
        sliderGlowRadius.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        sliderGlowRadius.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                SolsticeUI.SetGlowRadius(value);
            }
        });
        layout.addView(sliderGlowRadius);

        layout.addView(createLabel("控件 RightOffset"));
        sliderRightOffset = new Slider(this);
        sliderRightOffset.setValueFrom(0f);
        sliderRightOffset.setValueTo(200f);
        sliderRightOffset.setStepSize(1f);
        sliderRightOffset.setValue(20f);
        sliderRightOffset.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        sliderRightOffset.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                SolsticeUI.SetRightOffset(value);
            }
        });
        layout.addView(sliderRightOffset);
        
        layout.addView(createLabel("字体大小"));
        sliderFontSize = new Slider(this);
        sliderFontSize.setValueFrom(20f);
        sliderFontSize.setValueTo(50f);
        sliderFontSize.setStepSize(0.3f);
        sliderFontSize.setValue(20f);
        sliderFontSize.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        sliderFontSize.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                SolsticeUI.SetFontSize(value);
            }
        });
        layout.addView(sliderFontSize);
        
        
        swGlow = new SwitchMaterial(this);
        swGlow.setText("Glow");
        swGlow.setChecked(true);
        swGlow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        swGlow.setOnClickListener(v -> SolsticeUI.SetGlow(swGlow.isChecked()));
        layout.addView(swGlow);

        swTextShadow = new SwitchMaterial(this);
        swTextShadow.setText("Text Shadow");
        swTextShadow.setChecked(true);
        swTextShadow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        swTextShadow.setOnCheckedChangeListener((button, isChecked) -> SolsticeUI.SetTextShadow(isChecked));
        layout.addView(swTextShadow);

        sliderShadowOffset = new Slider(this);
        sliderShadowOffset.setValueFrom(0f);
        sliderShadowOffset.setValueTo(3f);
        sliderShadowOffset.setStepSize(0.01f);
        sliderShadowOffset.setValue(1f);
        sliderShadowOffset.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        sliderShadowOffset.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                SolsticeUI.SetShadowOffset(value);
            }
        });
        layout.addView(sliderShadowOffset);

        btnDisplay = new MaterialButton(this);
        btnDisplay.setText("Display: Split");
        btnDisplay.setTag("2");
        btnDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btnDisplay.setOnClickListener(v -> {
            displayIndex = (displayIndex + 1) % 4;
            btnDisplay.setTag(String.valueOf(displayIndex));
            btnDisplay.setText("Display: " + displayNames[displayIndex]);
            SolsticeUI.SetDisplay(displayIndex);
        });
        layout.addView(btnDisplay);

        TextInputLayout tilModule = new TextInputLayout(this);
        tilModule.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tilModule.setHint("Module name");
        etModule = new TextInputEditText(this);
        etModule.setText("KillAura");
        tilModule.addView(etModule);
        layout.addView(tilModule);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        btnAdd = new MaterialButton(this);
        btnAdd.setText("add");
        btnAdd.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btnAdd.setOnClickListener(v -> {
            String name = etModule.getText().toString();
            if (!name.isEmpty()) {
                SolsticeUI.ToggleModule(name, "", true);
            }
        });
        btnRow.addView(btnAdd);

        btnDel = new MaterialButton(this);
        btnDel.setText("del");
        btnDel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btnDel.setOnClickListener(v -> SolsticeUI.ToggleModule(etModule.getText().toString(), "", false));
        btnRow.addView(btnDel);

        layout.addView(btnRow);

        TextInputLayout tilWatermark = new TextInputLayout(this);
        tilWatermark.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        tilWatermark.setHint("大文字。");
        etSetWatermarkText = new TextInputEditText(this);
        etSetWatermarkText.setText("Solstice V4");
        tilWatermark.addView(etSetWatermarkText);
        layout.addView(tilWatermark);

        btnSetWatermarkText = new MaterialButton(this);
        btnSetWatermarkText.setText("设置。");
        btnSetWatermarkText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        btnSetWatermarkText.setOnClickListener(v -> SolsticeUI.SetWatermarkText(etSetWatermarkText.getText().toString()));
        layout.addView(btnSetWatermarkText);

        scrollView.addView(layout);
        root.addView(scrollView);
        setContentView(root);

        initDefaultModules();
    }

    private TextView createLabel(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return tv;
    }

    private void initDefaultModules() {
        SolsticeUI.ToggleModule("VAPE V4", "User Mlx1nGCNya", true);
        SolsticeUI.ToggleModule("KillAura", "Single", true);
        SolsticeUI.ToggleModule("Speed", "Strafe", true);
        SolsticeUI.ToggleModule("Fly", "Vanilla", false);
        SolsticeUI.ToggleModule("Scaffold", "Normal", true);
        SolsticeUI.ToggleModule("NoFall", "", true);
        SolsticeUI.ToggleModule("Velocity", "", false);
        SolsticeUI.ToggleModule("ChestStealer", "Silent", true);
        SolsticeUI.ToggleModule("AutoArmor", "", true);
        SolsticeUI.ToggleModule("Sprint", "", true);
        SolsticeUI.ToggleModule("InventoryMove", "", true);
        SolsticeUI.ToggleModule("Criticals", "Packet", true);
        SolsticeUI.ToggleModule("AntiBot", "", true);
        SolsticeUI.ToggleModule("Teams", "", true);
        SolsticeUI.ToggleModule("ESP", "2D", true);
        SolsticeUI.ToggleModule("Tracers", "", false);
        SolsticeUI.ToggleModule("Arraylist", "Split", true);
    }
}
