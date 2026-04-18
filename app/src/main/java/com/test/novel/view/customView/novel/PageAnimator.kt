package com.test.novel.view.customView.novel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import kotlin.math.abs

/**
 * 页面动画基础接口
 */
abstract class PageAnimator(protected val pageTurnView: PageTurnView) {
    // 动画插值器
    protected val myInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    // 动画控制器
    protected var animator: ValueAnimator? = null
    // 页面视图的引用
    protected val previousPage: View?
        get() = pageTurnView.getPreviousPage()

    protected val currentPage: View?
        get() = pageTurnView.getCurrentPage()

    protected val nextPage: View?
        get() = pageTurnView.getNextPage()

    // 页面初始化
    open fun onPageLoaded() {
        // 初始化页面状态
        previousPage?.visibility = View.VISIBLE
        currentPage?.visibility = View.VISIBLE
        nextPage?.visibility = View.VISIBLE
    }

    // 页面布局变化
    open fun onLayout() {}

    // 开始翻页
    open fun onFlipStart(direction: Int) {
        if (direction > 0) {
            // 向右翻，准备显示上一页
            previousPage?.visibility = View.VISIBLE
        } else {
            // 向左翻，准备显示下一页
            nextPage?.visibility = View.VISIBLE
        }
    }

    // 翻页过程
    abstract fun onFlipProgress(direction: Int, progress: Float, distance: Float)

    // 翻页结束
    abstract fun onFlipEnd(direction: Int, completed: Boolean, callback: () -> Unit)

    // 直接翻页动画
    abstract fun flipNextPage(callback: () -> Unit)

    abstract fun flipPreviousPage(callback: () -> Unit)

    // 自定义绘制
    open fun onDraw(canvas: Canvas) {}

    // 清除动画状态
    open fun clearAnimation() {
        animator?.cancel()
        animator = null
    }
}

/**
 * 覆盖效果的翻页动画
 * 前一页滑入覆盖当前页，或当前页滑出露出下一页
 */
class CoverPageAnimator(pageTurnView: PageTurnView) : PageAnimator(pageTurnView) {

    override fun onLayout() {
        super.onLayout()
        val width = pageTurnView.width
        val height = pageTurnView.height

        // 上一页初始位于屏幕左侧外
        previousPage?.apply {
            layout(-width, 0, 0, height)
            translationX = 0f  // 不要使用translationX，使用layout定位
            elevation = 20f    // 最上层
        }

        // 当前页正常显示
        currentPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 10f    // 中间层
        }

        // 下一页与当前页重叠，在底层
        nextPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 5f     // 最下层
        }
    }

    override fun onPageLoaded() {
        super.onPageLoaded()

        previousPage?.visibility = View.VISIBLE
        currentPage?.visibility = View.VISIBLE
        nextPage?.visibility = View.VISIBLE
    }

    override fun onFlipProgress(direction: Int, progress: Float, distance: Float) {
        if (direction > 0) {
            // 向右翻，当前页保持不动，前一页从左侧进入覆盖
            previousPage?.translationX = distance
        } else {
            // 向左翻，当前页向左移出，下一页保持不动（已在当前页下方）
            currentPage?.translationX = distance.coerceAtMost(0f)
        }
    }

    override fun onFlipEnd(direction: Int, completed: Boolean, callback: () -> Unit) {
        // 取消之前的动画
        clearAnimation()
        val width = pageTurnView.width
        Log.d("TAG", "onFlipEnd: $completed")
        if (completed) {
            // 翻页完成
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 250
                interpolator = myInterpolator

                if (direction > 0) {
                    // 向右翻完成：前一页继续向右移动覆盖当前页
                    val startTranslation = previousPage?.translationX ?: 0f
                    val endTranslation = width.toFloat()

                    addUpdateListener { animation ->
                        val fraction = animation.animatedValue as Float
                        previousPage?.translationX = startTranslation + (endTranslation - startTranslation) * fraction
                    }
                } else {
                    // 向左翻完成：当前页继续向左移出屏幕
                    val startTranslation = currentPage?.translationX ?: 0f
                    val endTranslation = -width.toFloat()

                    addUpdateListener { animation ->
                        val fraction = animation.animatedValue as Float
                        currentPage?.translationX = startTranslation + (endTranslation - startTranslation) * fraction
                    }
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        callback()
                    }
                })

                start()
            }
        } else {
            // 取消翻页，恢复原状
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                interpolator = myInterpolator

                if (direction > 0) {
                    // 取消向右翻：前一页回到左侧屏幕外
                    val startTranslation = previousPage?.translationX ?: 0f

                    addUpdateListener { animation ->
                        val fraction = animation.animatedValue as Float
                        previousPage?.translationX = startTranslation * (1 - fraction)
                    }
                } else {
                    // 取消向左翻：当前页回到原位
                    val startTranslation = currentPage?.translationX ?: 0f

                    addUpdateListener { animation ->
                        val fraction = animation.animatedValue as Float
                        currentPage?.translationX = startTranslation * (1 - fraction)
                    }
                }

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        callback()
                    }
                })

                start()
            }
        }
    }

    override fun flipNextPage(callback: () -> Unit) {
        animator = ValueAnimator.ofFloat(0f,1f).apply {
            duration = 200
            interpolator = myInterpolator
            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float
                currentPage?.translationX = -pageTurnView.width * fraction
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    callback()
                }
            })
            start()
        }
    }

    override fun flipPreviousPage(callback: () -> Unit) {
        animator = ValueAnimator.ofFloat(0f,1f).apply {
            duration = 200
            interpolator = myInterpolator
            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float
                previousPage?.translationX = pageTurnView.width * fraction
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    callback()
                }
            })
            start()
        }
    }

    override fun clearAnimation() {
        super.clearAnimation()
        // 重置动画状态
        animator?.cancel()
        animator = null
    }
}

/**
 * 仿真翻页效果动画
 */
class SimulationPageAnimator(pageTurnView: PageTurnView) : PageAnimator(pageTurnView) {

    private val touchPoint = PointF() // 手指触摸位置

    override fun onLayout() {
        super.onLayout()
        val width = pageTurnView.width
        val height = pageTurnView.height

        // 设置相机距离，增强3D效果
        pageTurnView.cameraDistance = width * 10f

        // 上一页重叠初始位于当前页顶部
        previousPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 20f
        }

        // 当前页正常显示
        currentPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 10f
        }

        // 下一页与当前页重叠，在底层
        nextPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 5f
        }
    }

    override fun onFlipStart(direction: Int) {
        super.onFlipStart(direction)
        val defaultX = if (direction > 0) 0f else pageTurnView.width.toFloat()
        val fallbackY = pageTurnView.height / 2f
        val touchY = pageTurnView.getLastTouchY().takeIf { it in 0f..pageTurnView.height.toFloat() } ?: fallbackY
        val touchX = pageTurnView.getLastTouchX().takeIf { it in 0f..pageTurnView.width.toFloat() } ?: defaultX
        touchPoint.set(touchX, touchY)

        if (direction > 0) {
            applyPreviousPageProgress(0f)
        } else {
            applyNextPageProgress(0f)
        }
    }

    override fun onFlipProgress(direction: Int, progress: Float, distance: Float) {
        if (direction > 0) {
            touchPoint.x = (progress * pageTurnView.width).coerceIn(0f, pageTurnView.width.toFloat())
            touchPoint.y = pageTurnView.getLastTouchY()
                .takeIf { it in 0f..pageTurnView.height.toFloat() }
                ?: (pageTurnView.height / 2f)
            applyPreviousPageProgress(progress)
        } else {
            touchPoint.x = pageTurnView.getLastTouchX()
                .takeIf { it in 0f..pageTurnView.width.toFloat() }
                ?: (pageTurnView.width - progress * pageTurnView.width)
            touchPoint.y = pageTurnView.getLastTouchY()
                .takeIf { it in 0f..pageTurnView.height.toFloat() }
                ?: (pageTurnView.height / 2f)
            applyNextPageProgress(progress)
        }
    }

    override fun onFlipEnd(direction: Int, completed: Boolean, callback: () -> Unit) {
        val startProgress = if (direction > 0) {
            ((previousPage?.clipBounds?.right ?: 0).toFloat() / pageTurnView.width).coerceIn(0f, 1f)
        } else {
            ((currentPage?.rotationY ?: 0f) / 72f).coerceIn(0f, 1f)
        }
        val endProgress = if (completed) 1f else 0f

        super.clearAnimation()
        previousPage?.animate()?.cancel()
        currentPage?.animate()?.cancel()
        nextPage?.animate()?.cancel()

        animator = ValueAnimator.ofFloat(startProgress, endProgress).apply {
            duration = if (completed) 260 else 180
            interpolator = myInterpolator
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                if (direction > 0) {
                    applyPreviousPageProgress(value)
                } else {
                    applyNextPageProgress(value)
                }
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!completed) {
                        resetSimulationState()
                    }
                    callback()
                }
            })
            start()
        }
    }

    override fun flipNextPage(callback: () -> Unit) {
        // 实现自动翻页动画
        onFlipStart(-1)
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                onFlipProgress(-1, progress, -pageTurnView.width * progress)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onFlipEnd(-1, true, callback)
                }
            })
            start()
        }
    }

    override fun flipPreviousPage(callback: () -> Unit) {
        // 实现自动翻页动画
        onFlipStart(1)
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                onFlipProgress(1, progress, pageTurnView.width * progress)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onFlipEnd(1, true, callback)
                }
            })
            start()
        }
    }

    override fun onPageLoaded() {
        super.onPageLoaded()
        resetSimulationState()
    }

    override fun clearAnimation() {
        super.clearAnimation()
        previousPage?.animate()?.cancel()
        currentPage?.animate()?.cancel()
        nextPage?.animate()?.cancel()
        resetSimulationState()
    }

    private fun applyPreviousPageProgress(rawProgress: Float) {
        val progress = rawProgress.coerceIn(0f, 1f)
        val width = pageTurnView.width
        val height = pageTurnView.height
        val revealWidth = (width * progress).toInt().coerceIn(0, width)

        previousPage?.apply {
            visibility = View.VISIBLE
            pivotX = 0f
            pivotY = height / 2f
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            translationX = 0f
            translationY = 0f
            translationZ = 120f * progress
            alpha = 0.82f + 0.18f * progress
            clipBounds = Rect(0, 0, revealWidth, height)
        }

        currentPage?.apply {
            visibility = View.VISIBLE
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            pivotX = width.toFloat()
            pivotY = height / 2f
            translationX = width * 0.035f * progress
            translationY = 0f
            translationZ = -90f * progress
            alpha = 1f - 0.12f * progress
            clipBounds = Rect(revealWidth, 0, width, height)
        }

        nextPage?.apply {
            visibility = View.INVISIBLE
            alpha = 1f
            clipBounds = Rect(width, 0, width, height)
        }
    }

    private fun applyNextPageProgress(rawProgress: Float) {
        val progress = rawProgress.coerceIn(0f, 1f)
        val width = pageTurnView.width.toFloat()
        val height = pageTurnView.height.toFloat()
        val useTopCorner = touchPoint.y <= height / 2f
        val cornerY = if (useTopCorner) 0f else height
        val cornerSign = if (useTopCorner) -1f else 1f
        val diagonalInfluence = (abs(touchPoint.y - cornerY) / height).coerceIn(0.15f, 1f)
        val revealLeft = (width * (1f - progress * 0.94f)).toInt().coerceIn(0, width.toInt())

        currentPage?.apply {
            visibility = View.VISIBLE
            pivotX = width
            pivotY = cornerY
            rotationY = 72f * progress
            rotationX = cornerSign * 10f * diagonalInfluence * progress
            rotation = cornerSign * (18f * diagonalInfluence) * progress
            translationX = -width * 0.1f * progress
            translationY = cornerSign * height * 0.045f * progress
            translationZ = -160f * progress
            scaleY = 1f - 0.05f * progress
            alpha = 1f - 0.18f * progress
            clipBounds = null
        }

        nextPage?.apply {
            visibility = View.VISIBLE
            pivotX = width
            pivotY = cornerY
            rotationY = -22f * (1f - progress)
            rotationX = -cornerSign * 5f * (1f - progress)
            rotation = -cornerSign * 8f * (1f - progress)
            translationX = -width * 0.03f * (1f - progress)
            translationY = cornerSign * height * 0.018f * (1f - progress)
            translationZ = 90f * progress
            scaleY = 0.985f + 0.015f * progress
            alpha = 0.72f + 0.28f * progress
            clipBounds = Rect(revealLeft, 0, width.toInt(), height.toInt())
        }

        previousPage?.apply {
            visibility = View.INVISIBLE
            clipBounds = Rect(0, 0, 0, height.toInt())
        }
    }

    private fun resetSimulationState() {
        val width = pageTurnView.width
        val height = pageTurnView.height

        previousPage?.apply {
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            translationX = 0f
            translationY = 0f
            translationZ = 0f
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
            clipBounds = Rect(0, 0, 0, height)
            visibility = View.INVISIBLE
        }
        currentPage?.apply {
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            translationX = 0f
            translationY = 0f
            translationZ = 0f
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
            clipBounds = null
            visibility = View.VISIBLE
        }
        nextPage?.apply {
            rotation = 0f
            rotationX = 0f
            rotationY = 0f
            translationX = 0f
            translationY = 0f
            translationZ = 0f
            scaleX = 1f
            scaleY = 1f
            alpha = 1f
            clipBounds = Rect(width, 0, width, height)
            visibility = View.INVISIBLE
        }
    }
}

/**
 * 逐行显示动画效果
 * 从左往右一排一排像素让View从不可见到可见
 */
class LineByLinePageAnimator(pageTurnView: PageTurnView) : PageAnimator(pageTurnView) {

    private val touchPoint = PointF() // 手指触摸位置
    private var lineHeight = 20f // 每行的高度

    override fun onLayout() {
        super.onLayout()
        val width = pageTurnView.width
        val height = pageTurnView.height

        // 上一页与当前页重叠，初始时完全不可见（裁剪为0宽度）
        previousPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 20f // 确保上一页在当前页之上
            visibility = View.INVISIBLE
            // 初始裁剪为0宽度
            clipBounds = android.graphics.Rect(0, 0, 0, height)
        }

        // 当前页正常显示
        currentPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 10f
        }

        // 下一页与当前页重叠，初始时完全不可见
        nextPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 5f
            visibility = View.INVISIBLE
            // 初始裁剪为0宽度（从右侧开始）
            clipBounds = android.graphics.Rect(width, 0, width, height)
        }
    }

    override fun onFlipStart(direction: Int) {
        super.onFlipStart(direction)
        // 初始化触摸点
        touchPoint.set(if (direction > 0) 0f else pageTurnView.width.toFloat(), pageTurnView.height / 2f)

        // 准备翻页时显示相应页面
        if (direction > 0) {
            // 向右翻，显示上一页
            previousPage?.visibility = View.VISIBLE
        } else {
            // 向左翻，显示下一页
            nextPage?.visibility = View.VISIBLE
        }
    }

    override fun onFlipProgress(direction: Int, progress: Float, distance: Float) {
        val width = pageTurnView.width
        val height = pageTurnView.height

        // 更新触摸点位置
        if (direction > 0) {
            touchPoint.x = progress * width
        } else {
            touchPoint.x = width - progress * width
        }

        if (direction > 0) {
            // 向右翻，上一页从左往右逐渐显示（不移动位置）
            previousPage?.let {
                // 设置裁剪区域，实现从左往右逐渐显示效果
                val clipWidth = (progress * width).toInt()
                it.clipBounds = android.graphics.Rect(0, 0, clipWidth, height)
            }

            currentPage?.let {
                // 当前页稍微变暗，增强层次感
                it.alpha = 1f - progress * 0.3f
            }
        } else {
            // 向左翻，下一页从右往左逐渐显示（不移动位置）
            nextPage?.let {
                // 设置裁剪区域，实现从右往左逐渐显示效果
                val clipWidth = (progress * width).toInt()
                it.clipBounds = android.graphics.Rect(width - clipWidth, 0, width, height)
            }

            currentPage?.let {
                // 当前页稍微变暗，增强层次感
                it.alpha = 1f - progress * 0.3f
            }
        }
    }

    override fun onFlipEnd(direction: Int, completed: Boolean, callback: () -> Unit) {
        if (completed) {
            // 翻页完成
            if (direction > 0) {
                // 向右翻到上一页
                currentPage?.animate()
                    ?.alpha(0f)
                    ?.setDuration(200)
                    ?.withEndAction {
                        // 清除裁剪区域
                        previousPage?.clipBounds = null
                        currentPage?.alpha = 1f
                        callback()
                    }
                    ?.start()

                previousPage?.animate()
                    ?.alpha(1f)
                    ?.setDuration(200)
                    ?.start()
            } else {
                // 向左翻到下一页
                currentPage?.animate()
                    ?.alpha(0f)
                    ?.setDuration(200)
                    ?.start()

                nextPage?.animate()
                    ?.alpha(1f)
                    ?.setDuration(200)
                    ?.withEndAction {
                        // 清除裁剪区域
                        nextPage?.clipBounds = null
                        currentPage?.alpha = 1f
                        callback()
                    }
                    ?.start()
            }
        } else {
            // 取消翻页，恢复原状
            currentPage?.animate()
                ?.alpha(1f)
                ?.setDuration(200)
                ?.start()

            if (direction > 0) {
                previousPage?.animate()
                    ?.alpha(0f)
                    ?.setDuration(200)
                    ?.withEndAction {
                        previousPage?.visibility = View.INVISIBLE
                        previousPage?.clipBounds = android.graphics.Rect(0, 0, 0, pageTurnView.height)
                        callback()
                    }
                    ?.start()
            } else {
                nextPage?.animate()
                    ?.alpha(0f)
                    ?.setDuration(200)
                    ?.withEndAction {
                        nextPage?.visibility = View.INVISIBLE
                        nextPage?.clipBounds = android.graphics.Rect(pageTurnView.width, 0, pageTurnView.width, pageTurnView.height)
                        callback()
                    }
                    ?.start()
            }
        }
    }

    override fun flipNextPage(callback: () -> Unit) {
        // 实现自动翻页动画
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                onFlipProgress(-1, progress, -pageTurnView.width * progress)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onFlipEnd(-1, true, callback)
                }
            })
            start()
        }
    }

    override fun flipPreviousPage(callback: () -> Unit) {
        // 实现自动翻页动画
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                onFlipProgress(1, progress, pageTurnView.width * progress)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onFlipEnd(1, true, callback)
                }
            })
            start()
        }
    }

    override fun onPageLoaded() {
        super.onPageLoaded()
        val height = pageTurnView.height
        val width = pageTurnView.width

        // 重置状态
        previousPage?.apply {
            translationX = 0f
            alpha = 1f
            visibility = View.INVISIBLE
            // 重置裁剪区域
            clipBounds = android.graphics.Rect(0, 0, 0, height)
        }
        currentPage?.apply {
            translationX = 0f
            alpha = 1f
        }
        nextPage?.apply {
            translationX = 0f
            alpha = 1f
            visibility = View.INVISIBLE
            // 重置裁剪区域
            clipBounds = android.graphics.Rect(width, 0, width, height)
        }
    }

    override fun clearAnimation() {
        super.clearAnimation()
        previousPage?.animate()?.cancel()
        currentPage?.animate()?.cancel()
        nextPage?.animate()?.cancel()
    }
}

/**
 * 平移翻页动画
 * 所有页面在同一平面上水平排列，像轮播图一样滑动切换
 */
class TranslationPageAnimator(pageTurnView: PageTurnView) : PageAnimator(pageTurnView) {

    override fun onLayout() {
        super.onLayout()
        val width = pageTurnView.width
        val height = pageTurnView.height

        // 所有页面都在同一平面上 (相同elevation)
        // 上一页位于左侧
        previousPage?.apply {
            layout(-width, 0, 0, height)
            translationX = 0f
            elevation = 0f
            visibility = View.VISIBLE
        }

        // 当前页在中间
        currentPage?.apply {
            layout(0, 0, width, height)
            translationX = 0f
            elevation = 0f
            visibility = View.VISIBLE
        }

        // 下一页在右侧
        nextPage?.apply {
            layout(width, 0, 2 * width, height)
            translationX = 0f
            elevation = 0f
            visibility = View.VISIBLE
        }
    }

    override fun onPageLoaded() {
        // 在平移动画中，所有页面默认都是可见的
        previousPage?.visibility = View.VISIBLE
        currentPage?.visibility = View.VISIBLE
        nextPage?.visibility = View.VISIBLE
    }

    override fun onFlipProgress(direction: Int, progress: Float, distance: Float) {
        // 所有页面一起移动，保持相对位置
        currentPage?.translationX = distance
        previousPage?.translationX = distance
        nextPage?.translationX = distance
    }

    override fun onFlipEnd(direction: Int, completed: Boolean, callback: () -> Unit) {
        // 取消可能正在进行的动画
        clearAnimation()

        val width = pageTurnView.width

        // 获取当前的位置作为起始点
        val currentTranslationX = currentPage?.translationX ?: 0f

        animator = ValueAnimator.ofFloat(currentTranslationX,
            if (completed) {
                if (direction > 0) width.toFloat() else -width.toFloat()
            } else {
                0f
            }
        ).apply {
            duration = 200
            interpolator = myInterpolator

            addUpdateListener { animation ->
                val value = animation.animatedValue as Float

                // 移动所有页面，保持相对位置
                currentPage?.translationX = value
                previousPage?.translationX = value
                nextPage?.translationX = value
            }

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!completed) {
                        // 如果是取消翻页，则回到初始状态后直接调用回调
                        callback()
                    } else {
                        // 如果是完成翻页，则在回调之前先确保所有页面平滑过渡
                        // 动画结束时让系统去更新页面状态
                        callback()
                    }
                }
            })
            start()
        }
    }

    override fun flipNextPage(callback: () -> Unit) {
        val width = pageTurnView.width
        animator = ValueAnimator.ofFloat(0f,-width.toFloat()).apply {
            duration = 200
            interpolator = myInterpolator
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float

                // 移动所有页面，保持相对位置
                currentPage?.translationX = value
                previousPage?.translationX = value
                nextPage?.translationX = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    callback()
                }
            })
            start()
        }
    }

    override fun flipPreviousPage(callback: () -> Unit) {
        val width = pageTurnView.width
        animator = ValueAnimator.ofFloat(0f,width.toFloat()).apply {
            duration = 200
            interpolator = myInterpolator
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                // 移动所有页面，保持相对位置
                currentPage?.translationX = value
                previousPage?.translationX = value
                nextPage?.translationX = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    callback()
                }
            })
            start()
        }
    }

    override fun clearAnimation() {
        super.clearAnimation()

        // 取消所有页面上可能存在的动画
        previousPage?.animate()?.cancel()
        currentPage?.animate()?.cancel()
        nextPage?.animate()?.cancel()
    }
}
