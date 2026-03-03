package com.test.novel.view.introductionPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IntroductionViewModel : ViewModel() {
    private val _introductionState = MutableStateFlow(IntroductionUiState())
    val introductionState = _introductionState.asStateFlow()
    
    init {
        loadMockData()
    }
    
    fun sendIntent(intent: IntroductionIntent) {
        when(intent) {
            is IntroductionIntent.LoadBookDetail -> {
                loadBookDetail(intent.bookBrief)
            }
            is IntroductionIntent.RefreshData -> {
                refreshData()
            }
            is IntroductionIntent.StartReading -> {
                startReading(intent.chapterIndex)
            }
            is IntroductionIntent.ToggleFavorite -> {
                toggleFavorite()
            }
        }
    }
    
    /**
     * 加载书籍详情数据到Model
     */
    private fun loadBookDetail(bookBrief: BookBriefVo) {
        // 设置加载状态
        _introductionState.value = _introductionState.value.copy(
            isLoading = true,
            error = null
        )
        
        viewModelScope.launch {
            _introductionState.value = _introductionState.value.copy(
                bookBrief = bookBrief,           // 书籍基本信息
                isLoading = false,                // 完成加载
                isFavorite = false,               // 默认未收藏
                readingProgress = 0f,             // 默认阅读进度
                lastReadChapter = null            // 默认无最后阅读章节
            )
        }
    }
    
    /**
     * 刷新数据
     */
    private fun refreshData() {
        val currentBook = _introductionState.value.bookBrief
        if (currentBook != null) {
            loadBookDetail(currentBook)
        }
    }
    
    /**
     * 开始阅读
     */
    private fun startReading(chapterIndex: Int) {
//        val bookBrief = _introductionState.value.bookBrief ?: return
//
//        // 计算阅读进度
//        val progress = if (bookBrief.chapters.isNotEmpty()) {
//            (chapterIndex + 1).toFloat() / bookBrief.chapters.size
//        } else {
//            0f
//        }
//
//        // 获取当前章节标题
//        val currentChapter = if (chapterIndex < bookBrief.chapters.size) {
//            bookBrief.chapters[chapterIndex].title
//        } else {
//            null
//        }
//
//        // 更新阅读状态
//        _introductionState.value = _introductionState.value.copy(
//            readingProgress = progress,
//            lastReadChapter = currentChapter
//        )
    }
    
    /**
     * 切换收藏状态
     */
    private fun toggleFavorite() {
//        val currentFavorite = _introductionState.value.isFavorite
//        _introductionState.value = _introductionState.value.copy(
//            isFavorite = !currentFavorite
//        )
    }
    
    /**
     * 加载模拟数据
     */
    private fun loadMockData() {
        val mockBook = BookBriefVo(
            title = "修仙传奇",
            bookId = 1,
            coverUrl = "https://example.com/cover.jpg",
            author = "网文大神",
            type = listOf("玄幻", "修仙", "热血"),
            status = "连载中",
            brief = "这是一个关于平凡少年踏上修仙之路的传奇故事。主角林凡原本是一个普通的山村少年，意外获得神秘传承，从此开启了他的修仙之路。在这个充满机遇与危险的世界里，他将面临各种挑战，结识志同道合的伙伴，最终成为一代仙尊。",
            isLocal = false,
            chapters = listOf(
                ChapterVo(1,1,"第一章 意外传承",0),
                ChapterVo(1,2,"第二章 初入仙门",0),
                ChapterVo(1,3,"第三章 修炼功法",0),
                ChapterVo(1,4,"第四章 门派试炼",0),
                ChapterVo(1,5,"第五章 奇遇",0),
                ChapterVo(1,6,"第六章 实力大增",0),
                ChapterVo(1,7,"第七章 秘境探险",0),
                ChapterVo(1,8,"第八章 强敌出现",0),
                ChapterVo(1,9,"第九章 生死一战",0),
                ChapterVo(1,10,"第十章 突破境界",0)
            )
        )
        
        // 直接加载模拟数据
        loadBookDetail(mockBook)
    }
}