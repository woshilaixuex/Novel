package com.test.novel.view.customView.novel

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.test.novel.R
import com.test.novel.view.customView.novel.ReadPageProvider.PageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class PageTurnView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val DEBOUNCE_DURATION = 250L
    private var pageChangeJob: Job? = null
    private val viewScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        private const val MOVE_THRESHOLD = 0.3f // 翻页阈值，超过屏幕宽度的30%自动完成翻页
    }

    // 翻页方式
    enum class PageAnimationType {
        COVER,       // 覆盖翻页
        SIMULATION,  // 仿真翻页
        TRANSLATION  // 平移翻页
    }


    // 当前的翻页动画类型
    var animationType = PageAnimationType.SIMULATION
        set(value) {
            if (field != value) {
                field = value
                if (::pageAnimator.isInitialized) {
                    pageAnimator.clearAnimation()
                }
                pageAnimator = createAnimator(value)
                requestLayout()
            }
        }

    // 页面监听器
    var pageListener: PageTurnListener? = null

    // 页面数据提供者
    var pageProvider: PageProvider? = null
        set(value) {
            field = value
            resetPages()
        }

    // 当前页码，从0开始
    var currentPageIndex: Int = 0
        private set

    // 页面工厂
    private val pageFactory = PageFactory(context)

    // 页面动画器
    private lateinit var pageAnimator: PageAnimator

    // 三个页面视图
    private var previousPage: View? = null
    private var currentPage: View? = null
    private var nextPage: View? = null

    // 触摸相关变量
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var lastX: Float = 0f
    private var lastTouchX: Float = 0f
    private var lastTouchY: Float = 0f
    private var touchDown = false
    private var isFlipping = false
    private var isClick = 0
    private var moveDirection = 0 // -1左滑，1右滑
    private var moveDistance = 0f
    private var animationInProgress = false

    // 触摸判定阈值
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()


    init {
        // 先准备一个默认动画器，避免 XML 属性在构造期回调 setter 时访问空引用。
        pageAnimator = createAnimator(animationType)

        // 初始化
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PageTurnView,
            0, 0
        ).apply {
            try {
                val type = getInteger(R.styleable.PageTurnView_animationType, 1)
                animationType = PageAnimationType.entries.toTypedArray()[type]
            } finally {
                recycle()
            }
        }

        // 设置默认背景，可通过主题修改
        setBackgroundColor(0xFFFAF9F6.toInt()) // 淡米色，模拟纸张


    }

    private fun createAnimator(type: PageAnimationType): PageAnimator {
        return when (type) {
            PageAnimationType.COVER -> CoverPageAnimator(this)
            PageAnimationType.SIMULATION -> SimulationPageAnimator(this)
            PageAnimationType.TRANSLATION -> TranslationPageAnimator(this)
        }
    }

    /**
     * 重置页面
     */
    fun resetPages() {
        // 取消正在进行的动画
        pageAnimator.clearAnimation()
        animationInProgress = false

        // 移除所有视图
        removeAllViews()

        // 重置页码
        currentPageIndex = 0

        // 重新加载页面
        loadPages()
    }

    /**
     * 加载页面
     */
    private fun loadPages() {
        val provider = pageProvider ?: return
        val pageCount = provider.getPageCount()
        Log.d("PageTurnView", "loadPages() called, pageCount: $pageCount, currentPageIndex: $currentPageIndex")
        if (pageCount == 0)
            return
        // 防止越界
        if (currentPageIndex < 0) currentPageIndex = 0
        if (currentPageIndex >= provider.getPageCount()) {
            currentPageIndex = provider.getPageCount() - 1
            if (currentPageIndex < 0) currentPageIndex = 0
        }

        // 清除现有页面
        removeAllViews()

        // 加载当前页
        currentPage = pageFactory.createPageView().apply {
            Log.d("PageTurnView", "Binding page at index: $currentPageIndex")
            provider.bindPage(this, currentPageIndex)
        }
        addView(currentPage)

        // 如果有上一页，加载上一页
        if (currentPageIndex > 0) {
            previousPage = pageFactory.createPageView().apply {
                provider.bindPage(this, currentPageIndex - 1)
                visibility = View.INVISIBLE
            }
            addView(previousPage)
        } else {
            previousPage = null
        }

        // 如果有下一页，加载下一页
        if (currentPageIndex < provider.getPageCount() - 1) {
            nextPage = pageFactory.createPageView().apply {
                provider.bindPage(this, currentPageIndex + 1)
                visibility = View.INVISIBLE
            }
            addView(nextPage)
        } else {
            nextPage = null
        }

        // 初始化动画器
        pageAnimator.onPageLoaded()
    }



    /**
     * 转到指定页面
     */
    fun goToPage(index: Int) {
        val provider = pageProvider ?: return

        if (index < 0 || index >= provider.getPageCount() || index == currentPageIndex) {
            return
        }

        currentPageIndex = index
        loadPages()
        pageListener?.onPageChanged(currentPageIndex)
    }

    /**
     * 转到下一页
     */
    private fun goToNextPage() {
        val provider = pageProvider ?: return

        if (currentPageIndex < provider.getPageCount() - 1) {
            pageAnimator.flipNextPage {
                currentPageIndex++
                pageListener?.onPageChanged(currentPageIndex)
                loadPages()
            }
        }
    }

    /**
     * 转到上一页
     */
    private fun goToPreviousPage() {
        if (currentPageIndex > 0) {
            pageAnimator.flipPreviousPage {
                currentPageIndex--
                pageListener?.onPageChanged(currentPageIndex)
                loadPages()
            }
        }
    }

    /**
     * 通知页面数据已更新
     * 当PageProvider的数据发生变化时调用此方法
     */
    private fun notifyDataChanged() {
        // 重新加载页面
        loadPages()
        // 通知监听器页面已更新
        pageListener?.onPageChanged(currentPageIndex)
    }

    /**
     * 动态添加页面
     *
     * @param pageContent 页面内容
     * @param navigateToNewPage 是否立即导航到新添加的页面
     * @return 新页面的索引，如果添加失败则返回-1
     */
    fun addPage(pageContent: PageData, navigateToNewPage: Boolean = true): Int {
        val provider = pageProvider
        Log.d("PageTurnView", "addPage called, provider: $provider, pageContent: $pageContent")

        // 检查Provider是否支持动态添加页面
        if (provider !is PageProvider) {
            Log.w("PageTurnView", "Current provider doesn't support dynamic page addition")
            return -1
        }

        // 调用提供者添加页面
        val newPageIndex = provider.addPage(pageContent)
        Log.d("PageTurnView", "addPage result: newPageIndex=$newPageIndex, navigateToNewPage=$navigateToNewPage")

        if (newPageIndex >= 0) {
            if (navigateToNewPage) {
                // 导航到新页面
                goToPage(newPageIndex)
            } else {
                // 仅更新视图状态
                notifyDataChanged()
            }
        }

        return newPageIndex
    }

    /**
     * 动态添加页面到最后
     * 简便方法，将页面添加到末尾
     *
     * @param pageContent 页面内容
     * @param navigateToNewPage 是否立即导航到新添加的页面
     * @return 是否添加成功
     */
    fun appendPage(pageContent: PageData, navigateToNewPage: Boolean = true): Boolean {
        return addPage(pageContent, navigateToNewPage) >= 0
    }
    fun appendPages(pageContents:List<PageData>,navigateIndex: Int): Boolean {
        return addPages(pageContents, navigateIndex) >= 0
    }
    /**
     * 用于初始化对应数据，防止重复调用loadPage导致频繁绘制
     * @param pageContents 所以页面内容
     * @param navigateIndex 是否立即导航到的页面的索引 -1为不导航
     * @return 此时page的长度
     */
    fun addPages(pageContents:List<PageData>,navigateIndex: Int) :Int{
        val provider = pageProvider
        if (provider !is PageProvider) {
            Log.w("PageTurnView", "Current provider doesn't support dynamic page addition")
            return -1
        }
        val newPageIndex = 0
        for (content in pageContents) {
            val newPageIndex = provider.addPage(content)
        }
        if (navigateIndex >= 0) {
            goToPage(newPageIndex)
        } else {
            // 仅更新视图状态
            notifyDataChanged()
        }
        return newPageIndex
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (pageProvider == null || animationInProgress) return super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 重置状态
                isFlipping = false
                moveDirection = 0
                moveDistance = 0f
                startX = event.x
                startY = event.y
                lastX = startX
                lastTouchX = event.x
                lastTouchY = event.y
                touchDown = true
                isClick = 0
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!touchDown) return false

                isClick ++

                val deltaX = event.x - lastX
                val totalDeltaX = event.x - startX
                val totalDeltaY = event.y - startY

                // 如果刚开始滑动，确定方向
                if (!isFlipping && abs(totalDeltaX) > touchSlop &&
                    abs(totalDeltaY) < abs(totalDeltaX)) {
                    isFlipping = true
                    moveDirection = if (totalDeltaX > 0) 1 else -1

                    // 检查是否可以朝该方向翻页
                    if ((moveDirection == 1 && previousPage == null) ||
                        (moveDirection == -1 && nextPage == null)) {
                        isFlipping = false
                        return false
                    }

                    // 准备开始翻页动画
                    pageAnimator.onFlipStart(moveDirection)
                }

                // 如果正在翻页，更新翻页进度
                if (isFlipping) {
                    moveDistance = totalDeltaX
                    lastTouchX = event.x
                    lastTouchY = event.y
                    val progress = abs(moveDistance) / width
                    pageAnimator.onFlipProgress(moveDirection, progress, moveDistance)
                    invalidate()
                }

                lastX = event.x
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isFlipping && isClick < 3) {
                    // 点击事件处理
                    val screenWidth = width.toFloat()
                    when {
                        event.x < screenWidth / 3 -> {
                            debouncePageChange { goToPreviousPage() }
                        }
                        event.x > screenWidth * 2 / 3 -> {
                            debouncePageChange { goToNextPage() }
                        }
                        else -> pageListener?.onCenterClick() // 中间区域点击
                    }
                    touchDown = false
                    return true
                }

                // 判断是否完成翻页
                val progress = abs(moveDistance) / width
                animationInProgress = true // 标记动画进行中

                if (progress > MOVE_THRESHOLD) {
                    // 翻页成功
                    pageAnimator.onFlipEnd(moveDirection, true) {
                        // 翻页完成后更新页码
                        Log.d("TAG", "onTouchEvent: $moveDirection")
                        if (moveDirection > 0) {
                            // 向右翻，显示上一页
                            if (currentPageIndex > 0) {
                                currentPageIndex--
                                pageListener?.onPageChanged(currentPageIndex)
                            }
                        } else {
                            // 向左翻，显示下一页
                            if (currentPageIndex < (pageProvider?.getPageCount() ?: 0) - 1) {
                                currentPageIndex++
                                pageListener?.onPageChanged(currentPageIndex)
                            }
                        }
                        loadPages()
                        animationInProgress = false
                    }
                } else {
                    // 取消翻页
                    pageAnimator.onFlipEnd(moveDirection, false) {
                        // 恢复原状
                        animationInProgress = false
                    }
                }

                return true
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        pageAnimator.onLayout()
    }

    override fun dispatchDraw(canvas: Canvas) {
        pageAnimator.onDraw(canvas)
        super.dispatchDraw(canvas)
    }
    private fun debouncePageChange(action: () -> Unit) {
        pageChangeJob?.cancel()
        pageChangeJob = viewScope.launch {
            delay(DEBOUNCE_DURATION)
            action()
        }
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewScope.cancel()
    }
    // 获取页面视图
    fun getPreviousPage(): View? = previousPage
    fun getCurrentPage(): View? = currentPage
    fun getNextPage(): View? = nextPage
    fun getLastTouchX(): Float = lastTouchX
    fun getLastTouchY(): Float = lastTouchY
}
