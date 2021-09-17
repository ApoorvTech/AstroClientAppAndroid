package com.myastrotell.utils;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.myastrotell.R;

public class CustomTextView extends View {
    // Content filling brush
    private Paint contentPaint;
    // Standard font color
    private String contentNormalColor = "#737373";
    // Focused font color
    private String contentFocuedColor = "#333333";
    // Control width
    private int viewWidth = 0;
    // Control height
    private int viewHeight = 0;
    // Standard word style
    public static final int TEXT_TYPE_NORMAL = 1;
    // Process when the control gets focus
    public static final int TEXT_TYPE_FOCUED = 2;
    // The default is the standard style
    private int currentTextType = TEXT_TYPE_NORMAL;
    // default current color
    private String textColor = "#444444";
    //font size
    private int textSize = 60;
    // Content
    private String mText = "Test text information";
    // Minimum view height
    private float minHeight = 0;
    // Minimum view width
    private float minWidth = 0;
    //Line spacing
    private float lineSpace = 20;
    // Maximum number of rows
    private int maxLines = 0;
    // Text measurement tool
    private Paint.FontMetricsInt textFm;
    /// Real number of lines
    private int realLines;
    // Number of currently displayed lines
    private int line;
    // Whether to display an ellipsis at the end
    private boolean showEllipsise;

    // The width of the text display area
    private int textWidth;
    // The height of a single line of text
    private int signleLineHeight;
    private int mPaddingLeft, mPaddingRight, mPaddingTop, mPaddingBottom;
    /**
     * Ellipsis
     **/
    private String ellipsis = "...";

    public CustomTextView(Context context) {
        this(context, null);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        initAttr(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    private boolean isFristload;

    /**
     * Initialization
     */
    private void init() {
        contentPaint = new Paint();
        contentPaint.setTextSize(textSize);
        contentPaint.setAntiAlias(true);
        contentPaint.setStrokeWidth(2);
        contentPaint.setColor(Color.parseColor(textColor));
        contentPaint.setTextAlign(Paint.Align.LEFT);
        textFm = contentPaint.getFontMetricsInt();
        signleLineHeight = Math.abs(textFm.top - textFm.bottom);
    }

    /**
     * Initialize properties
     *
     * @param context
     * @param attrs
     */
    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        mPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_paddingLeft, 0);
        mPaddingRight = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_paddingRight, 0);
        mPaddingTop = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_paddingTop, 0);
        mPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_paddingBottom, 0);

        mText = typedArray.getString(R.styleable.CustomTextView_text);
        textColor = typedArray.getString(R.styleable.CustomTextView_textColor);
        if (textColor == null) {
            textColor = "#444444";
        }

        textSize = typedArray.getDimensionPixelSize(R.styleable.CustomTextView_textSize, dip2px( context,15));
        // lineSpace = typedArray.getInteger(R.styleable.CustomTextView_lineSpacing, 20);
        typedArray.recycle();
    }

    public void setText(String ss) {
        mText = ss;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        textWidth = viewWidth - mPaddingLeft - mPaddingRight;
        if(mText!=null)
        viewHeight = (int) getViewHeight();
        setMeasuredDimension(viewWidth, viewHeight);
    }

    private float getViewHeight() {
        char[] textChars = mText.toCharArray();
        float ww = 0.0f;
        int count = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textChars.length; i++) {
            float v = contentPaint.measureText(textChars[i] + "");
            if (ww + v <= textWidth) {
                sb.append(textChars[i]);
                ww += v;
            } else {
                count++;
                sb = new StringBuilder();
                ww = 0.0f;
                ww += v;
                sb.append(textChars[i]);
            }
        }
        if (sb.toString().length() != 0) {
            count++;
        }
        return count * signleLineHeight + lineSpace * (count - 1) + mPaddingBottom + mPaddingTop;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
    }

    /**
     * Loop through the text
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {

        char[] textChars = mText.toCharArray();
        float ww = 0.0f;
        float startL = 0.0f;
        float startT = 0.0f;
        startL += mPaddingLeft;
        startT += mPaddingTop + signleLineHeight / 2 + (textFm.bottom - textFm.top) / 2 - textFm.bottom;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < textChars.length; i++) {
            float v = contentPaint.measureText(textChars[i] + "");
            if (ww + v <= textWidth) {
                sb.append(textChars[i]);
                ww += v;
            } else {
                canvas.drawText(sb.toString(), startL, startT, contentPaint);
                startT += signleLineHeight + lineSpace;
                sb = new StringBuilder();
                ww = 0.0f;
                ww += v;
                sb.append(textChars[i]);
            }
        }

        if (sb.toString().length() > 0) {
            canvas.drawText(sb.toString(), startL, startT, contentPaint);
        }

    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);

      }


    }

