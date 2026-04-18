package com.test.novel.view.customView.novel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Rect
import android.graphics.Path
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import kotlin.math.atan2
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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

    private val touchPoint = PointF()
    private val visiblePath = Path()
    private val foldPath = Path()
    private val backPath = Path()
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x28000000
        style = Paint.Style.FILL
    }
    private val backTintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x55F3E6D0
        style = Paint.Style.FILL
    }
    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x22000000
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val shadowRect = RectF()
    private val verticalShadowDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(0x4A000000, 0x16000000, 0x00000000)
    )

    private var currentDirection = 0
    private var currentProgress = 0f
    private var isCustomDrawing = false

    private var frontBitmap: Bitmap? = null
    private var underBitmap: Bitmap? = null

    override fun onLayout() {
        super.onLayout()
        val width = pageTurnView.width
        val height = pageTurnView.height

        previousPage?.layout(0, 0, width, height)
        currentPage?.layout(0, 0, width, height)
        nextPage?.layout(0, 0, width, height)
    }

    override fun onFlipStart(direction: Int) {
        super.onFlipStart(direction)
        currentDirection = direction
        currentProgress = 0f

        val width = pageTurnView.width.toFloat()
        val height = pageTurnView.height.toFloat()
        val defaultTouchX = if (direction > 0) width * 0.18f else width * 0.82f
        val defaultTouchY = height * 0.72f

        val touchX = pageTurnView.getLastTouchX()
            .takeIf { it in 0f..width }
            ?: defaultTouchX
        val touchY = pageTurnView.getLastTouchY()
            .takeIf { it in 0f..height }
            ?: defaultTouchY

        touchPoint.set(touchX, touchY)
        if (direction > 0) {
            isCustomDrawing = false
            setupVerticalRevealStart()
        } else {
            capturePageBitmaps(direction)
            isCustomDrawing = frontBitmap != null && underBitmap != null
            hideAllPagesForCustomDraw()
        }
        pageTurnView.invalidate()
    }

    override fun onFlipProgress(direction: Int, progress: Float, distance: Float) {
        currentDirection = direction
        currentProgress = progress.coerceIn(0f, 1f)

        val width = pageTurnView.width.toFloat()
        val height = pageTurnView.height.toFloat()
        if (direction > 0) {
            touchPoint.x = pageTurnView.getLastTouchX()
                .takeIf { it in 0f..width }
                ?: (currentProgress * width)
            touchPoint.y = pageTurnView.getLastTouchY()
                .takeIf { it in 0f..height }
                ?: (height * 0.72f)
            applyPreviousPageProgress(currentProgress)
            return
        }

        if (!isCustomDrawing) return

        val cornerX = width
        val minGap = width * 0.06f
        touchPoint.x =
            pageTurnView.getLastTouchX()
                .takeIf { it in 0f..width }
                ?.coerceAtLeast(minGap)
                ?: (width - currentProgress * width).coerceIn(minGap, width - minGap)

        if (abs(touchPoint.x - cornerX) < minGap) {
            touchPoint.x = width - minGap
        }

        touchPoint.y = pageTurnView.getLastTouchY()
            .takeIf { it in 0f..height }
            ?: (height * 0.72f)

        pageTurnView.invalidate()
    }

    override fun onFlipEnd(direction: Int, completed: Boolean, callback: () -> Unit) {
        if (direction > 0) {
            finishVerticalReveal(completed, callback)
            return
        }

        if (!isCustomDrawing) {
            callback()
            return
        }

        val startProgress = currentProgress
        val endProgress = if (completed) 1f else 0f
        val startTouch = PointF(touchPoint.x, touchPoint.y)
        val endTouch = computeAutoEndTouch(direction, endProgress)

        super.clearAnimation()
        animator = ValueAnimator.ofFloat(startProgress, endProgress).apply {
            duration = if (completed) 360 else 220
            interpolator = myInterpolator
            addUpdateListener { animation ->
                val fraction = animation.animatedFraction
                currentProgress = animation.animatedValue as Float
                touchPoint.x = lerp(startTouch.x, endTouch.x, fraction)
                touchPoint.y = lerp(startTouch.y, endTouch.y, fraction)
                pageTurnView.invalidate()
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
        onFlipStart(-1)
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 420
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val width = pageTurnView.width.toFloat()
                val height = pageTurnView.height.toFloat()
                touchPoint.x = width - progress * width * 0.94f
                touchPoint.y = height * 0.72f
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
        onFlipStart(1)
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 320
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                val width = pageTurnView.width.toFloat()
                val height = pageTurnView.height.toFloat()
                touchPoint.x = progress * width * 0.94f
                touchPoint.y = height * 0.72f
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

    override fun onDraw(canvas: Canvas) {
        if (!isCustomDrawing) return

        val front = frontBitmap ?: return
        val under = underBitmap ?: return
        val width = pageTurnView.width.toFloat()
        val height = pageTurnView.height.toFloat()
        if (width <= 0f || height <= 0f) return

        val state = buildFoldState(width, height, currentDirection, touchPoint)
        canvas.drawBitmap(under, 0f, 0f, bitmapPaint)

        visiblePath.reset()
        addPolygonToPath(visiblePath, state.visiblePolygon)
        canvas.save()
        canvas.clipPath(visiblePath)
        canvas.drawBitmap(front, 0f, 0f, bitmapPaint)
        canvas.restore()

        backPath.reset()
        addPolygonToPath(backPath, state.backPolygon)
        canvas.save()
        canvas.clipPath(backPath)
        canvas.concat(state.reflectionMatrix)
        canvas.drawBitmap(front, 0f, 0f, bitmapPaint)
        canvas.restore()

        canvas.save()
        canvas.clipPath(backPath)
        canvas.drawPath(backPath, backTintPaint)
        canvas.restore()

        drawFoldShadow(canvas, state)
        canvas.drawLine(
            state.foldLineStart.x,
            state.foldLineStart.y,
            state.foldLineEnd.x,
            state.foldLineEnd.y,
            edgePaint
        )
    }

    override fun onPageLoaded() {
        super.onPageLoaded()
        resetSimulationState()
    }

    override fun clearAnimation() {
        super.clearAnimation()
        resetSimulationState()
    }

    private fun capturePageBitmaps(direction: Int) {
        releaseBitmaps()
        frontBitmap = captureBitmap(currentPage)
        underBitmap = captureBitmap(if (direction > 0) previousPage else nextPage)
    }

    private fun setupVerticalRevealStart() {
        releaseBitmaps()
        hideVerticalShadow()
        previousPage?.apply {
            visibility = View.VISIBLE
            alpha = 0.84f
            translationX = 0f
            clipBounds = Rect(0, 0, 0, pageTurnView.height)
        }
        currentPage?.apply {
            visibility = View.VISIBLE
            alpha = 1f
            translationX = 0f
            clipBounds = Rect(0, 0, pageTurnView.width, pageTurnView.height)
        }
        nextPage?.apply {
            visibility = View.INVISIBLE
            clipBounds = Rect(pageTurnView.width, 0, pageTurnView.width, pageTurnView.height)
        }
    }

    private fun applyPreviousPageProgress(progress: Float) {
        val width = pageTurnView.width
        val height = pageTurnView.height
        val clampedProgress = progress.coerceIn(0f, 1f)
        val revealWidth = (width * clampedProgress).toInt().coerceIn(0, width)

        previousPage?.apply {
            visibility = View.VISIBLE
            alpha = 0.84f + 0.16f * clampedProgress
            translationX = 0f
            clipBounds = Rect(0, 0, revealWidth, height)
        }
        currentPage?.apply {
            visibility = View.VISIBLE
            alpha = 1f - 0.14f * clampedProgress
            translationX = width * 0.02f * clampedProgress
            clipBounds = Rect(revealWidth, 0, width, height)
        }
        updateVerticalShadow(revealWidth, height, clampedProgress)
    }

    private fun finishVerticalReveal(completed: Boolean, callback: () -> Unit) {
        val width = pageTurnView.width
        val startProgress = ((previousPage?.clipBounds?.right ?: 0).toFloat() / width).coerceIn(0f, 1f)
        val endProgress = if (completed) 1f else 0f

        super.clearAnimation()
        animator = ValueAnimator.ofFloat(startProgress, endProgress).apply {
            duration = if (completed) 240 else 180
            interpolator = myInterpolator
            addUpdateListener { animation ->
                currentProgress = animation.animatedValue as Float
                applyPreviousPageProgress(currentProgress)
                pageTurnView.invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (!completed) {
                        resetVerticalRevealState()
                    }
                    callback()
                }
            })
            start()
        }
    }

    private fun captureBitmap(view: View?): Bitmap? {
        val target = view ?: return null
        val width = target.width.takeIf { it > 0 } ?: pageTurnView.width
        val height = target.height.takeIf { it > 0 } ?: pageTurnView.height
        if (width <= 0 || height <= 0) return null

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bitmapCanvas = Canvas(bitmap)
        target.draw(bitmapCanvas)
        return bitmap
    }

    private fun buildFoldState(
        width: Float,
        height: Float,
        direction: Int,
        rawTouch: PointF
    ): FoldState {
        val anchorX = if (direction > 0) 0f else width
        val anchorY = if (rawTouch.y < height / 2f) 0f else height
        val clampedTouch = PointF(
            rawTouch.x.coerceIn(width * 0.04f, width * 0.96f),
            rawTouch.y.coerceIn(height * 0.04f, height * 0.96f)
        )

        if (abs(clampedTouch.x - anchorX) < width * 0.03f) {
            clampedTouch.x = if (direction > 0) width * 0.03f else width * 0.97f
        }

        val midpoint = PointF(
            (clampedTouch.x + anchorX) / 2f,
            (clampedTouch.y + anchorY) / 2f
        )

        val normalX = anchorX - clampedTouch.x
        val normalY = anchorY - clampedTouch.y
        val foldDirX = -normalY
        val foldDirY = normalX

        val anchorSide = sideOfLine(anchorX, anchorY, midpoint.x, midpoint.y, normalX, normalY)
        val foldPolygon = clipRectByLine(width, height, midpoint, normalX, normalY, keepPositive = anchorSide >= 0f)
        val visiblePolygon = clipRectByLine(width, height, midpoint, normalX, normalY, keepPositive = anchorSide < 0f)
        val backPolygon = foldPolygon.map { reflectPoint(it, midpoint, foldDirX, foldDirY) }

        val reflectionMatrix = buildReflectionMatrix(midpoint, foldDirX, foldDirY)
        val shadowDistance = min(width, height) * 0.045f
        val normalLength = max(kotlin.math.sqrt(normalX * normalX + normalY * normalY), 1f)
        val unitNormalX = normalX / normalLength
        val unitNormalY = normalY / normalLength

        val intersections = lineIntersectionsWithRect(width, height, midpoint, foldDirX, foldDirY)
        val foldLineStart = intersections.firstOrNull() ?: PointF(midpoint.x, midpoint.y)
        val foldLineEnd = intersections.getOrNull(1) ?: PointF(midpoint.x, midpoint.y)

        return FoldState(
            visiblePolygon = visiblePolygon,
            backPolygon = backPolygon,
            reflectionMatrix = reflectionMatrix,
            foldLineStart = foldLineStart,
            foldLineEnd = foldLineEnd,
            shadowBounds = RectF(
                min(foldLineStart.x, foldLineEnd.x) - abs(unitNormalX) * shadowDistance,
                min(foldLineStart.y, foldLineEnd.y) - abs(unitNormalY) * shadowDistance,
                max(foldLineStart.x, foldLineEnd.x) + abs(unitNormalX) * shadowDistance,
                max(foldLineStart.y, foldLineEnd.y) + abs(unitNormalY) * shadowDistance
            )
        )
    }

    private fun drawFoldShadow(canvas: Canvas, state: FoldState) {
        shadowRect.set(state.shadowBounds)
        canvas.save()
        foldPath.reset()
        addPolygonToPath(foldPath, state.backPolygon)
        canvas.clipPath(foldPath)
        canvas.drawRect(shadowRect, shadowPaint)
        canvas.restore()

        canvas.save()
        visiblePath.reset()
        addPolygonToPath(visiblePath, state.visiblePolygon)
        canvas.clipPath(visiblePath)
        canvas.drawRect(shadowRect, shadowPaint)
        canvas.restore()
    }

    private fun clipRectByLine(
        width: Float,
        height: Float,
        pointOnLine: PointF,
        normalX: Float,
        normalY: Float,
        keepPositive: Boolean
    ): List<PointF> {
        val input = mutableListOf(
            PointF(0f, 0f),
            PointF(width, 0f),
            PointF(width, height),
            PointF(0f, height)
        )
        val output = mutableListOf<PointF>()
        if (input.isEmpty()) return output

        var previous = input.last()
        var previousInside = isInside(previous, pointOnLine, normalX, normalY, keepPositive)

        input.forEach { current ->
            val currentInside = isInside(current, pointOnLine, normalX, normalY, keepPositive)
            if (currentInside != previousInside) {
                intersectSegmentWithLine(previous, current, pointOnLine, normalX, normalY)?.let(output::add)
            }
            if (currentInside) {
                output.add(PointF(current.x, current.y))
            }
            previous = current
            previousInside = currentInside
        }

        return output
    }

    private fun isInside(
        point: PointF,
        linePoint: PointF,
        normalX: Float,
        normalY: Float,
        keepPositive: Boolean
    ): Boolean {
        val side = sideOfLine(point.x, point.y, linePoint.x, linePoint.y, normalX, normalY)
        return if (keepPositive) side >= 0f else side <= 0f
    }

    private fun sideOfLine(
        x: Float,
        y: Float,
        lineX: Float,
        lineY: Float,
        normalX: Float,
        normalY: Float
    ): Float {
        return (x - lineX) * normalX + (y - lineY) * normalY
    }

    private fun intersectSegmentWithLine(
        start: PointF,
        end: PointF,
        linePoint: PointF,
        normalX: Float,
        normalY: Float
    ): PointF? {
        val dx = end.x - start.x
        val dy = end.y - start.y
        val denominator = dx * normalX + dy * normalY
        if (abs(denominator) < 0.0001f) return null
        val t = ((linePoint.x - start.x) * normalX + (linePoint.y - start.y) * normalY) / denominator
        return PointF(start.x + dx * t, start.y + dy * t)
    }

    private fun reflectPoint(point: PointF, linePoint: PointF, dirX: Float, dirY: Float): PointF {
        val length = max(kotlin.math.sqrt(dirX * dirX + dirY * dirY), 1f)
        val ux = dirX / length
        val uy = dirY / length
        val vx = point.x - linePoint.x
        val vy = point.y - linePoint.y
        val parallel = vx * ux + vy * uy
        val projX = ux * parallel
        val projY = uy * parallel
        val perpX = vx - projX
        val perpY = vy - projY
        return PointF(
            linePoint.x + projX - perpX,
            linePoint.y + projY - perpY
        )
    }

    private fun buildReflectionMatrix(linePoint: PointF, dirX: Float, dirY: Float): Matrix {
        val angle = atan2(dirY, dirX)
        val cos2 = kotlin.math.cos(2 * angle)
        val sin2 = kotlin.math.sin(2 * angle)
        val tx = linePoint.x - cos2 * linePoint.x - sin2 * linePoint.y
        val ty = linePoint.y - sin2 * linePoint.x + cos2 * linePoint.y

        return Matrix().apply {
            setValues(
                floatArrayOf(
                    cos2, sin2, tx,
                    sin2, -cos2, ty,
                    0f, 0f, 1f
                )
            )
        }
    }

    private fun lineIntersectionsWithRect(
        width: Float,
        height: Float,
        point: PointF,
        dirX: Float,
        dirY: Float
    ): List<PointF> {
        val intersections = mutableListOf<PointF>()
        val epsilon = 0.0001f

        if (abs(dirX) > epsilon) {
            val tLeft = (0f - point.x) / dirX
            val leftY = point.y + tLeft * dirY
            if (leftY in 0f..height) intersections.add(PointF(0f, leftY))

            val tRight = (width - point.x) / dirX
            val rightY = point.y + tRight * dirY
            if (rightY in 0f..height) intersections.add(PointF(width, rightY))
        }

        if (abs(dirY) > epsilon) {
            val tTop = (0f - point.y) / dirY
            val topX = point.x + tTop * dirX
            if (topX in 0f..width) intersections.add(PointF(topX, 0f))

            val tBottom = (height - point.y) / dirY
            val bottomX = point.x + tBottom * dirX
            if (bottomX in 0f..width) intersections.add(PointF(bottomX, height))
        }

        return intersections
            .distinctBy { "${it.x.toInt()}_${it.y.toInt()}" }
            .take(2)
    }

    private fun addPolygonToPath(path: Path, polygon: List<PointF>) {
        path.reset()
        if (polygon.isEmpty()) return
        path.moveTo(polygon.first().x, polygon.first().y)
        polygon.drop(1).forEach { path.lineTo(it.x, it.y) }
        path.close()
    }

    private fun computeAutoEndTouch(direction: Int, progress: Float): PointF {
        val width = pageTurnView.width.toFloat()
        val height = pageTurnView.height.toFloat()
        val anchorY = if (touchPoint.y < height / 2f) height * 0.12f else height * 0.88f
        return if (progress >= 1f) {
            if (direction > 0) PointF(width * 0.95f, anchorY) else PointF(width * 0.05f, anchorY)
        } else {
            if (direction > 0) PointF(width * 0.08f, anchorY) else PointF(width * 0.92f, anchorY)
        }
    }

    private fun hideAllPagesForCustomDraw() {
        hideVerticalShadow()
        previousPage?.visibility = View.INVISIBLE
        currentPage?.visibility = View.INVISIBLE
        nextPage?.visibility = View.INVISIBLE
    }

    private fun restorePageVisibility() {
        previousPage?.visibility = View.INVISIBLE
        currentPage?.visibility = View.VISIBLE
        nextPage?.visibility = View.INVISIBLE
    }

    private fun resetSimulationState() {
        currentDirection = 0
        currentProgress = 0f
        isCustomDrawing = false
        resetVerticalRevealState()
        releaseBitmaps()
        pageTurnView.invalidate()
    }

    private fun resetVerticalRevealState() {
        hideVerticalShadow()
        previousPage?.apply {
            visibility = View.INVISIBLE
            alpha = 1f
            translationX = 0f
            clipBounds = Rect(0, 0, 0, pageTurnView.height)
        }
        currentPage?.apply {
            visibility = View.VISIBLE
            alpha = 1f
            translationX = 0f
            clipBounds = null
        }
        nextPage?.apply {
            visibility = View.INVISIBLE
            alpha = 1f
            translationX = 0f
            clipBounds = Rect(pageTurnView.width, 0, pageTurnView.width, pageTurnView.height)
        }
    }

    private fun updateVerticalShadow(revealWidth: Int, pageHeight: Int, progress: Float) {
        if (revealWidth <= 0 || revealWidth >= pageTurnView.width) {
            hideVerticalShadow()
            return
        }

        val density = pageTurnView.resources.displayMetrics.density
        val shadowWidth = (density * 28f).toInt().coerceAtLeast(12)
        val left = revealWidth.coerceIn(0, pageTurnView.width - 1)
        val right = (left + shadowWidth).coerceAtMost(pageTurnView.width)

        verticalShadowDrawable.alpha = (60 + 80 * progress).toInt().coerceIn(0, 150)
        verticalShadowDrawable.setBounds(left, 0, right, pageHeight)
        pageTurnView.foreground = verticalShadowDrawable
    }

    private fun hideVerticalShadow() {
        if (pageTurnView.foreground === verticalShadowDrawable) {
            pageTurnView.foreground = null
        }
    }

    private fun releaseBitmaps() {
        frontBitmap?.recycle()
        underBitmap?.recycle()
        frontBitmap = null
        underBitmap = null
    }

    private fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction
    }

    private data class FoldState(
        val visiblePolygon: List<PointF>,
        val backPolygon: List<PointF>,
        val reflectionMatrix: Matrix,
        val foldLineStart: PointF,
        val foldLineEnd: PointF,
        val shadowBounds: RectF
    )
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
