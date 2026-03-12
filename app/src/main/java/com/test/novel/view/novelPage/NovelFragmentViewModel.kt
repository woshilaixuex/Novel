package com.test.novel.view.novelPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.novel.database.chapter.ChapterDao
import com.test.novel.database.readHistory.ReadHistory
import com.test.novel.database.readHistory.ReadHistoryDao
import com.test.novel.model.BookBrief
// import com.test.novel.utils.WebCrawler // 移除WebCrawler引用
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NovelFragmentViewModel @Inject constructor(
    private val chapterDao: ChapterDao,
    private val readHistoryDao: ReadHistoryDao
) : ViewModel() {

    private val _state = MutableStateFlow(BookState())
    val state = _state.asStateFlow()

    private val _intent = MutableSharedFlow<BookIntent>()
    private val intent = _intent.asSharedFlow()

    private var hideBarJob: Job? = null

    init {
        viewModelScope.launch {
            intent.collect {
                processIntent(it)
            }
        }
    }

    fun sendIntent(intent: BookIntent) {
        viewModelScope.launch {
            _intent.emit(intent)
        }
    }

    private fun processIntent(intent: BookIntent) {
        viewModelScope.launch {
            when (intent) {
                BookIntent.ShowBar -> showOrHideBar()

                is BookIntent.Init -> {
                    init(intent.bookBrief)
                }

                is BookIntent.SetContent -> {
                    val pagesCount = _state.value.pageCount.toMutableList()
                    intent.pages.forEach { pageState ->
                        ensurePageCountSize(pagesCount, pageState.chapterIndex)
                        pagesCount[pageState.chapterIndex - 1] = 1
                    }
                    _state.value = _state.value.copy(pages = intent.pages, pageCount = pagesCount)
                }

                is BookIntent.AddPages -> {
                    if (intent.pageState.isEmpty()) return@launch

                    val chapterIndex = intent.pageState[0].chapterIndex
                    val pagesCount = _state.value.pageCount.toMutableList()
                    ensurePageCountSize(pagesCount, chapterIndex)

                    // Update the page count for this chapter
                    pagesCount[chapterIndex - 1] = intent.pageState.size

                    // Find the offset for this chapter's first page
                    var offset = 0
                    for (i in 0 until chapterIndex - 1) {
                        offset += pagesCount.getOrElse(i) { 0 }
                    }

                    // Create new pages list with updated/added pages
                    val newList = _state.value.pages.toMutableList()

                    // Ensure the list has enough capacity
                    while (newList.size < offset + intent.pageState.size) {
                        newList.add(PageState(chapterIndex = 0, title = "", text = "", load = false))
                    }

                    // Replace or add the new pages
                    for (i in intent.pageState.indices) {
                        if (offset + i < newList.size) {
                            newList[offset + i] = intent.pageState[i]
                        } else {
                            newList.add(intent.pageState[i])
                        }
                    }

                    _state.value = _state.value.copy(pages = newList, pageCount = pagesCount)
                }

                is BookIntent.SetCurrentIndex -> {
                    _state.value = _state.value.copy(currentIndex = intent.index)
                }

                is BookIntent.GetContentFromLocal -> {
                    getLocalContent(intent.bookId)
                }
                
                is BookIntent.LoadMockContent -> {
                    loadMockContent(intent.bookBrief)
                }
            }
        }
    }

    private fun ensurePageCountSize(pageCount: MutableList<Int>, chapterIndex: Int) {
        while (pageCount.size < chapterIndex) {
            pageCount.add(0)
        }
    }

    private fun init(bookBrief: BookBrief) {
        _state.value = _state.value.copy(
            bookId = bookBrief.bookId,
            title = bookBrief.title,
            author = bookBrief.author,
            brief = bookBrief.brief,
            coverUrl = bookBrief.coverUrl,
            type = bookBrief.type
        )

        sendIntent(BookIntent.GetContentFromLocal(bookBrief.bookId))
    }

    private fun showOrHideBar() {
        if (_state.value.currentIndex == 0)
            return
        if (_state.value.showBar) {
            _state.value = _state.value.copy(showBar = false)
            hideBarJob?.cancel() // 取消已经存在的协程
        } else {
            _state.value = _state.value.copy(showBar = true)
            hideBarJob?.cancel() // 取消已经存在的协程
            hideBarJob = viewModelScope.launch {
                delay(3000)
                _state.value = _state.value.copy(showBar = false)
            }
        }
    }

    private suspend fun getLocalContent(bookId: Int) {
        withContext(Dispatchers.IO) {
            val chapters = chapterDao.getChapters(bookId)
            chapters.collect { chapterList ->
                val pages = mutableListOf<PageState>()
                chapterList.forEachIndexed { index, chapter ->
                    pages.add(
                        PageState(
                            title = chapter.title,
                            showTitle = true,
                            chapterIndex = index + 1,
                            text = chapter.content,
                            load = false
                        )
                    )
                }
                sendIntent(BookIntent.SetContent(pages))
            }
        }
    }
    
    private fun loadMockContent(bookBrief: BookBrief) {
        viewModelScope.launch {
            // 创建模拟章节内容
            val mockChapters = createMockChapters(bookBrief)
            val pages = mutableListOf<PageState>()
            
            mockChapters.forEachIndexed { index, chapter ->
                pages.add(
                    PageState(
                        title = chapter.title,
                        showTitle = true,
                        chapterIndex = index + 1,
                        text = chapter.content,
                        load = false
                    )
                )
            }
            
            sendIntent(BookIntent.SetContent(pages))
        }
    }
    
    private fun createMockChapters(bookBrief: BookBrief): List<MockChapter> {
        return listOf(
            MockChapter(
                title = "第一章 开始",
                content = """
                    在这个浩瀚的宇宙中，存在着无数个世界，每个世界都有着独特的规则和力量体系。
                    
                    我们的主角，叶辰，是一个来自地球的普通青年，但他的命运却因为一次意外而彻底改变。
                    
                    "这是什么地方？"叶辰看着眼前陌生的环境，心中充满了疑惑和不安。
                    
                    周围是茂密的森林，空气中弥漫着淡淡的雾气，远处的山峦若隐若现。
                    
                    突然，一道光芒闪过，一个神秘的身影出现在他面前...
                """.trimIndent()
            ),
            MockChapter(
                title = "第二章 修炼",
                content = """
                    "欢迎来到修炼世界。"神秘人缓缓开口，声音中带着一种难以言喻的威严。
                    
                    叶辰警惕地看着对方："你到底是谁？这里是哪里？"
                    
                    "这里是玄天大陆，一个修炼者的世界。"神秘人回答道，"至于我，你可以称我为引路人。"
                    
                    "修炼？"叶辰眼中闪过一丝明悟，"就像小说中写的那样？"
                    
                    "没错。"引路人点头，"在这里，力量就是一切。弱者只能任人宰割，而强者则可以俯瞰众生。"
                    
                    说着，引路人伸出手，一团柔和的光芒出现在他的掌心。
                    
                    "这是给你的礼物，"他说道，"有了它，你就能开始自己的修炼之路。"
                """.trimIndent()
            ),
            MockChapter(
                title = "第三章 初试",
                content = """
                    叶辰接过那团光芒，只觉得一股暖流涌遍全身。
                    
                    他能感觉到，自己的身体正在发生着奇妙的变化。
                    
                    "这就是修炼的力量吗？"叶辰握紧拳头，感受着体内涌动的力量。
                    
                    "这只是开始。"引路人说道，"修炼之路漫长而艰险，需要你不断努力。"
                    
                    叶辰重重地点头："我明白，无论多么困难，我都会坚持下去。"
                    
                    引路人满意地点头："很好，那么我先教你基础的修炼法门。"
                    
                    接下来的时间里，叶辰认真学习着修炼的基础知识。
                    
                    他知道，这只是开始，前方的路还很长...
                """.trimIndent()
            )
        )
    }
    
    data class MockChapter(
        val title: String,
        val content: String
    )
}


