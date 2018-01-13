package com.mingle.widget

import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

/**
 * Created by niantuo on 2018/1/13.
 * 添加anko支持
 */
inline fun ViewManager.shapeLoadingView(init: ShapeLoadingView.() -> Unit): ShapeLoadingView {
    return ankoView({ ShapeLoadingView(it) }, 0, init)
}


inline fun ViewManager.loadingView(init: LoadingView.() -> Unit): LoadingView {
    return ankoView({ LoadingView(it) }, 0, init)
}
