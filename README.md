# XImageView

[RatioImageView 实现ImageView按比例缩放等效果](https://www.jianshu.com/p/b9a5aeb07978)
[ShapeImageView 处理ImageView形状，原形圆角等](https://www.jianshu.com/p/8b942a78c158)


### 0. 源码地址
https://github.com/zhxhcoder/XImageView
### 1. 引用方法

```
compile 'com.zhxh:ximageviewlib:1.2'
```

### 2. 使用方法


举个栗子：
```
        <com.zhxh.ximageviewlib.RatioImageView
            android:id="@+id/ad_app_image"
            android:layout_width="100dp"
            android:layout_height="wrap_content"
            android:scaleType="centerCrop"
            android:src="@drawable/ic_test_750_360"
            app:riv_height_to_width_ratio="0.48"
            tools:ignore="ContentDescription" />
```
上面riv_height_to_width_ratio=0.48
已经定义layout_width="100dp" 计算得出 layout_height="48dp"


实现效果

![](https://github.com/zhxhcoder/XImageView/blob/master/screenshots/ximageview.png)

### 3. 源码实现

#### 3.1 属性定义与描述
```
    <declare-styleable name="RatioImageView">
        <!-- 宽度是否根据src图片的比例来测量（高度已知） -->
        <attr name="riv_is_width_fix_drawable_size_ratio" format="boolean" />
        <!-- 高度是否根据src图片的比例来测量（宽度已知） -->
        <attr name="riv_is_height_fix_drawable_size_ratio" format="boolean" />
        <!--当mIsWidthFitDrawableSizeRatio生效时，最大宽度-->
        <attr name="riv_max_width_when_width_fix_drawable" format="dimension" />
        <!--当mIsHeightFitDrawableSizeRatio生效时-->
        <attr name="riv_max_height_when_height_fix_drawable" format="dimension" />
        <!-- 高度设置，参考宽度，如0.5 , 表示 高度＝宽度×0.5 -->
        <attr name="riv_height_to_width_ratio" format="float" />
        <!-- 宽度设置，参考高度，如0.5 , 表示 宽度＝高度×0.5 -->
        <attr name="riv_width_to_height_ratio" format="float" />
        <!--宽度和高度,避免layout_width/layout_height会在超过屏幕尺寸时特殊处理的情况-->
        <attr name="riv_width" format="dimension" />
        <attr name="riv_height" format="dimension" />
    </declare-styleable>
```
#### 3.2 代码实现

1，属性初始化
从AttributeSet 中初始化相关属性
```
  private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.RatioImageView);
        mIsWidthFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_riv_is_width_fix_drawable_size_ratio,
                mIsWidthFitDrawableSizeRatio);
        mIsHeightFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_riv_is_height_fix_drawable_size_ratio,
                mIsHeightFitDrawableSizeRatio);
        mMaxWidthWhenWidthFixDrawable = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_max_width_when_width_fix_drawable,
                mMaxWidthWhenWidthFixDrawable);
        mMaxHeightWhenHeightFixDrawable = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_max_height_when_height_fix_drawable,
                mMaxHeightWhenHeightFixDrawable);
        mHeightRatio = a.getFloat(
                R.styleable.RatioImageView_riv_height_to_width_ratio, mHeightRatio);
        mWidthRatio = a.getFloat(
                R.styleable.RatioImageView_riv_width_to_height_ratio, mWidthRatio);
        mDesiredWidth = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_width, mDesiredWidth);
        mDesiredHeight = a.getDimensionPixelOffset(R.styleable.RatioImageView_riv_height, mDesiredHeight);

        a.recycle();
    }
```

2，关键数据初始化

mDrawableSizeRatio = -1f; // src图片(前景图)的宽高比例

在构造函数中调用以下代码，当mDrawable不为空时
```
            mDrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
```
其他变量初始化
```
    private boolean mIsWidthFitDrawableSizeRatio; // 宽度是否根据src图片(前景图)的比例来测量（高度已知）
    private boolean mIsHeightFitDrawableSizeRatio; // 高度是否根据src图片(前景图)的比例来测量（宽度已知）
    private int mMaxWidthWhenWidthFixDrawable = -1; // 当mIsWidthFitDrawableSizeRatio生效时，最大宽度
    private int mMaxHeightWhenHeightFixDrawable = -1; // 当mIsHeightFitDrawableSizeRatio生效时，最大高度

    // 宽高比例
    private float mWidthRatio = -1; // 宽度 = 高度*mWidthRatio
    private float mHeightRatio = -1; // 高度 = 宽度*mHeightRatio

    private int mDesiredWidth = -1; // 宽度和高度,避免layout_width/layout_height会在超过屏幕尺寸时特殊处理的情况
    private int mDesiredHeight = -1;

```
3，重新生成所需的drawable
我们覆盖ImageView的setImageResource与setImageDrawable函数，对生成的drawable对象重新自定义

```
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        reSetDrawable();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        reSetDrawable();
    }
```
自定义所需的drawable对象
```
    private void reSetDrawable() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            // 发生变化，重新调整布局
            if (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio) {
                float old = mDrawableSizeRatio;
                mDrawableSizeRatio = 1f * drawable.getIntrinsicWidth()
                        / drawable.getIntrinsicHeight();
                if (old != mDrawableSizeRatio && mDrawableSizeRatio > 0) {
                    requestLayout();
                }
            }
        }
    }
```
从上面代码我们看到，当图片本身比例与定义比例不同时，会调用  **requestLayout();**方法重新调整布局。
该方法的作用是什么呢？
我们进入 **requestLayout()**方法：
```

    /**
     * Call this when something has changed which has invalidated the
     * layout of this view. This will schedule a layout pass of the view
     * tree. This should not be called while the view hierarchy is currently in a layout
     * pass ({@link #isInLayout()}. If layout is happening, the request may be honored at the
     * end of the current layout pass (and then layout will run again) or after the current
     * frame is drawn and the next layout occurs.
     *
     * <p>Subclasses which override this method should call the superclass method to
     * handle possible request-during-layout errors correctly.</p>
     */
    @CallSuper
    public void requestLayout() {
        if (mMeasureCache != null) mMeasureCache.clear();

        if (mAttachInfo != null && mAttachInfo.mViewRequestingLayout == null) {
            // Only trigger request-during-layout logic if this is the view requesting it,
            // not the views in its parent hierarchy
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot != null && viewRoot.isInLayout()) {
                if (!viewRoot.requestLayoutDuringLayout(this)) {
                    return;
                }
            }
            mAttachInfo.mViewRequestingLayout = this;
        }

        mPrivateFlags |= PFLAG_FORCE_LAYOUT;
        mPrivateFlags |= PFLAG_INVALIDATED;

        if (mParent != null && !mParent.isLayoutRequested()) {
            mParent.requestLayout();
        }
        if (mAttachInfo != null && mAttachInfo.mViewRequestingLayout == this) {
            mAttachInfo.mViewRequestingLayout = null;
        }
    }
```
上面是Android view中该方法的定义，从代码中我们可以看出它首先先判断当前View树是否正在布局流程，接着为当前子View设置标记位，该标记位的作用就是标记了当前的View是需要进行重新布局的，接着调用mParent.requestLayout方法，这个十分重要，因为这里是向**父容器请求布局**，即调用父容器的**requestLayout**方法，为父容器添加PFLAG_FORCE_LAYOUT标记位，而父容器又会调用它的父容器的requestLayout方法，即requestLayout事件层层向上传递，直到DecorView，即根View，而根View又会传递给ViewRootImpl，也即是说子View的requestLayout事件，最终会被ViewRootImpl接收并得到处理。可以看出这种向上传递的流程，其实是采用了责任链模式，即不断向上传递该事件，直到找到能处理该事件的上级，在这里，只有ViewRootImpl能够处理requestLayout事件。

```
    @Override
    public void requestLayout() {
        if (!mHandlingLayoutInLayoutRequest) {
            checkThread();
            mLayoutRequested = true;
            scheduleTraversals();
        }
    }
```

我们进一步深入，可以看出在ViewRootImpl中，重写了requestLayout方法。
在这里，调用了scheduleTraversals方法，这个方法是一个异步方法，最终会调用到ViewRootImpl#performTraversals方法，这也是View工作流程的核心方法，在这个方法内部，分别调用measure、layout、draw方法来进行View的三大工作流程，对于三大工作流程，前几篇文章已经详细讲述了，这里再做一点补充说明。
先看View#measure方法：
```
public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
     ...

    if ((mPrivateFlags & PFLAG_FORCE_LAYOUT) == PFLAG_FORCE_LAYOUT ||
            widthMeasureSpec != mOldWidthMeasureSpec ||
            heightMeasureSpec != mOldHeightMeasureSpec) {
        ...省略无关代码...
        if (cacheIndex < 0 || sIgnoreMeasureCache) {
            // measure ourselves, this should set the measured dimension flag back
            onMeasure(widthMeasureSpec, heightMeasureSpec);
            mPrivateFlags3 &= ~PFLAG3_MEASURE_NEEDED_BEFORE_LAYOUT;
        }
        ...省略无关代码...
        mPrivateFlags |= PFLAG_LAYOUT_REQUIRED;
    }
}

```
首先是判断一下标记位，如果当前View的标记位为**PFLAG_FORCE_LAYOUT**，那么就会进行测量流程，调用**onMeasure**，对该View进行测量，接着最后为标记位设置为**PFLAG_LAYOUT_REQUIRED**,这个标记位的作用就是在View的layout流程中，如果当前View设置了该标记位，则会进行布局流程。具体可以看如下View#layout源码：

```
public void layout(int l, int t, int r, int b) {
     ...省略无关代码...
    //判断标记位是否为PFLAG_LAYOUT_REQUIRED，如果有，则对该View进行布局
    if (changed || (mPrivateFlags & PFLAG_LAYOUT_REQUIRED) == PFLAG_LAYOUT_REQUIRED) {
        onLayout(changed, l, t, r, b);
        //onLayout方法完成后，清除PFLAG_LAYOUT_REQUIRED标记位
        mPrivateFlags &= ~PFLAG_LAYOUT_REQUIRED;
        ListenerInfo li = mListenerInfo;
        if (li != null && li.mOnLayoutChangeListeners != null) {
            ArrayList<OnLayoutChangeListener> listenersCopy =
                    (ArrayList<OnLayoutChangeListener>)li.mOnLayoutChangeListeners.clone();
            int numListeners = listenersCopy.size();
            for (int i = 0; i < numListeners; ++i) {
                listenersCopy.get(i).onLayoutChange(this, l, t, r, b, oldL, oldT, oldR, oldB);
            }
        }
    }

    //最后清除PFLAG_FORCE_LAYOUT标记位
    mPrivateFlags &= ~PFLAG_FORCE_LAYOUT;
    mPrivateFlags3 |= PFLAG3_IS_LAID_OUT;
}
```
从上面的分析可以看出当子View调用requestLayout方法，会标记当前View及父容器，同时逐层向上提交，直到ViewRootImpl处理该事件，ViewRootImpl会调用三大流程，从measure开始，对于每一个含有标记位的view及其子View都会进行测量、布局、绘制。

另外我也在这里简单介绍下当调用invalidate和postInvalidate时，View的内部调用逻辑。
直接上结论：
当子View调用了invalidate方法后，会为该View添加一个标记位，同时不断向父容器请求刷新，父容器通过计算得出自身需要重绘的区域，直到传递到ViewRootImpl中，最终触发performTraversals方法，进行开始View树重绘流程(只绘制需要重绘的视图)。

![View的生命周期](https://upload-images.jianshu.io/upload_images/4334234-08e71dcc2958da96.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

回到，XimageView中，我们知道，当调用requestLayout时会调用 onMeasure和onLayout以及onDraw函数，因为比例发生变化，我们需要重新测量，方法如下：

```
@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 优先级从大到小：
        // mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
        // mWidthRatio mHeightRatio
        if (mDrawableSizeRatio > 0) {
            // 根据前景图宽高比例来测量view的大小
            if (mIsWidthFitDrawableSizeRatio) {
                mWidthRatio = mDrawableSizeRatio;
            } else if (mIsHeightFitDrawableSizeRatio) {
                mHeightRatio = 1 / mDrawableSizeRatio;
            }
        }

        if (mHeightRatio > 0 && mWidthRatio > 0) {
            throw new RuntimeException("高度和宽度不能同时设置百分比！！");
        }

        if (mWidthRatio > 0) { // 高度已知，根据比例，设置宽度
            int height = 0;
            if (mDesiredHeight > 0) {
                height = mDesiredHeight;
            } else {
                height = MeasureSpec.getSize(heightMeasureSpec);
            }
            int width = (int) (height * mWidthRatio);
            if (mIsWidthFitDrawableSizeRatio && mMaxWidthWhenWidthFixDrawable > 0
                    && width > mMaxWidthWhenWidthFixDrawable) { // 限制最大宽度
                width = mMaxWidthWhenWidthFixDrawable;
                height = (int) (width / mWidthRatio);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else if (mHeightRatio > 0) { // 宽度已知，根据比例，设置高度
            int width = 0;
            if (mDesiredWidth > 0) {
                width = mDesiredWidth;
            } else {
                width = MeasureSpec.getSize(widthMeasureSpec);
            }
            int height = (int) (width * mHeightRatio);
            if (mIsHeightFitDrawableSizeRatio && mMaxHeightWhenHeightFixDrawable > 0
                    && height > mMaxHeightWhenHeightFixDrawable) { // 限制最大高度
                height = mMaxHeightWhenHeightFixDrawable;
                width = (int) (height / mHeightRatio);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else if (mDesiredHeight > 0 && mDesiredWidth > 0) { // 当没有设置其他属性时，width和height必须同时设置才生效
            int width = mDesiredWidth;
            int height = mDesiredHeight;
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else { // 系统默认测量
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
```
代码就是根据当前配置的比例对widthMeasureSpec和heightMeasureSpec重新赋值。

因为我们并没有改变ImageView的布局和绘制，所以当重新测量后，仍会按系统默认的方式重新布局和绘制。




