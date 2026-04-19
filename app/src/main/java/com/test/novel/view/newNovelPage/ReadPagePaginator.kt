package com.test.novel.view.newNovelPage

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.test.novel.view.customView.novel.ReadPageProvider
import kotlin.math.max

object ReadPagePaginator {

    // 与 NewNovelTextView 保持一致，避免分页点落在中文标点前面。
    private val hangingPunctuation = setOf('。', '？', '！', '，', '、', '；', '：', '’', '”', '）', '》')

    fun paginate(
        content: String,
        chapterTitle: String,
        style: PaginationStyle
    ): List<ReadPageProvider.PageData> {
        val pages = paginateContent(content, style)
        val totalPages = pages.size
        return pages.mapIndexed { index, pageContent ->
            ReadPageProvider.PageData(
                content = pageContent,
                pageIndex = index,
                totalPages = totalPages,
                title = chapterTitle,
                pageType = ReadPageProvider.PageType.Cover
            )
        }
    }

    private fun paginateContent(
        content: String,
        style: PaginationStyle
    ): List<String> {
        if (content.isEmpty()) {
            return listOf("")
        }

        val pages = mutableListOf<String>()
        var start = 0
        var pageIndex = 0

        while (start < content.length) {
            // 首页和普通页可用高度不同，所以第一页要用单独的测量规格。
            val spec = if (pageIndex == 0) style.firstPageSpec() else style.normalPageSpec()
            // 计算“从 start 开始，这一页最多能排到哪个字符下标”。
            val end = calculatePageEnd(content, start, spec)
            if (end <= start) {
                val fallbackEnd = (start + 1).coerceAtMost(content.length)
                pages.add(content.substring(start, fallbackEnd))
                start = fallbackEnd
            } else {
                pages.add(content.substring(start, end))
                start = end
            }
            pageIndex++
        }

        return pages
    }

    private fun calculatePageEnd(
        content: String,
        start: Int,
        spec: StaticPageSpec
    ): Int {
        // 这里不是自己一行一行 breakText，而是交给 StaticLayout 按 Android 的文本排版规则
        // 直接算出从 start 到结尾这段文本在给定宽度下会如何换行。
        val layout = StaticLayout.Builder
            .obtain(content, start, content.length, spec.paint, spec.availableWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(spec.lineSpacingExtra, spec.lineSpacingMultiplier)
            .setIncludePad(spec.includeFontPadding)
            .build()

        // 根据页面可用高度，先定位“垂直方向上最后可能可见的那一行”。
        var lastVisibleLine = layout.getLineForVertical(max(spec.availableHeight - 1, 0))
        // getLineForVertical 拿到的是命中的行号，但那一行的底边有可能已经超出页面，
        // 所以要继续往前退，直到 lineBottom 落回页面可用高度内。
        while (lastVisibleLine >= 0 && layout.getLineBottom(lastVisibleLine) > spec.availableHeight) {
            lastVisibleLine--
        }

        if (lastVisibleLine < 0) {
            return start
        }

        // 先拿到 StaticLayout 原始算出的分页点。
        val rawEnd = layout.getLineEnd(lastVisibleLine)
        // 再补一层“标点不落行首”的修正：如果下一字符是中文标点，
        // 就把它带到当前页末尾，和实际 NewNovelTextView 的行内处理保持一致。
        return adjustPageEndForHangingPunctuation(content, rawEnd)
    }

    private fun adjustPageEndForHangingPunctuation(
        content: String,
        rawEnd: Int
    ): Int {
        if (rawEnd >= content.length) return rawEnd
        if (!hangingPunctuation.contains(content[rawEnd])) return rawEnd

        var adjustedEnd = rawEnd + 1
        // 连续标点时，最多再吞一个，和旧版自定义 TextView 的逻辑对齐。
        if (adjustedEnd < content.length && hangingPunctuation.contains(content[adjustedEnd])) {
            adjustedEnd++
        }
        return adjustedEnd.coerceAtMost(content.length)
    }

    data class PaginationStyle(
        val paint: TextPaint,
        val availableWidth: Int,
        val firstPageAvailableHeight: Int,
        val normalPageAvailableHeight: Int,
        val lineSpacingExtra: Float,
        val lineSpacingMultiplier: Float,
        val includeFontPadding: Boolean
    ) {
        fun firstPageSpec(): StaticPageSpec {
            return createSpec(firstPageAvailableHeight)
        }

        fun normalPageSpec(): StaticPageSpec {
            return createSpec(normalPageAvailableHeight)
        }

        private fun createSpec(availableHeight: Int): StaticPageSpec {
            return StaticPageSpec(
                paint = TextPaint(paint),
                availableWidth = availableWidth.coerceAtLeast(1),
                availableHeight = availableHeight.coerceAtLeast(1),
                lineSpacingExtra = lineSpacingExtra,
                lineSpacingMultiplier = lineSpacingMultiplier,
                includeFontPadding = includeFontPadding
            )
        }
    }

    data class StaticPageSpec(
        val paint: TextPaint,
        val availableWidth: Int,
        val availableHeight: Int,
        val lineSpacingExtra: Float,
        val lineSpacingMultiplier: Float,
        val includeFontPadding: Boolean
    )
}
