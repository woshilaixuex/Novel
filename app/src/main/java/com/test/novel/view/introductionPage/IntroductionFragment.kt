package com.test.novel.view.introductionPage

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.test.novel.R
import com.test.novel.databinding.FragmentIntroductionBinding
import com.test.novel.model.vo.BookVo
import com.test.novel.model.vo.ChapterVo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IntroductionFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(bookVo: BookVo) = IntroductionFragment().apply {
            arguments = Bundle().apply {
                putParcelable("bookVo", bookVo)
            }
        }
    }

    private val viewModel: IntroductionViewModel by viewModels()
    private var _binding: FragmentIntroductionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 优先尝试接收 Parcelable 对象
        arguments?.getParcelable<BookVo>("bookVo")?.let { bookVo ->
            viewModel.sendIntent(IntroductionIntent.LoadBookDetailVo(bookVo))
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
            viewModel.sendIntent(IntroductionIntent.StartingRead)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.introductionState.collect { state ->
                        renderUI(state)
                    }
                }
                launch {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is IntroductionEffect.NavigateToRead -> {
                                val action =
                                    IntroductionFragmentDirections
                                        .actionIntroductionFragmentToReadFragment(effect.chapterIndex)
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * MVI核心：根据State渲染UI
     */
    private fun renderUI(state: IntroductionUiState) {

        // 渲染书籍基本信息与基本视图
        renderBookInfo(state.bookVo)
        
        // 渲染收藏状态
        renderFavoriteState(state.isFavorite)
        
        // 渲染阅读进度
        renderReadingProgress(state.readingProgress, state.lastReadChapter)
        
        // 渲染加载状态
        renderLoadingState(state.isLoading)
        
        // 渲染错误状态
        renderErrorState(state.error)
    }

    private fun renderBookInfo(bookVo: BookVo?) {
        bookVo?.let { book ->
            // 书名
            binding.bookName.text = book.title

            // 作者
            binding.author.text = book.author

            // 类型标签
            binding.tagTv.text = book.categories.joinToString("    ")
            // 简介
            binding.briefTv.text = book.description

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