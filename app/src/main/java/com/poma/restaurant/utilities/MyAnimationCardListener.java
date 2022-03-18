package com.poma.restaurant.utilities;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class MyAnimationCardListener implements Animation.AnimationListener {

    ImageView view;
    Float scalex;
    Float scaley;
    Intent intent;

    public Float getScalex() {
        return scalex;
    }

    public Float getScaley() {
        return scaley;
    }

    public void setIntent(Intent i){
        this.intent=i;
    }

    public void setImage(ImageView view) {
        this.view = view;
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
                new Float(1.5), this.scalex,// Start and end values for the X axis scaling
                 new Float(1.5), this.scaley, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        a.setFillAfter(true); // Needed to keep the result of the animation
        a.setDuration(500);

        view.startAnimation(a);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
