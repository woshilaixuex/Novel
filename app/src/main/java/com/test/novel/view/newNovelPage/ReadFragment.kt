package com.test.novel.view.newNovelPage

import android.graphics.Canvas
import android.os.Bundle
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
import com.test.novel.databinding.FragmentPageBinding
import com.test.novel.databinding.FragmentReadBinding
import com.test.novel.model.vo.ReadingPageVo
import com.test.novel.utils.SizeUtils.navigationBarHeight
import com.test.novel.utils.SizeUtils.statusBarHeight
import com.test.novel.view.customView.novel.PageProvider
import com.test.novel.view.customView.novel.PageTurnListener
import com.test.novel.view.customView.novel.PageTurnView
import com.test.novel.view.customView.novel.ReadPageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val CHAPTER_ID = "chapterId"
@AndroidEntryPoint
class ReadFragment : Fragment() {
    private var _binding :FragmentReadBinding? = null
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

            toolBinding.root.measure(
                View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            )
            toolBinding.root.layout(0, 0, width, height)
            toolBinding.root.draw(Canvas())
            val toolNovelText = toolBinding.novelText
            // 3. 作为工具使用
            toolNovelText.isDrawEnable = false
            // 直接使用String类型的context
            toolNovelText.text = pageVo.content
            toolNovelText.draw(Canvas())

            val pages = toolNovelText.getPages()
            val totalPages = pages.size
            Log.d("ReadFragment", "getPages() returned $totalPages pages")
            pages.forEachIndexed { index, pageContent ->
                // 创建PageData用于每一页
                val pageData = ReadPageProvider.PageData(
                    content = pageContent,
                    pageIndex = index,
                    totalPages = totalPages,
                    pageType = ReadPageProvider.PageType.Cover
                )
                Log.d("ReadFragment", "Appending page $index, content length: ${pageContent.length}")
                pageTurnView.appendPage(pageData,false)
            }
            Log.d("ReadFragment", "paginateText completed, total pages added: $totalPages")
        }
    }

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

    companion object {

        @JvmStatic
        fun newInstance(chapterId: Int) = ReadFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHAPTER_ID, chapterId)
                }
            }
    }
}