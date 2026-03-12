package com.test.novel.view.introductionPage

import com.test.novel.model.vo.BookVo

data class IntroductionUiState(
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
    data class LoadBookDetailVo(val bookVo: BookVo) : IntroductionIntent()  
    data class ToggleFavorite(val bookId: Int) : IntroductionIntent()
    object StartingRead: IntroductionIntent()
    data class StartingReadWithChapterId(val chapterId: Int): IntroductionIntent()
    object RefreshData : IntroductionIntent()
}

/**
 *  MVI中执行一次的事件
 */
sealed class IntroductionEffect {
    data class NavigateToRead(val chapterIndex: Int) : IntroductionEffect()
}