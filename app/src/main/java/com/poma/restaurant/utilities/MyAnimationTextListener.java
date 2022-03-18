package com.poma.restaurant.utilities;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAnimationTextListener implements Animation.AnimationListener {

    TextView text;
    Float scalex;
    Float scaley;

    public Float getScalex() {
        return scalex;
    }

    public Float getScaley() {
        return scaley;
    }

    public void setImage(TextView text) {
        this.text = text;
    }

    public void setXY(Float x, Float y) {
        this.scalex = x;
        this.scaley = y;}

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Animation a = new ScaleAnimation(
                new Float(0.9), this.scalex,// Start and end values for the X axis scaling
                 new Float(0.9), this.scaley, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        a.setFillAfter(true); // Needed to keep the result of the animation
        a.setDuration(500);

        text.startAnimation(a);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
