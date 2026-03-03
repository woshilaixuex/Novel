package com.test.novel.view.introductionPage

import kotlinx.serialization.Serializable

@Serializable
data class BookBriefVo(
    val title: String = "title",
    val bookId: Int = 0,
    val coverUrl: String = "coverUrl",
    val author: String = "author",
    val type:List<String> = listOf("玄幻","恋爱"),
    val status:String = "status",
    val brief : String = "brief",
    val isLocal:Boolean = false,
    val chapters: List<ChapterVo> = emptyList(),
)
@Serializable
data class ChapterVo(
    val bookId: Int = 0,
    val chapterId: Int = 0, // 章节编号
    val title: String = "",
    val page: Int = 0, // 分页
)