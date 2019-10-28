package utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Calendar;

import butterknife.BindView;
import io.github.project_travel_mate.R;

/**
 * Created by Santosh on 11/10/18.
 *
 */
public abstract class TravelMateAnalogClock extends RelativeLayout {

    @DrawableRes
    private int mFaceId;
    @DrawableRes
    private int mHourId;
    @DrawableRes
    private int mMinuteId;
    @DrawableRes
    private int mSecondId;

    private Context mCtx;

    @BindView(R.id.face)
    AppCompatImageView AnalogFace;
    @BindView(R.id.hour)
    AppCompatImageView AnalogHour;
    @BindView(R.id.minute)
    AppCompatImageView AnalogMinute;
    @BindView(R.id.second)
    AppCompatImageView AnalogSecond;


    public TravelMateAnalogClock(Context ctx) {
        super(ctx);
        this.mCtx = ctx;
    }

    public TravelMateAnalogClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCtx = context;
    }

    public TravelMateAnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCtx = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TravelMateAnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCtx = context;
    }

    /**
     *  A simple initialization with default assets
     */
    public void initializeSimple() {
        this.mFaceId = R.drawable.clock_face;
        this.mHourId = R.drawable.hours_hand;
        this.mMinuteId = R.drawable.minutes_hand;
        this.mSecondId = R.drawable.second_hand;

        main(mCtx);
    }

    /** Intitializes the view.
     * If you want to provide your own vector assets.(You will have a hard time configuring that)
     *
     * @param faceId: the clock face vector svg resource.
     * @param hourId: the hours clock hand vector svg resource.
     * @param minuteId: the minutes clock hand vector svg resource.
     * @param secondId: the seconds clock hand vector svg resource.
     */
    public void initializeCustom(@DrawableRes int faceId, @DrawableRes int hourId,
                                 @DrawableRes int minuteId, @DrawableRes int secondId) {
        this.mFaceId = faceId;
        this.mHourId = hourId;
        this.mMinuteId = minuteId;
        this.mSecondId = secondId;

        main(mCtx);
    }

    private boolean mShowSeconds = true;
    private int mColor = 0xff000000;
    private float mScale = 1.0f;
    private float mOpacity = 1.0f;
    private Calendar mCalendar;

    private int mDp;
    private int mSizeInDp;
    private int mSizeInPixels;
    private int mDiameterInPixels = 0;
    private float mDiameterInDp = 0;
    private float mScaleMultiplier = 1.0f;

    //-------------- Getters --------------\\

    /**
     * @return the calendar that the clock is operating with
     */
    public Calendar getCalendar() {
        if (mCalendar == null)
            mCalendar = Calendar.getInstance();

        return mCalendar;
    }

    /**
     * @return the diameter in pixels set by the user explicitly in setDiameterInPixels()
     */
    public int getDiameterInPixels() {
        return mDiameterInPixels;
    }

    /**
     * @return the diameter in dp set by the user explicitly in setDiameterInDp()
     */
    public float getDiameterInDp() {
        return mDiameterInDp;
    }

    /**
     * @return the scale set by setScale()
     */
    public float getScale() {
        return mScale;
    }

    /**
     * @return [0, 1.0]
     */
    public float getOpacity() {
        return mOpacity;
    }

    /**
     * @return hexadecimal integer color
     */
    public int getColor() {
        return mColor;
    }

    /**
     * @return boolean indicating if the clock is currently showing the seconds hand
     */
    public boolean isShowingSeconds() {
        return mShowSeconds;
    }

    //-------------- Setters --------------\\

    /**
     *  Sets the timing of the clock from the calendar object
     */
    public TravelMateAnalogClock setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
        tickTick();

        return this;
    }

    /**
     * Sets the scale of the view.
     */
    public TravelMateAnalogClock setScale(float scale) {
        this.mScale = scale;
        this.setScaleY(scale * mScaleMultiplier);
        this.setScaleX(scale * mScaleMultiplier);

        return this;
    }

    /**
     * @param diameterInDp: the desired diameter in dp
     */
    public TravelMateAnalogClock setDiameterInDp(float diameterInDp) {
        this.mDiameterInDp = diameterInDp;
        //scaleMultiplier = newSize / oldSize
        mScaleMultiplier = diameterInDp / this.mSizeInDp;
        setScale(mScale);

        return this;
    }

    /**
     * @param diameterInPixels: the desired diameter in pixels
     */
    public TravelMateAnalogClock setDiameterInPixels(int diameterInPixels) {
        this.mDiameterInPixels = diameterInPixels;
        //scaleMultiplier = newSize / oldSize
        mScaleMultiplier = (diameterInPixels + 0.0f) / (this.mSizeInPixels + 0.0f);
        Log.d("xx", mScaleMultiplier + "");
        setScale(mScale);

        return this;
    }

    /**
     * @param opacity: ranges from 0 (transparent) to 1.0 (opaque)
     *
     *               Default: 1.0f
     */
    public TravelMateAnalogClock setOpacity(float opacity) {
        this.mOpacity = opacity;
        main(mCtx);

        return this;
    }

    /**
     * @param color: hexadecimal color (ex: 0xff000000)
     */
    public TravelMateAnalogClock setColor(int color) {
        this.mColor = color;
        main(mCtx);

        return this;
    }

    /**
     * @param mShowSeconds: controls whether to show the seconds hand or not.
     */
    public TravelMateAnalogClock setmShowSeconds(boolean mShowSeconds) {
        this.mShowSeconds = mShowSeconds;
        main(mCtx);

        return this;
    }

    /**
     *  Black Box
     */
    ViewTreeObserver.OnGlobalLayoutListener layoutListener;
    private void main(Context ctx) {

        Drawable face = AppCompatResources.getDrawable(ctx, mFaceId);
        Drawable hour = AppCompatResources.getDrawable(ctx, mHourId);
        Drawable minute = AppCompatResources.getDrawable(ctx, mMinuteId);
        Drawable second = AppCompatResources.getDrawable(ctx, mSecondId);

        int alpha255 = (int) (mOpacity * 255);
        face.setAlpha(alpha255);
        hour.setAlpha(alpha255);
        minute.setAlpha(alpha255);
        second.setAlpha(alpha255);

        face = DrawableCompat.wrap(face);
        hour = DrawableCompat.wrap(hour);
        minute = DrawableCompat.wrap(minute);
        second = DrawableCompat.wrap(second);

        DrawableCompat.setTint(face.mutate(), mColor);
        DrawableCompat.setTint(hour.mutate(), mColor);
        DrawableCompat.setTint(minute.mutate(), mColor);
        DrawableCompat.setTint(second.mutate(), mColor);

        inflate(ctx, R.layout.analog_clock, this);

//

        if (!mShowSeconds) {
            AnalogSecond.setVisibility(GONE);
        }

        //square it
        AnalogFace.setAdjustViewBounds(true);
        AnalogHour.setAdjustViewBounds(true);
        AnalogMinute.setAdjustViewBounds(true);
        AnalogSecond.setAdjustViewBounds(true);

        AnalogHour.setScaleType(ImageView.ScaleType.FIT_END);
        AnalogMinute.setScaleType(ImageView.ScaleType.FIT_END);
        AnalogSecond.setScaleType(ImageView.ScaleType.FIT_END);

        mSizeInDp = 40; //why 40 ? cause it works.
        mSizeInDp = (mSizeInDp + 25) * 4;
        mSizeInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mSizeInDp, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams layoutParams = AnalogFace.getLayoutParams();
        layoutParams.width = mSizeInPixels;
        layoutParams.height = mSizeInPixels;
        AnalogFace.setLayoutParams(layoutParams);

        layoutParams = AnalogSecond.getLayoutParams();
        float minutesHeight = (mSizeInPixels / 2) - (mSizeInPixels / 5.5f);
        layoutParams.height = (int) minutesHeight;
        AnalogSecond.setLayoutParams(layoutParams);

        layoutParams = AnalogMinute.getLayoutParams();
        layoutParams.height = (int) minutesHeight;
        AnalogMinute.setLayoutParams(layoutParams);

        layoutParams = AnalogHour.getLayoutParams();
        layoutParams.height = (mSizeInPixels) / 5;
        AnalogHour.setLayoutParams(layoutParams);

        AnalogFace.setImageDrawable(face);
        AnalogHour.setImageDrawable(hour);
        AnalogMinute.setImageDrawable(minute);
        AnalogSecond.setImageDrawable(second);

        mDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.5f, getResources().getDisplayMetrics());

        layoutListener = () -> {
            setPositionFor(AnalogSecond);
            setPositionFor(AnalogMinute);
            setPositionFor(AnalogHour);

            tickTick();

            getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
        };

        //the coolest line
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    private void setPositionFor(View v) {
        v.setTranslationY( -v.getHeight() / 2 + mDp);
    }

    /**
     *  Positions clock hands correctly(hopefully), and the rest is clockwork
     */
    private void tickTick() {
        Calendar calendar = getCalendar();

        long curMillis = calendar.get(Calendar.MILLISECOND);
        int curSecond = calendar.get(Calendar.SECOND);
        int curMinute = calendar.get(Calendar.MINUTE);
        int curHour = calendar.get(Calendar.HOUR);

        //every 1 second moves 6 degrees every second
        //every 1 minute moves 6 degrees every minute
        //every 1 hour moves by 30 degrees every 1 hour
        //Seconds Degrees
        float degrees = curSecond * 6 + 0.006f * curMillis;
        long duration = 1000 * 60;

        Animation positioner = new RotateAnimation(0, degrees, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        positioner.setFillAfter(true);
        positioner.setDuration(0);

        Animation rotator = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotator.setDuration(duration);
        rotator.setRepeatMode(Animation.RESTART);
        rotator.setRepeatCount(Animation.INFINITE);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(positioner);
        animationSet.addAnimation(rotator);
        animationSet.setInterpolator(new LinearInterpolator());

        AnalogSecond.startAnimation(animationSet);

        //Minutes Degree
        degrees = 6 * curMinute + 0.1f * curSecond + (1e-5f) * curMillis;
        duration *= 60;

        positioner = new RotateAnimation(0, degrees, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        positioner.setFillAfter(true);
        positioner.setDuration(0);

        rotator = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotator.setDuration(duration);
        rotator.setRepeatMode(Animation.RESTART);
        rotator.setRepeatCount(Animation.INFINITE);

        animationSet = new AnimationSet(true);
        animationSet.addAnimation(positioner);
        animationSet.addAnimation(rotator);
        animationSet.setInterpolator(new LinearInterpolator());

        AnalogMinute.startAnimation(animationSet);

        //every 1 hour moves by 0.00166667 degree every 1 second
        //every 1 hour moves by 0.1 degree every 1 minute

        //Hours Degree
        degrees = 1.66667e-6f * curMillis + 1.66667e-3f * curSecond + 0.5f * curMinute + 30 * curHour;
        duration *= 12;

        positioner = new RotateAnimation(0, degrees, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        positioner.setFillAfter(true);
        positioner.setDuration(0);

        rotator = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotator.setDuration(duration);
        rotator.setRepeatMode(Animation.RESTART);
        rotator.setRepeatCount(Animation.INFINITE);

        animationSet = new AnimationSet(true);
        animationSet.addAnimation(positioner);
        animationSet.addAnimation(rotator);
        animationSet.setInterpolator(new LinearInterpolator());

        AnalogHour.startAnimation(animationSet);

        //move analogSecond by 360 degree every 1 minute
        //move analogMinute by 360 degrees every 60 minute
        //move analogHour by 360 degrees every 3600 minutes
    }
}