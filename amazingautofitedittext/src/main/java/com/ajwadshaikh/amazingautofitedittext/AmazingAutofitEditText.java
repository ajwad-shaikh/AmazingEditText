package com.ajwadshaikh.amazingautofitedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.View;

import java.util.Arrays;

public class AmazingAutofitEditText extends AppCompatEditText {

    private static float DEFAULT_MIN_TEXT_SIZE = 16f;
    private static float DEFAULT_MAX_TEXT_SIZE = 80f;
    private static int DEFAULT_MIN_WIDTH = 800;

    private final SparseIntArray textCachedSizes = new SparseIntArray();
    private final SizeTester sizeTester;
    private TextPaint paint;
    private float maxTextSize;
    private float minTextSize;
    private float minWidth;
    private float scaleFactor = 1.f;
    private boolean isCursorVisible = false;
    private boolean isDefaultTypeface = true;
    private int maxWidth;


    public AmazingAutofitEditText(final Context context) {
        this(context, null, 0);
    }

    public AmazingAutofitEditText(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmazingAutofitEditText(final Context context, final AttributeSet attrs,
                                  final int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.AmazingAutofitEditText);
        if (typedArray != null) {
            minTextSize = typedArray.getDimension(R.styleable.AmazingAutofitEditText_minTextSize,
                    DEFAULT_MIN_TEXT_SIZE * getResources().getDisplayMetrics().scaledDensity);
            maxTextSize = typedArray.getDimension(R.styleable.AmazingAutofitEditText_maxTextSize,
                    DEFAULT_MAX_TEXT_SIZE * getResources().getDisplayMetrics().scaledDensity);
            minWidth = typedArray.getDimensionPixelOffset(R.styleable.AmazingAutofitEditText_minWidth, DEFAULT_MIN_WIDTH);
            typedArray.recycle();
        }

        sizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public int onTestSize(final int suggestedSize,
                                  final RectF availableSpace) {
                paint.setTextSize(suggestedSize);

                String text;
                if (!TextUtils.isEmpty(getHint())) {
                    text = "";
                } else {
                    text = getText().toString();
                }
                String[] lines = text.split("\\r?\\n");
                Log.d("lines", Arrays.toString(lines));
                float key = 0;
                for(String line : lines) {
                    key = Math.max(paint.measureText(line), key);
                }

                textRect.bottom = paint.getFontSpacing();
                textRect.right = key;
                textRect.offsetTo(0, 0);

                if (availableSpace.contains(textRect)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };

        setFocusable(true);
        setFocusableInTouchMode(true);
        setTextIsSelectable(true);
        setSingleLine(false);
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        setTextSize(((int)minTextSize + (int) maxTextSize) >>> 1);
        addSelfRemovableTextWatcher();
        setDrawingCacheEnabled(true);

    }

    @Override
    public void setCursorVisible(boolean visible) {
        isCursorVisible = visible;
//        super.setCursorVisible(visible);
    }

    @Override
    protected  void onFinishInflate() {
        super.onFinishInflate();
        adjustTextSize();
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        adjustTextSize();
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldwidth, final int oldheight) {
        textCachedSizes.clear();
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight) {
            adjustTextSize();
        }
    }

    /**
     * Sets the typeface in which the text should be displayed
     */
    @Override
    public void setTypeface(final Typeface tf) {
        if (paint == null) {
            paint = new TextPaint(getPaint());
        }
        isDefaultTypeface = false;
        paint.setTypeface(tf);
        super.setTypeface(tf);
    }

    /**
     * Resizes text on layout changes
     */
    private void adjustTextSize() {
        final int startSize = (int) minTextSize;
        int heightLimit;
        if (isDefaultTypeface) {
            heightLimit = getMeasuredHeight()
                    - getCompoundPaddingBottom() - getCompoundPaddingTop();
            maxWidth = getMeasuredWidth() - getCompoundPaddingLeft()
                    - getCompoundPaddingRight();
        } else {
            heightLimit = ((int) (getMeasuredHeight() * scaleFactor))
                    - getCompoundPaddingBottom() - getCompoundPaddingTop();
            maxWidth = getMeasuredWidth() - getCompoundPaddingLeft()
                    - getCompoundPaddingRight();
        }

        if (maxWidth <= 0) {
            return;
        }

        super.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                efficientTextSizeSearch(startSize, (int) maxTextSize,
                        sizeTester, new RectF(0, 0, maxWidth, ((View)getParent()).getHeight())));

    }

    /**
     * Gets cached text size from list of previously stored sizes
     */
    private int efficientTextSizeSearch(final int start, final int end,
                                        final SizeTester sizeTester, final RectF availableSpace) {
        String text;
        if (!TextUtils.isEmpty(getHint())) {
            text = getHint().toString();
        } else {
            text = getText().toString();
        }
        String[] lines = text.split("\\r?\\n");
        Log.d("lines", Arrays.toString(lines));
        int key = 0;
        for(String line : lines) {
            key = Math.max(line.length(), key);
        }
        Log.d("key", String.valueOf(key));
        int size = textCachedSizes.get(key);
        if (size != 0) {
            Log.d("size", String.valueOf(size));
            return size;
        }
        Log.d("start", String.valueOf(start));
        Log.d("end", String.valueOf(end));
        size = binarySearch(start, end, sizeTester, availableSpace);
        textCachedSizes.put(key, size);
        Log.d("avail", availableSpace.toShortString());
        return size;
    }

    /**
     * Calculates best text size for current EditText size
     */
    private int binarySearch(final int start, final int end, final SizeTester sizeTester, final RectF availableSpace) {
        int lastBest = start;
        int low = start;
        int high = end - 1;
        int middle;
        while (low <= high) {
            middle = low + high >>> 1;
            final int midValCmp = sizeTester.onTestSize(middle, availableSpace);
            if (midValCmp < 0) {
                lastBest = low;
                low = middle + 1;
            } else if (midValCmp > 0) {
                high = middle - 1;
                lastBest = high;
            } else {
                return middle;
            }
        }
        return lastBest;
    }

    public float getMaxTextSize() {
        return maxTextSize;
    }

    public void setMaxTextSize(float maxTextSize) {
        this.maxTextSize = maxTextSize;
    }

    public float getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(float minTextSize) {
        this.minTextSize = minTextSize;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    @Override
    public int getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    private void addSelfRemovableTextWatcher() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                removeTextChangedListener(this);
                setHint(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //empty
            }
        });
    }

    private interface SizeTester {
        /**
         * AutoResizeEditText
         *
         * @param suggestedSize  Size of text to be tested
         * @param availableSpace available space in which text must fit
         * @return an integer < 0 if after applying {@code suggestedSize} to
         * text, it takes less space than {@code availableSpace}, > 0
         * otherwise
         */
        int onTestSize(int suggestedSize, RectF availableSpace);
    }
}
