package com.cumulations.libreV2;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007J \u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\u0005H\u0002J\b\u0010\f\u001a\u00020\rH\u0016J\u0010\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\tH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/cumulations/libreV2/BlurTransformation;", "Lcom/squareup/picasso/Transformation;", "context", "Landroid/content/Context;", "mRadius", "", "mSampling", "(Landroid/content/Context;II)V", "getBlurredBitmap", "Landroid/graphics/Bitmap;", "bitmap", "radius", "key", "", "transform", "source", "Companion", "libreoemsdk_release"})
public final class BlurTransformation implements com.squareup.picasso.Transformation {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    private final int mRadius = 0;
    private final int mSampling = 0;
    public static final int MAX_RADIUS = 25;
    public static final int DEFAULT_DOWN_SAMPLING = 1;
    @org.jetbrains.annotations.NotNull
    public static final com.cumulations.libreV2.BlurTransformation.Companion Companion = null;
    
    public BlurTransformation(@org.jetbrains.annotations.NotNull
    android.content.Context context, int mRadius, int mSampling) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public android.graphics.Bitmap transform(@org.jetbrains.annotations.NotNull
    android.graphics.Bitmap source) {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String key() {
        return null;
    }
    
    @kotlin.jvm.Throws(exceptionClasses = {androidx.renderscript.RSRuntimeException.class})
    private final android.graphics.Bitmap getBlurredBitmap(android.content.Context context, android.graphics.Bitmap bitmap, int radius) throws androidx.renderscript.RSRuntimeException {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/cumulations/libreV2/BlurTransformation$Companion;", "", "()V", "DEFAULT_DOWN_SAMPLING", "", "MAX_RADIUS", "libreoemsdk_release"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}