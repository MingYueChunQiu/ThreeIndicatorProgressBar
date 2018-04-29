# ThreeIndicatorProgressBar
三端指示进度条
由于在项目中需要在一个view中显示三个进度，所以自定义了一个三端指示进度条，下面简单介绍一下它的使用，希望能对大家有所帮助，如果有不对之处，请多包涵，欢迎指正。

## 一.引入依赖##
1.在项目的根build.gradle下添加

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2.在自己的Module的build.gradle下添加依赖

```
	dependencies {
	        implementation 'com.github.MingYueChunQiu:ThreeIndicatorProgressBar:1.0'
	}

```

## 二.如何使用 ##
1.在xml中进行使用

```
    <com.zhuolong.threeindicatorprogressbar.ui.ThreeIndicatorProgressBar
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_margin="10dp"
        android:max="10"
        android:progress="2"
        app:barRoundedRadius="10dp"
        app:isHeadDrawable="true"
        app:isTailDrawable="true"
        app:progressBarHeight="30dp"
        app:progressTextSize="20sp"
        app:headBarColor="@android:color/holo_green_dark"
        app:middleBarColor="@android:color/darker_gray"
        app:tailBarColor="@android:color/holo_red_dark"
        app:tailProgress="3" />
```
2.在代码中使用Builder模式创建对象

```
ThreeIndicatorProgressBar progressBar = new ThreeIndicatorProgressBar.Builder(this)
                .setBarHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        20, getResources().getDisplayMetrics()))
                .setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                        30, getResources().getDisplayMetrics()))
                .setHeadBarColor(Color.GREEN)
                .setMiddleBarColor(Color.GRAY)
                .setTailBarColor(Color.RED)
                .setHeadCircleColor(Color.BLUE)
                .setTailCircleColor(Color.BLUE)
                .setMax(10)
                .setProgress(2)
                .setTailProgress(4)
                .build();
        progressBar.setTailProgress(2);
        ((LinearLayoutCompat)findViewById(R.id.cl_container))
                .addView(progressBar);
```
3.项目的源代码：

```
public class ThreeIndicatorProgressBar extends ProgressBar {

    private static final int DEFAULT_BAR_ROUNDED_RADIUS = 25;//默认进度条圆角度数
    private static final int DEFAULT_BAR_HEIGHT = 20;//默认进度条高度
    private static final int DEFAULT_TEXT_SIZE = 20;//默认文字大小

    private int mBarRoundedRadius;//进度条圆角矩形角度
    private int mHeadBarColor, mMiddleBarColor, mTailBarColor, mHeadCircleColor, mTailCircleColor;
    private int mProgressTotalWidth;
    private int mTailProgress;//记录尾端进度
    private boolean isPercentage, isHeadDrawable, isTailDrawable;//标记是否显示作业百分比，头部和底部是否绘制图片还是圆圈
    private Paint mPaint;
    private int mHeight;//总高度
    private int mBarHeight;//进度条高度
    private int mTextSize;//提示文字大小
    private int mHeadBarDrawableId, mTailBarDrawableId;//头部和尾部进度图标资源ID
    private Bitmap mHeadBarBitmap, mTailBarBitmap;//头部和尾部进度图标
    private float mRadius;//头部进度指示头半径
    private boolean isFromHead;//标记进度是从头部还是尾部变化

    public ThreeIndicatorProgressBar(Builder builder) {
        this(builder.context);
        mBarRoundedRadius = builder.barRoundedRadius;
        mHeadBarColor = builder.headBarColor;
        mMiddleBarColor = builder.middleBarColor;
        mTailBarColor = builder.tailBarColor;
        mHeadCircleColor = builder.headCircleColor;
        mTailCircleColor = builder.tailCircleColor;
        mProgressTotalWidth = builder.progressTotalWidth;
        setMax(builder.max);
        setProgress(builder.progress);
        mTailProgress = builder.tailProgress;
        isPercentage = builder.isPercentage;
        isHeadDrawable = builder.isHeadDrawable;
        isTailDrawable = builder.isTailDrawable;
        mBarHeight = builder.barHeight;
        mTextSize = builder.textSize;
        mHeadBarDrawableId = builder.headBarDrawableId;
        mTailBarDrawableId = builder.tailBarDrawableId;
        initPadding(0.5f);
    }

    public ThreeIndicatorProgressBar(Context context) {
        this(context, null);
    }

    public ThreeIndicatorProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreeIndicatorProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
        initPadding(0.5f);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        isFromHead = true;
    }

    public int getTailProgress() {
        return mTailProgress;
    }

    public void setTailProgress(int tailProgress) {
        mTailProgress = tailProgress;
        isFromHead = false;
        invalidate();
    }

    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(boolean percentage) {
        isPercentage = percentage;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        mProgressTotalWidth = width - getPaddingLeft() - getPaddingRight();
        setMeasuredDimension(width, mHeight);
        initDrawable();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        float headRadio = getProgress() * 1.0f / getMax();
        float headProgressX = mProgressTotalWidth * headRadio;
        float tailRadio = getTailProgress() * 1.0f / getMax();
        float tailProgressX = mProgressTotalWidth - mProgressTotalWidth * tailRadio;
        String headText, tailText, middleText;
        if (isPercentage) {
            headText = getProgress() + "%";
            tailText = getTailProgress() + "%";
            middleText = (getMax() - getProgress() - getTailProgress()) + "%";
        } else {
            headText = getProgress() + "";
            tailText = getTailProgress() + "";
            middleText = (getMax() - getProgress() - getTailProgress()) + "";
        }
//        float radius = (mBarHeight + getPaddingTop() + getPaddingBottom()) / 2;

        //绘制首端进度
        Paint headPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path headPath = new Path();
        headPaint.setColor(mHeadBarColor);
        RectF headRectF = new RectF(getPaddingLeft(), getPaddingTop(),
                headProgressX, mBarHeight + getPaddingTop());
        float[] headRadius = new float[]{mBarRoundedRadius, mBarRoundedRadius, 0, 0, 0, 0,
                mBarRoundedRadius, mBarRoundedRadius};
        headPath.addRoundRect(headRectF, headRadius, Path.Direction.CW);
        canvas.drawPath(headPath, headPaint);

        //绘制尾端进度
        float tailX = tailProgressX;
        if (tailX < headProgressX) {
            tailX = headProgressX;
        }
        Paint tailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path tailPath = new Path();
        tailPaint.setColor(mTailBarColor);
        RectF tailRectF = new RectF(tailX + getPaddingLeft(), getPaddingTop(),
                mProgressTotalWidth + getPaddingLeft(), mBarHeight + getPaddingTop());
        float[] tailRadius = new float[]{0, 0, mBarRoundedRadius, mBarRoundedRadius,
                mBarRoundedRadius, mBarRoundedRadius, 0, 0};
        tailPath.addRoundRect(tailRectF, tailRadius, Path.Direction.CW);
        canvas.drawPath(tailPath, tailPaint);

        //绘制中间进度，如果有一头为0，则绘制圆角矩形，否则绘制矩形
        Paint middlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        middlePaint.setColor(mMiddleBarColor);
        RectF middleRectF = new RectF(headProgressX + getPaddingLeft(), getPaddingTop(),
                tailProgressX + getPaddingLeft(), mBarHeight + getPaddingTop());
        float[] middleRadius = null;
        if (getProgress() <= 0 || getTailProgress() <= 0) {
            middleRadius = new float[]{mBarRoundedRadius, mBarRoundedRadius, mBarRoundedRadius, mBarRoundedRadius,
                    mBarRoundedRadius, mBarRoundedRadius, mBarRoundedRadius, mBarRoundedRadius};
        }
        if (middleRadius == null) {
            canvas.drawRect(middleRectF, middlePaint);
        } else {
            Path middlePath = new Path();
            middlePath.addRoundRect(middleRectF, middleRadius, Path.Direction.CW);
            canvas.drawPath(middlePath, middlePaint);
        }

        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //先绘制尾端指示圆，这样当两端交汇时，会由首端的指示圆覆盖掉尾端的指示圆
//        drawIndicatorCircle(canvas, mTailBarColor, tailProgressX, mRadius, circlePaint);
        if (isFromHead) {
            if (isHeadDrawable && mHeadBarBitmap != null) {
                canvas.drawBitmap(mHeadBarBitmap, headProgressX - mRadius, 0, mPaint);
            } else {
                drawIndicatorCircle(canvas, mHeadCircleColor, headProgressX, mRadius, circlePaint);
            }
        } else {
            if (isTailDrawable && mTailBarBitmap != null) {
                canvas.drawBitmap(mTailBarBitmap, tailProgressX - mRadius, 0, mPaint);
            } else {
                drawIndicatorCircle(canvas, mTailCircleColor, tailProgressX, mRadius, circlePaint);
            }
        }
        drawIndicatorText(canvas, headProgressX, tailProgressX, headText, tailText, middleText);
    }

    public void setHeadIndicatorRadius(float radius) {
        mRadius = radius;
    }

    public float getHeadIndicatorRadius() {
        return mRadius;
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ThreeIndicatorProgressBar);
        if (a != null) {
            mBarRoundedRadius = a.getDimensionPixelSize(R.styleable.ThreeIndicatorProgressBar_barRoundedRadius, DEFAULT_BAR_ROUNDED_RADIUS);
            mHeadBarColor = a.getColor(R.styleable.ThreeIndicatorProgressBar_headBarColor, Color.GREEN);
            mMiddleBarColor = a.getColor(R.styleable.ThreeIndicatorProgressBar_middleBarColor, Color.WHITE);
            mTailBarColor = a.getColor(R.styleable.ThreeIndicatorProgressBar_tailBarColor, Color.RED);
            isPercentage = a.getBoolean(R.styleable.ThreeIndicatorProgressBar_isIndicatorPercentage, false);
            isHeadDrawable = a.getBoolean(R.styleable.ThreeIndicatorProgressBar_isHeadDrawable, false);
            isTailDrawable = a.getBoolean(R.styleable.ThreeIndicatorProgressBar_isTailDrawable, false);
            mTailProgress = a.getInteger(R.styleable.ThreeIndicatorProgressBar_tailProgress, 0);
            mBarHeight = a.getDimensionPixelSize(R.styleable.ThreeIndicatorProgressBar_progressBarHeight, DEFAULT_BAR_HEIGHT);
            mHeadCircleColor = a.getColor(R.styleable.ThreeIndicatorProgressBar_headCircleColor, Color.YELLOW);
            mTailCircleColor = a.getColor(R.styleable.ThreeIndicatorProgressBar_tailCircleColor, Color.BLUE);
            mTextSize = a.getDimensionPixelSize(R.styleable.ThreeIndicatorProgressBar_progressTextSize, DEFAULT_TEXT_SIZE);
            mHeadBarDrawableId = a.getResourceId(R.styleable.ThreeIndicatorProgressBar_headBarDrawable,
                    R.drawable.sun);
            mTailBarDrawableId = a.getResourceId(R.styleable.ThreeIndicatorProgressBar_tailBarDrawable,
                    R.drawable.moon);
            a.recycle();
        }
    }

    private void initPadding(float ratio) {
        int padding = (int) (mBarHeight * ratio);
        setPadding(0, padding, 0, padding);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(Color.WHITE);
        mRadius = (mBarHeight + getPaddingTop() + getPaddingBottom()) / 2;
    }

    private void initDrawable() {
        Bitmap headBitmap = BitmapFactory.decodeResource(getResources(), mHeadBarDrawableId);
        Bitmap tailBitmap = BitmapFactory.decodeResource(getResources(), mTailBarDrawableId);
//        mHeadBarBitmap = Bitmap.createScaledBitmap(headBitmap, (int)mRadius * 2, (int)mRadius * 2, false);
//        mTailBarBitmap = Bitmap.createScaledBitmap(tailBitmap, (int)mRadius * 2, (int)mRadius * 2, false);
        //这种缩放图片方法，将大图片缩放成小图片时，效果比上面的好，但还是会失真
        if (headBitmap != null) {
            Matrix headMatrix = new Matrix();
            headMatrix.postScale(mRadius * 2 / headBitmap.getWidth(), mRadius * 2 / headBitmap.getHeight());
            mHeadBarBitmap = Bitmap.createBitmap(headBitmap, 0, 0,
                    headBitmap.getWidth(), headBitmap.getHeight(), headMatrix, true);
        }
        if (tailBitmap != null) {
            Matrix tailMatrix = new Matrix();
            tailMatrix.postScale(mRadius * 2 / tailBitmap.getWidth(), mRadius * 2 / tailBitmap.getHeight());
            mTailBarBitmap = Bitmap.createBitmap(tailBitmap, 0, 0,
                    tailBitmap.getWidth(), tailBitmap.getHeight(), tailMatrix, true);
        }
    }

    /**
     * 绘制首尾两端指示文本
     *
     * @param canvas        画布
     * @param headProgressX 头部文字位置
     * @param tailProgressX 尾部文字位置
     * @param headText      头部文字
     * @param tailText      尾部文字
     * @param middleText    中间文字
     */
    private void drawIndicatorText(Canvas canvas, float headProgressX, float tailProgressX,
                                   String headText, String tailText, String middleText) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(Color.WHITE);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float headTextWidth = textPaint.measureText(headText);
        float tailTextWidth = textPaint.measureText(tailText);
        float middleTextWidth = textPaint.measureText(middleText);
        float baseline = getPaddingTop() + mBarHeight / 2 + (fontMetrics.bottom - fontMetrics.top) / 2
                - fontMetrics.bottom;
        if ((isHeadDrawable && headProgressX >= headTextWidth && mHeadBarBitmap != null) ||
                (!isHeadDrawable && !isFromHead)) {
            canvas.drawText(headText, getPaddingLeft() + (headProgressX - headTextWidth) / 2,
                    baseline, textPaint);
        } else {
            canvas.drawText(headText, getPaddingLeft() + headProgressX - textPaint.measureText(headText) / 2,
                    baseline, textPaint);
        }
        if ((isTailDrawable && mProgressTotalWidth - tailProgressX >= tailTextWidth && mTailBarBitmap != null) ||
                (!isTailDrawable && isFromHead)) {
            canvas.drawText(tailText, getPaddingLeft() + tailProgressX +
                    (mProgressTotalWidth - tailProgressX - tailTextWidth) / 2, baseline, textPaint);
        } else {
            canvas.drawText(tailText, getPaddingLeft() + tailProgressX - textPaint.measureText(tailText) / 2,
                    baseline, textPaint);
        }
        //在进度条中间绘制指示进度文本
        if (tailProgressX - headProgressX >= middleTextWidth) {
            canvas.drawText(middleText,
                    headProgressX + (tailProgressX - headProgressX) / 2, baseline, textPaint);
        }
    }

    /**
     * 绘制指示圆圈
     *
     * @param canvas      圆圈画布
     * @param color       圆圈实心颜色
     * @param progressX   左端x坐标
     * @param radius      圆圈半径
     * @param circlePaint 圆圈画笔
     */
    private void drawIndicatorCircle(Canvas canvas, int color, float progressX, float radius, Paint circlePaint) {
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(color);
        canvas.drawCircle(progressX, radius, radius, circlePaint);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(Color.WHITE);
        canvas.drawCircle(progressX, radius, radius, circlePaint);
    }

    /**
     * 计算进度条高度
     *
     * @param measureSpec 测量信息
     * @return 返回确定好的进度条高度
     */
    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        float textHeight = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
        if (mBarHeight < textHeight) {
            mBarHeight = (int) (textHeight);
            mRadius = (mBarHeight + getPaddingTop() + getPaddingBottom()) / 2;
        }
        int result = getPaddingTop() + getPaddingBottom() + mBarHeight;
        if (specMode == MeasureSpec.EXACTLY) {
            if (specSize > result) {
                result = specSize;
            }
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public static class Builder {

        private Context context;
        private int barRoundedRadius;
        private int headBarColor, middleBarColor, tailBarColor, headCircleColor, tailCircleColor;
        private int progressTotalWidth;
        private int max, progress, tailProgress;//记录尾端进度
        private boolean isPercentage, isHeadDrawable, isTailDrawable;//标记是否显示作业百分比，头部和底部是否绘制图片还是圆圈
        private int barHeight;//进度条高度
        private int textSize;//提示文字大小
        private int headBarDrawableId, tailBarDrawableId;//头部和尾部进度图标资源ID

        public Builder(Context context) {
            this.context = context;
        }

        public ThreeIndicatorProgressBar build() {
            return new ThreeIndicatorProgressBar(this);
        }

        public void setBarRoundedRadius(int barRoundedRadius) {
            this.barRoundedRadius = barRoundedRadius;
        }

        public Builder setHeadBarColor(int headBarColor) {
            this.headBarColor = headBarColor;
            return this;
        }

        public Builder setMiddleBarColor(int middleBarColor) {
            this.middleBarColor = middleBarColor;
            return this;
        }

        public Builder setTailBarColor(int tailBarColor) {
            this.tailBarColor = tailBarColor;
            return this;
        }

        public Builder setHeadCircleColor(int headCircleColor) {
            this.headCircleColor = headCircleColor;
            return this;
        }

        public Builder setTailCircleColor(int tailCircleColor) {
            this.tailCircleColor = tailCircleColor;
            return this;
        }

        public Builder setProgressTotalWidth(int progressTotalWidth) {
            this.progressTotalWidth = progressTotalWidth;
            return this;
        }

        public Builder setMax(int max) {
            this.max = max;
            return this;
        }

        public Builder setProgress(int progress) {
            this.progress = progress;
            return this;
        }

        public Builder setTailProgress(int tailProgress) {
            this.tailProgress = tailProgress;
            return this;
        }

        public Builder setPercentage(boolean percentage) {
            isPercentage = percentage;
            return this;
        }

        public Builder setHeadDrawable(boolean headDrawable) {
            isHeadDrawable = headDrawable;
            return this;
        }

        public Builder setTailDrawable(boolean tailDrawable) {
            isTailDrawable = tailDrawable;
            return this;
        }

        public Builder setBarHeight(int barHeight) {
            this.barHeight = barHeight;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setHeadBarDrawableId(int headBarDrawableId) {
            this.headBarDrawableId = headBarDrawableId;
            return this;
        }

        public Builder setTailBarDrawableId(int tailBarDrawableId) {
            this.tailBarDrawableId = tailBarDrawableId;
            return this;
        }
    }

}
```

## 三.显示效果 ##
![这里写图片描述](https://img-blog.csdn.net/20180429180016147?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NsMjAxOGdvZA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

## 四.总结 ##
以上就是具体的使用方式，如果有什么建议或者更好的方式，欢迎指出，下面是项目的GitHub地址：https://github.com/MingYueChunQiu/ThreeIndicatorProgressBar.git
码云地址：https://gitee.com/MingYueChunQiu/ThreeIndicatorProgressBar.git


