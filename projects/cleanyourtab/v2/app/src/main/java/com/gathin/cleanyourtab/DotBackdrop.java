package com.gathin.cleanyourtab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;

/** Decorative Nothing-inspired dots + low-contrast radial accent, entirely offline. */
public class DotBackdrop extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public DotBackdrop(Context ctx) { super(ctx); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w=getWidth(), h=getHeight();
        paint.setShader(new LinearGradient(0,0,w,h, Color.rgb(16,22,30),Color.rgb(5,8,12),Shader.TileMode.CLAMP));
        canvas.drawRect(0,0,w,h,paint);
        paint.setShader(null);
        paint.setColor(Color.argb(21,240,244,249));
        float step=dp(22), r=dp(0.8f);
        for(float x=dp(18);x<w;x+=step) for(float y=dp(20);y<h;y+=step) canvas.drawCircle(x,y,r,paint);
        paint.setColor(Color.argb(30,255,82,85));
        canvas.drawCircle(w*0.86f,h*0.19f,dp(115),paint);
    }
    float dp(float v){return v*getResources().getDisplayMetrics().density;}
}
