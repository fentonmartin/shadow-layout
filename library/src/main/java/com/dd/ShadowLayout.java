package com.dd;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dd.shadow.layout.R;

public class ShadowLayout extends FrameLayout {

    private int shadowColor;
    private float shadowRadius;
    private float cornerRadius;
    private float dx;
    private float dy;

    private boolean isInvalidateShadowOnSizeChanged = true;
    private boolean isForceInvalidateShadow = false;

    public ShadowLayout(Context context) {
        super(context);
        initView(context, null, 0);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0);
    }

    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShadowLayout(final Context context, final AttributeSet attrs, final int defStyleAttr,
            final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleRes);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return 0;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0 && (getBackground() == null || isInvalidateShadowOnSizeChanged || isForceInvalidateShadow)) {
            isForceInvalidateShadow = false;
            setBackgroundCompat(w, h);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isForceInvalidateShadow) {
            isForceInvalidateShadow = false;
            setBackgroundCompat(right - left, bottom - top);
        }
    }

    public void setInvalidateShadowOnSizeChanged(boolean invalidateShadowOnSizeChanged) {
        isInvalidateShadowOnSizeChanged = invalidateShadowOnSizeChanged;
    }

    public void invalidateShadow() {
        isForceInvalidateShadow = true;
        requestLayout();
        invalidate();
    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyledRes) {
        initAttributes(context, attrs, defStyledRes);

        final int xPadding = (int) (shadowRadius + Math.abs(dx));
        final int yPadding = (int) (shadowRadius + Math.abs(dy));
        setPadding(xPadding, yPadding, xPadding, yPadding);
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        Bitmap bitmap = createShadowBitmap(w, h, cornerRadius, shadowRadius, dx, dy, shadowColor, Color.TRANSPARENT);
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            setBackgroundDrawable(drawable);
        }
    }

    private void initAttributes(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleRes) {
        if (attrs == null)
            return;

        final TypedArray attr = getTypedArray(context, attrs, R.styleable.ShadowLayout, defStyleRes);

        try {
            cornerRadius = attr.getDimension(R.styleable.ShadowLayout_cornerRadius, getResources().getDimension(R.dimen.default_corner_radius));
            shadowRadius = attr.getDimension(R.styleable.ShadowLayout_shadowRadius, getResources().getDimension(R.dimen.default_shadow_radius));
            dx = attr.getDimension(R.styleable.ShadowLayout_dx, 0);
            dy = attr.getDimension(R.styleable.ShadowLayout_dy, 0);
            shadowColor = attr.getColor(R.styleable.ShadowLayout_shadowColor, getResources().getColor(R.color.default_shadow_color));
        } finally {
            attr.recycle();
        }
    }

    @NonNull
    private TypedArray getTypedArray(@NonNull Context context, @NonNull AttributeSet attributeSet,
            @NonNull int[] attr, int defStyleRes) {
        return context.obtainStyledAttributes(attributeSet, attr, 0, defStyleRes);
    }

    @NonNull
    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
            float dx, float dy, int shadowColor, int fillColor) {

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(output);

        RectF shadowRect = new RectF(
                shadowRadius,
                shadowRadius,
                shadowWidth - shadowRadius,
                shadowHeight - shadowRadius);

        if (dy > 0) {
            shadowRect.top += dy;
            shadowRect.bottom -= dy;
        } else if (dy < 0) {
            shadowRect.top += Math.abs(dy);
            shadowRect.bottom -= Math.abs(dy);
        }
        if (dx > 0) {
            shadowRect.left += dx;
            shadowRect.right -= dx;
        } else if (dx < 0) {
            shadowRect.left += Math.abs(dx);
            shadowRect.right -= Math.abs(dx);
        }
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(fillColor);
        shadowPaint.setStyle(Paint.Style.FILL);

        if (!isInEditMode()) {
            shadowPaint.setShadowLayer(shadowRadius, dx, dy, shadowColor);
        }
        canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);

        return output;
    }
}