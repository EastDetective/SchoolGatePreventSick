package com.example.cs.pushpull.extension

import android.opengl.GLES10
import android.os.Build
import android.widget.Button
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay

fun Button.disable() {
    alpha = 0.5f
    isClickable = false
}

fun Button.enable() {
    alpha = 1.0f
    isClickable = true
}

fun getOpenglRenderLimitValue(): Int {

    val maxSize: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        getOpenglRenderLimitEqualAboveLollipop()
    } else {
        getOpenglRenderLimitBelowLollipop()
    }

    return if (maxSize == 0) 4096 else maxSize
}

private fun getOpenglRenderLimitBelowLollipop(): Int {
    val maxSize = IntArray(1)
    GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
    return maxSize[0]
}

private fun getOpenglRenderLimitEqualAboveLollipop(): Int {
    val egl: EGL10 = EGLContext.getEGL() as EGL10
    val dpy: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
    val vers = IntArray(2)
    egl.eglInitialize(dpy, vers)
    val configAttr = intArrayOf(
        EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
        EGL10.EGL_LEVEL, 0,
        EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
        EGL10.EGL_NONE
    )
    val configs = Array<EGLConfig?>(1) { null }
    val numConfig = IntArray(1)
    egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig)
    if (numConfig[0] == 0) {
    }
    val config = configs[0]
    val surfAttr = intArrayOf(EGL10.EGL_WIDTH, 64, EGL10.EGL_HEIGHT, 64, EGL10.EGL_NONE)
    val surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr)
    val EGL_CONTEXT_CLIENT_VERSION = 0x3098
    val ctxAttrib = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 1, EGL10.EGL_NONE)
    val ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib)
    egl.eglMakeCurrent(dpy, surf, surf, ctx)
    val maxSize = IntArray(1)
    GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
    egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
    egl.eglDestroySurface(dpy, surf)
    egl.eglDestroyContext(dpy, ctx)
    egl.eglTerminate(dpy)
    return maxSize[0]
}