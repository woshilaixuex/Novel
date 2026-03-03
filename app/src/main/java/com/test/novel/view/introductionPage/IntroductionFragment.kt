package com.test.novel.view.introductionPage

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.test.novel.R
import com.test.novel.databinding.FragmentIntroductionBinding
import com.test.novel.model.BookBrief
import com.test.novel.model.vo.BookVo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class IntroductionFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(bookBriefJson: String) = IntroductionFragment().apply {
            arguments = Bundle().apply {
                putString("bookBrief", bookBriefJson)
            }
        }
    }

    private val viewModel: IntroductionViewModel by viewModels()
    private var _binding: FragmentIntroductionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 优先尝试接收 Parcelable 对象
arguments?.getParcelable<BookVo>("bookBrief")?.let { bookVo ->
            viewModel.sendIntent(IntroductionIntent.LoadBookDetailVo(bookVo))
        } ?: run {
            // 兼容旧的 JSON 字符串格式
            arguments?.getString("bookBrief")?.let { bookBriefJson ->
                try {
                    val bookBrief = Json.decodeFromString<BookBriefVo>(bookBriefJson)
                    viewModel.sendIntent(IntroductionIntent.LoadBookDetail(bookBrief))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "数据解析失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // 弹出返回store页面
        binding.topicBack.setOnClickListener {
            findNavController().popBackStack()
        }
        // 开始阅读，默认调至第一章，后续更新历史记录功能
        binding.startReading.setOnClickListener {
            // 创建死数据BookBrief用于测试（兼容你的数据结构）
            val testBookBrief = BookBrief(
                title = "测试小说",
                bookId = 1,
                coverUrl = "https://example.com/cover.jpg",
                author = "测试作者",
                type = listOf("玄幻", "修仙"),
                status = "连载中",
                brief = "这是一本测试小说，用于演示阅读效果。主角叶辰的修炼之路从这里开始...",
                isLocal = false
            )
            
            // 序列化并跳转到NovelFragment
            val bookBriefJson = Json.encodeToString(BookBrief.serializer(), testBookBrief)
            val action = IntroductionFragmentDirections.actionIntroductionFragmentToNovelFragment(bookBriefJson)
            findNavController().navigate(action)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.introductionState.collect { state ->
                renderUI(state)
            }
        }
    }

    /**
     * MVI核心：根据State渲染UI
     */
    private fun renderUI(state: IntroductionUiState) {
        // 渲染书籍基本信息
        renderBookInfo(state.bookBrief)
        
        // 渲染收藏状态
        renderFavoriteState(state.isFavorite)
        
        // 渲染阅读进度
        renderReadingProgress(state.readingProgress, state.lastReadChapter)
        
        // 渲染加载状态
        renderLoadingState(state.isLoading)
        
        // 渲染错误状态
        renderErrorState(state.error)
    }

    private fun renderBookInfo(bookBrief: BookBriefVo?) {
        bookBrief?.let { book ->
            // 书名
            binding.bookName.text = book.title
            
            // 作者
            binding.author.text = book.author
            
            // 类型标签
            binding.tagTv.text = book.type.joinToString("    ")
            // 简介
            binding.briefTv.text = book.brief
            
            // 封面图片
            com.bumptech.glide.Glide.with(this)
                .load(book.coverUrl)
                .placeholder(R.drawable.cover1)
                .into(binding.bookCover)
            // 章节信息
            renderChapterList(book.chapters)
        } ?: run {
            // 清空UI
            binding.bookName.text = ""
            binding.author.text = ""
            binding.tagTv.text = ""
            binding.briefTv.text = ""
        }
    }

    private fun renderChapterList(chapters: List<ChapterVo>) {
//        // 章节总数
//        binding.chapterCount.text = "共${chapters.size}章"
//
//        // 最近章节列表
//        val recentChapters = chapters.takeLast(5).map { it.title }
//        binding.recentChapters.text = recentChapters.joinToString("\n")
    }

    private fun renderFavoriteState(isFavorite: Boolean) {
//        binding.favoriteButton.isSelected = isFavorite
//        // 可以根据状态更新按钮样式
//        binding.favoriteButton.text = if (isFavorite) "已收藏" else "收藏"
    }

    private fun renderReadingProgress(progress: Float, lastChapter: String?) {
//        // 进度条
//        binding.readingProgress.progress = (progress * 100).toInt()
//
//        // 进度文本
//        binding.progressText.text = "${(progress * 100).toInt()}%"
//
//        // 最后阅读章节
//        lastChapter?.let { chapter ->
//            binding.lastReadChapter.text = "上次阅读: $chapter"
//        } ?: run {
//            binding.lastReadChapter.text = "暂无阅读记录"
//        }
    }

    private fun renderLoadingState(isLoading: Boolean) {
//        if (isLoading) {
//            binding.progressBar.visibility = View.VISIBLE
//            binding.contentGroup.visibility = View.GONE
//            binding.loadingText.text = "正在加载..."
//        } else {
//            binding.progressBar.visibility = View.GONE
//            binding.contentGroup.visibility = View.VISIBLE
//        }
    }

    private fun renderErrorState(error: String?) {
//        error?.let { errorMsg ->
//            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
//            // 可以显示错误视图
//            binding.errorText.text = errorMsg
//            binding.errorText.visibility = View.VISIBLE
//        } ?: run {
//            binding.errorText.visibility = View.GONE
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}