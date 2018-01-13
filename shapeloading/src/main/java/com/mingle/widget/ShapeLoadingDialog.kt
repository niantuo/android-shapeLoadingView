package com.mingle.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.mingle.shapeloading.R
import org.jetbrains.anko.*

/**
 * Created by niantuo on 2018/1/13.
 * 自定义的加载框
 */
class ShapeLoadingDialog(private val mContext: Context) : AnkoLogger {


    private var mDialog: Dialog
    private lateinit var mLoadingView: LoadingView
    private var mDialogContentView: View


    init {
        mDialogContentView = createView()
        mDialog = Dialog(this.mContext, R.style.custom_dialog)
        mDialog.setContentView(mDialogContentView)

        info("loadingView: $mLoadingView")
    }


    private fun createView(): View {
        val mWidth = mContext.resources.displayMetrics.widthPixels * 0.8
        return AnkoContext.create(mContext)
                .apply {
                    relativeLayout {
                        padding = dip(10)
                        backgroundResource = R.drawable.aa_dialog_bg
                        mLoadingView = loadingView {
                        }.lparams(mWidth.toInt(), wrapContent) {
                            centerInParent()
                        }
                    }
                }.view
    }


    fun setBackground(color: Int) {
        val gradientDrawable = this.mDialogContentView.background as GradientDrawable
        gradientDrawable.setColor(color)
    }

    fun setLoadingText(charSequence: CharSequence): ShapeLoadingDialog {
        info("setLoadingText :  $charSequence")
        mLoadingView.loadingText = charSequence
        return this
    }

    fun show():ShapeLoadingDialog {
        if (!mDialog.isShowing)
            this.mDialog.show()
        return this
    }


    fun dismiss() {
        this.mDialog.dismiss()
    }

    fun getDialog(): Dialog {
        return this.mDialog
    }

    fun setCanceledOnTouchOutside(cancel: Boolean) {
        this.mDialog.setCanceledOnTouchOutside(cancel)
    }
}