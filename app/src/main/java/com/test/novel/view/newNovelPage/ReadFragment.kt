package com.test.novel.view.newNovelPage

import android.os.Bundle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.test.novel.databinding.FragmentCoverReadBinding
import com.test.novel.databinding.FragmentReadBinding
import com.test.novel.model.vo.ReadingPageVo
import com.test.novel.utils.SizeUtils.navigationBarHeight
import com.test.novel.utils.SizeUtils.statusBarHeight
import com.test.novel.view.customView.novel.PageTurnListener
import com.test.novel.view.customView.novel.PageTurnView
import com.test.novel.view.customView.novel.ReadPageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.max

@AndroidEntryPoint
class ReadFragment : Fragment() {
    private var _binding : FragmentReadBinding? = null
    private val binding get() = _binding!!
    private lateinit var pageProvider: ReadPageProvider
    private lateinit var pageTurnView: PageTurnView
    private val viewModel: ReadViewModel by viewModels()
    private lateinit var toolBinding: FragmentCoverReadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolBinding = FragmentCoverReadBinding.inflate(
            LayoutInflater.from(requireContext().applicationContext),
            null,
            false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBinding = FragmentCoverReadBinding.inflate(layoutInflater)
        // 根据章节加载数据
        var chapterId = 0
        arguments?.let {
            chapterId = it.getInt(CHAPTER_ID)
        }
        viewModel.loadChapter(chapterId)
        // 全屏阅读
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, statusBarHeight, systemBars.right, 0)
            insets
        }
        // 绑定的View由业务模型决定，业务模型可以根据本地用户信息获取，默认是Cover
        pageTurnView = binding.page
        pageProvider = ReadPageProvider()
        pageTurnView.pageProvider = pageProvider
        pageTurnView.pageListener = object : PageTurnListener{
            override fun onPageChanged(pageIndex: Int) {
            }

            override fun onCenterClick() {
                viewModel.sendIntent(ReadIntent.ShowOrHideBar)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 并行收集多个Flow
                launch {
                    viewModel.showBar.collect {
                        animateBars(it)
                    }
                }
                launch {
                    viewModel.readState
                        .map { it.readingPageVo }
                        .filter { it != null }
                        .collect { pageVo ->
                            Log.d("ReadFragment", "readState collected, pageVo: $pageVo")
                            paginateText(pageVo!!)
                        }
                }
            }
        }
    }

    private fun paginateText(pageVo: ReadingPageVo) {
        Log.d("ReadFragment", "paginateText called, content length: ${pageVo.content.length}")
        pageTurnView.post {
            val width = pageTurnView.measuredWidth
            val height = pageTurnView.measuredHeight
            Log.d("ReadFragment", "pageTurnView dimensions: width=$width, height=$height")

            if (width <= 0 || height <= 0) {
                Log.w("ReadFragment", "paginateText skipped because pageTurnView is not measured yet")
                return@post
            }

            val pages = paginateWithStaticLayout(pageVo, width, height)
            val totalPages = pages.size
            Log.d("ReadFragment", "StaticLayout returned $totalPages pages")
            val pageDataList = mutableListOf<ReadPageProvider.PageData>()
            pages.forEachIndexed { index, pageContent ->
                // 创建PageData用于每一页
                val pageData = ReadPageProvider.PageData(
                    content = pageContent,
                    pageIndex = index,
                    totalPages = totalPages,
                    title = pageVo.chapterTitle,
                    pageType = ReadPageProvider.PageType.Cover
                )
                Log.d("ReadFragment", "Adding page $index, content length: ${pageContent.length}")
                pageDataList.add(pageData)
            }

            pageProvider.replacePages(pageDataList)
            pageTurnView.resetPages()
            Log.d("ReadFragment", "paginateText completed, total pages added: $totalPages")
        }
    }

    private fun paginateWithStaticLayout(
        pageVo: ReadingPageVo,
        pageWidth: Int,
        pageHeight: Int
    ): List<String> {
        val content = pageVo.content
        if (content.isEmpty()) {
            return listOf("")
        }

        val firstPageSpec = createPageSpec(pageWidth, pageHeight, showTitle = true)
        val normalPageSpec = createPageSpec(pageWidth, pageHeight, showTitle = false)
        val pages = mutableListOf<String>()
        var start = 0
        var pageIndex = 0

        while (start < content.length) {
            val spec = if (pageIndex == 0) firstPageSpec else normalPageSpec
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

    private fun createPageSpec(
        pageWidth: Int,
        pageHeight: Int,
        showTitle: Boolean
    ): StaticPageSpec {
        toolBinding.title.visibility = if (showTitle) View.VISIBLE else View.GONE
        toolBinding.root.measure(
            View.MeasureSpec.makeMeasureSpec(pageWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(pageHeight, View.MeasureSpec.EXACTLY)
        )
        toolBinding.root.layout(0, 0, pageWidth, pageHeight)

        val textView = toolBinding.novelText
        val availableWidth =
            (textView.measuredWidth - textView.paddingLeft - textView.paddingRight).coerceAtLeast(1)
        val availableHeight =
            (textView.measuredHeight - textView.paddingTop - textView.paddingBottom).coerceAtLeast(1)

        return StaticPageSpec(
            paint = TextPaint(textView.paint),
            availableWidth = availableWidth,
            availableHeight = availableHeight,
            lineSpacingExtra = textView.lineSpacingExtra,
            lineSpacingMultiplier = textView.lineSpacingMultiplier,
            includeFontPadding = textView.includeFontPadding
        )
    }

    private fun calculatePageEnd(
        content: String,
        start: Int,
        spec: StaticPageSpec
    ): Int {
        val layout = StaticLayout.Builder
            .obtain(content, start, content.length, spec.paint, spec.availableWidth)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(spec.lineSpacingExtra, spec.lineSpacingMultiplier)
            .setIncludePad(spec.includeFontPadding)
            .build()

        var lastVisibleLine = layout.getLineForVertical(max(spec.availableHeight - 1, 0))
        while (lastVisibleLine >= 0 && layout.getLineBottom(lastVisibleLine) > spec.availableHeight) {
            lastVisibleLine--
        }

        if (lastVisibleLine < 0) {
            return start
        }

        return layout.getLineEnd(lastVisibleLine)
    }

    private data class StaticPageSpec(
        val paint: TextPaint,
        val availableWidth: Int,
        val availableHeight: Int,
        val lineSpacingExtra: Float,
        val lineSpacingMultiplier: Float,
        val includeFontPadding: Boolean
    )

    private fun animateBars(show: Boolean) {
        val topBar = binding.topBar
        val bottomBar = binding.bottomBar

        if (show) {
            topBar.visibility = View.VISIBLE
            topBar.animate()
                .translationY(0F)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            bottomBar.visibility = View.VISIBLE
            bottomBar.animate()
                .translationY(0F)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            activity?.let {
                WindowInsetsControllerCompat(it.window, binding.root).apply {
                    show(WindowInsetsCompat.Type.systemBars())
                }
            }
        } else {
            topBar.animate()
                .translationY((-topBar.height).toFloat())
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { topBar.visibility = View.GONE }
                .start()

            bottomBar.animate()
                .translationY(bottomBar.height.toFloat() + navigationBarHeight)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction { bottomBar.visibility = View.GONE }
                .start()

            activity?.let {
                WindowInsetsControllerCompat(it.window, binding.root).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        const val CHAPTER_ID = "chapterId"
        @JvmStatic
        fun newInstance(chapterId: Int) = ReadFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHAPTER_ID, chapterId)
                }
            }
    }
}
