package com.test.novel.view.introductionPage

import com.test.novel.model.vo.BookVo

data class IntroductionUiState(
    val bookBrief: BookBriefVo? = null,        // 书籍详情（兼容旧格式）
    val bookVo: BookVo? = null,                // 书籍详情（新格式）
    val isLoading: Boolean = false,            // 加载状态
    val error: String? = null,                 // 错误信息
    val isFavorite: Boolean = false,            // 收藏状态
    val readingProgress: Float = 0f,           // 阅读进度
    val lastReadChapter: String? = null        // 最后阅读章节
)
sealed class IntroductionIntent {
    /**
     * 加载书籍简介信息:后续改成id请求对应书籍信息接口
     */
    data class LoadBookDetail(val bookBrief: BookBriefVo) : IntroductionIntent()
    data class LoadBookDetailVo(val bookVo: BookVo) : IntroductionIntent()  
    data class ToggleFavorite(val bookId: Int) : IntroductionIntent()
    data class StartReading(val chapterIndex: Int = 0) : IntroductionIntent()
    object RefreshData : IntroductionIntent()
}