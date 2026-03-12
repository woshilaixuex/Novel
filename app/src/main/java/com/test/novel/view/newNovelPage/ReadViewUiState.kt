package com.test.novel.view.newNovelPage

import com.test.novel.model.vo.BookVo
import com.test.novel.model.vo.ChapterVo
import com.test.novel.model.vo.ReadingPageVo
import com.test.novel.view.novelPage.PageState

data class ReadViewUiState(
    val bookVo: BookVo? = null,
    val chapterVo: ChapterVo? = null,
    val readingPageVo: ReadingPageVo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)
sealed class ReadIntent{
    data object ShowOrHideBar: ReadIntent()
    data object TurnPage: ReadIntent()
    data class LoadChapterWithId(val chapterId: Int):ReadIntent()
}

