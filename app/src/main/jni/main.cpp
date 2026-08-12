#include <jni.h>
#include <android/log.h>
#include <GLES3/gl3.h>
#include <imgui.h>
#include <imgui_impl_opengl3.h>
#include <chrono>

#include "resources/font.h"

#include "Arraylist.hpp"
#include "Notifications.hpp"

#define LOG_TAG "Arraylist"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

static Arraylist gArraylist;
static Notifications gNotifications;

static bool gContextCreated = false;
static bool gGLInitialized = false;
static std::chrono::steady_clock::time_point gLastTime;

ImFont* gFontDefault = nullptr;
ImFont* gFontLarge   = nullptr;

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_Init(JNIEnv* env, jclass clazz) {
    IMGUI_CHECKVERSION();
    ImGui::CreateContext();
    ImGuiIO& io = ImGui::GetIO();

    ImGui::StyleColorsDark();
    ImGuiStyle& style = ImGui::GetStyle();
    style.WindowRounding = 0.0f;
    style.WindowBorderSize = 0.0f;
    style.WindowPadding = ImVec2(0, 0);

    ImFontConfig cfg;
    cfg.PixelSnapH = false;
    cfg.OversampleH = 1;
    cfg.OversampleV = 1;

    gFontDefault = io.Fonts->AddFontFromMemoryTTF(
        (void*)OPPOSans_H, (int)OPPOSans_H_size, 20.0f, &cfg);
    gFontLarge = io.Fonts->AddFontFromMemoryTTF(
        (void*)OPPOSans_H, (int)OPPOSans_H_size, 100.0f, &cfg);

    io.FontDefault = gFontDefault;
    gContextCreated = true;
    LOGI("ImGui init done");
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_Resize(JNIEnv* env, jclass clazz, jint width, jint height) {
    ImGui::GetIO().DisplaySize = ImVec2((float)width, (float)height);
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_Render(JNIEnv* env, jclass clazz) {
    if (!gContextCreated) return;

    if (!gGLInitialized) {
        ImGui_ImplOpenGL3_Init("#version 300 es");
        gLastTime = std::chrono::steady_clock::now();
        gGLInitialized = true;
        LOGI("ImGui GL init OK");
    }

    auto now = std::chrono::steady_clock::now();
    float deltaTime = std::chrono::duration<float>(now - gLastTime).count();
    gLastTime = now;
    if (deltaTime <= 0.0f || deltaTime > 1.0f) deltaTime = 1.0f / 60.0f;

    ImGui_ImplOpenGL3_NewFrame();
    ImGuiIO& io = ImGui::GetIO();
    io.DeltaTime = deltaTime;
    ImGui::NewFrame();

    ImGui::SetNextWindowPos(ImVec2(0, 0));
    ImGui::SetNextWindowSize(io.DisplaySize);
    ImGui::Begin("Overlay", nullptr,
        ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoResize |
        ImGuiWindowFlags_NoMove | ImGuiWindowFlags_NoScrollbar |
        ImGuiWindowFlags_NoInputs | ImGuiWindowFlags_NoBackground);

    gArraylist.render(ImGui::GetBackgroundDrawList(), io.DisplaySize, deltaTime);
    gNotifications.render(ImGui::GetBackgroundDrawList(), io.DisplaySize, deltaTime);

    ImGui::End();
    ImGui::Render();

    glViewport(0, 0, (int)io.DisplaySize.x, (int)io.DisplaySize.y);
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_Touch(JNIEnv* env, jclass clazz, jfloat x, jfloat y, jint action) {
    ImGuiIO& io = ImGui::GetIO();
    io.MousePos = ImVec2(x, y);
    int masked = action & 0xFF;
    if (masked == 0) io.MouseDown[0] = true;
    else if (masked == 1 || masked == 3) io.MouseDown[0] = false;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_Shutdown(JNIEnv* env, jclass clazz) {
    if (gGLInitialized) {
        ImGui_ImplOpenGL3_Shutdown();
        gGLInitialized = false;
    }
    if (gContextCreated) {
        ImGui::DestroyContext();
        gContextCreated = false;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_Notify(JNIEnv* env, jclass, jstring msg, jint type, jfloat duration) {
    const char* cstr = env->GetStringUTFChars(msg, nullptr);
    gNotifications.add(cstr, (Notification::Type)type, duration);
    env->ReleaseStringUTFChars(msg, cstr);
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetBlurStrength(JNIEnv*, jclass, jfloat v) {
    gArraylist.mBlurStrength = v;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetGlow(JNIEnv*, jclass, jboolean v) {
    gArraylist.mGlow = v;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetDisplay(JNIEnv*, jclass, jint v) {
    gArraylist.mDisplay = (Arraylist::Display)v;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetFontSize(JNIEnv*, jclass, jfloat v) {
    gArraylist.mFontSize = v;
    // Notifications 字体与 Arraylist 保持 30/35 比例
    gNotifications.mFontSize = v * (30.0f / 35.0f);
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_ToggleModule(JNIEnv* env, jclass, jstring name, jstring setting, jboolean addIfMissing) {
    const char* n = env->GetStringUTFChars(name, nullptr);
    const char* s = env->GetStringUTFChars(setting, nullptr);
    gArraylist.toggleModule(n, s, addIfMissing);
    env->ReleaseStringUTFChars(name, n);
    env->ReleaseStringUTFChars(setting, s);
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetGlowDensity(JNIEnv*, jclass, jint v) {
    gArraylist.mGlowDensity = v;
    gNotifications.mShadowDensity = v;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetGlowRadius(JNIEnv*, jclass, jfloat v) {
    gArraylist.mGlowStrength = v;
    gNotifications.mShadowBlur = v * 100.0f;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetWatermarkText(JNIEnv* env, jclass, jstring text) {
    const char* cstr = env->GetStringUTFChars(text, nullptr);
    gArraylist.mWatermarkText = cstr;
    env->ReleaseStringUTFChars(text, cstr);
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetRightOffset(JNIEnv*, jclass, jfloat v) {
    gArraylist.mRightOffset = v;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetTextShadow(JNIEnv*, jclass, jboolean v) {
    gArraylist.mTextShadow = v;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mlxingc_solstice_SolsticeUI_SetShadowOffset(JNIEnv*, jclass, jfloat v) {
    gArraylist.mShadowOffset = v;
}
