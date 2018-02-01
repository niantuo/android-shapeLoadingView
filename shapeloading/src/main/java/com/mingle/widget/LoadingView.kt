package com.mingle.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mingle.shapeloading.R
import com.nineoldandroids.animation.Animator
import com.nineoldandroids.animation.AnimatorSet
import com.nineoldandroids.animation.ObjectAnimator
import com.nineoldandroids.view.ViewHelper
import org.jetbrains.anko.*


/**
 * Created by zzz40500 on 15/4/6.
 * 改用kotlin语言编写
 */
class LoadingView(context: Context, attrs: AttributeSet? = null,
                  defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private var mShapeLoadingView: ShapeLoadingView? = null
    private var mIndicationIm: ImageView? = null
    private var mLoadTextView: TextView
    private var mUpAnimatorSet: AnimatorSet? = null
    private var mDownAnimatorSet: AnimatorSet? = null

    private var mStopped = false

    var delay: Int = 0

    private val mFreeFallRunnable = Runnable {
        ViewHelper.setRotation(mShapeLoadingView, 180f)
        ViewHelper.setTranslationY(mShapeLoadingView, 0f)
        ViewHelper.setScaleX(mIndicationIm, 0.2f)
        mStopped = false
        freeFall()
    }

    var loadingText: CharSequence?
        get() = mLoadTextView.text
        set(loadingText) {
            if (TextUtils.isEmpty(loadingText)) {
                mLoadTextView.visibility = View.GONE
            } else {
                mLoadTextView.visibility = View.VISIBLE
            }
            mLoadTextView.text = loadingText
        }


    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    init {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER_HORIZONTAL
        minimumWidth = (resources.displayMetrics.widthPixels * 0.6).toInt()
        mDistance = dip(54).toFloat()
        mShapeLoadingView = shapeLoadingView {
            val lp = LayoutParams(dip(24), dip(24))
            lp.topMargin = dip(2)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = lp
        }

        mIndicationIm = imageView {
            val lp = LayoutParams(dip(23), dip(3))
            lp.gravity = Gravity.CENTER_HORIZONTAL
            lp.topMargin = dip(56)
            layoutParams = lp
            setImageResource(R.drawable.shadow)
        }

        mLoadTextView = textView {
            val lp = LayoutParams(matchParent, wrapContent)
            lp.gravity = Gravity.CENTER_HORIZONTAL
            lp.topMargin = dip(18)
            layoutParams = lp
            setTextColor(Color.parseColor("#757575"))
            textSize = 14f
            visibility = View.INVISIBLE
            gravity = Gravity.CENTER_HORIZONTAL
        }
        ViewHelper.setScaleX(mIndicationIm!!, 0.2f)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
        val loadText = typedArray.getString(R.styleable.LoadingView_loadingText)
        val textAppearance = typedArray.getResourceId(R.styleable.LoadingView_loadingText, -1)
        delay = typedArray.getInteger(R.styleable.LoadingView_delay, 80)
        typedArray.recycle()

        if (textAppearance != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mLoadTextView.setTextAppearance(textAppearance)
            } else {
                mLoadTextView.setTextAppearance(getContext(), textAppearance)
            }
        }
        loadingText = loadText
    }

    private fun startLoading(delay: Long) {
        if (mDownAnimatorSet != null && mDownAnimatorSet!!.isRunning) {
            return
        }
        this.removeCallbacks(mFreeFallRunnable)
        if (delay > 0) {
            this.postDelayed(mFreeFallRunnable, delay)
        } else {
            this.post(mFreeFallRunnable)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (visibility == View.VISIBLE) {
            startLoading(delay.toLong())
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopLoading()
    }

    private fun stopLoading() {
        mStopped = true
        if (mUpAnimatorSet != null) {
            if (mUpAnimatorSet!!.isRunning) {
                mUpAnimatorSet!!.cancel()
            }
            mUpAnimatorSet!!.removeAllListeners()
            for (animator in mUpAnimatorSet!!.childAnimations) {
                animator.removeAllListeners()
            }
            mUpAnimatorSet = null
        }
        if (mDownAnimatorSet != null) {
            if (mDownAnimatorSet!!.isRunning) {
                mDownAnimatorSet!!.cancel()
            }
            mDownAnimatorSet!!.removeAllListeners()
            for (animator in mDownAnimatorSet!!.childAnimations) {
                animator.removeAllListeners()
            }
            mDownAnimatorSet = null
        }
        this.removeCallbacks(mFreeFallRunnable)
    }

    override fun setVisibility(visibility: Int) {
        this.setVisibility(visibility, delay)
    }

    fun setVisibility(visibility: Int, delay: Int) {
        super.setVisibility(visibility)
        if (visibility == View.VISIBLE) {
            startLoading(delay.toLong())
        } else {
            stopLoading()
        }
    }

    /**
     * 上抛
     */
    fun upThrow() {

        if (mUpAnimatorSet == null) {
            val objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView, "translationY", mDistance, 0f)
            val scaleIndication = ObjectAnimator.ofFloat(mIndicationIm, "scaleX", 1f, 0.2f)

            var objectAnimator1: ObjectAnimator? = null
            when {
                mShapeLoadingView!!.shape == ShapeLoadingView.Shape.SHAPE_RECT -> objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0f, 180f)
                mShapeLoadingView!!.shape == ShapeLoadingView.Shape.SHAPE_CIRCLE -> objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0f, 180f)
                mShapeLoadingView!!.shape == ShapeLoadingView.Shape.SHAPE_TRIANGLE -> objectAnimator1 = ObjectAnimator.ofFloat(mShapeLoadingView, "rotation", 0f, 180f)
            }

            mUpAnimatorSet = AnimatorSet()
            mUpAnimatorSet!!.playTogether(objectAnimator, objectAnimator1, scaleIndication)

            mUpAnimatorSet!!.duration = ANIMATION_DURATION.toLong()
            mUpAnimatorSet!!.setInterpolator(DecelerateInterpolator(FACTOR))

            mUpAnimatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!mStopped) {
                        freeFall()
                    }

                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }
        mUpAnimatorSet!!.start()


    }

    /**
     * 下落
     */
    fun freeFall() {

        if (mDownAnimatorSet == null) {
            val objectAnimator = ObjectAnimator.ofFloat(mShapeLoadingView, "translationY", 0f, mDistance)
            val scaleIndication = ObjectAnimator.ofFloat(mIndicationIm, "scaleX", 0.2f, 1f)

            mDownAnimatorSet = AnimatorSet()
            mDownAnimatorSet!!.playTogether(objectAnimator, scaleIndication)
            mDownAnimatorSet!!.duration = ANIMATION_DURATION.toLong()
            mDownAnimatorSet!!.setInterpolator(AccelerateInterpolator(FACTOR))
            mDownAnimatorSet!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!mStopped) {
                        mShapeLoadingView!!.changeShape()
                        upThrow()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }
        mDownAnimatorSet!!.start()
    }

    companion object {

        private val ANIMATION_DURATION = 500

        private val FACTOR = 1.2f

        private var mDistance = 200f
    }

}
