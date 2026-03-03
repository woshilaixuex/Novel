package com.test.novel.model.domain

import kotlinx.serialization.Serializable

/**
 * 统一的书籍领域模型
 * 这是核心业务模型，包含所有必要的业务字段
 */
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String,
    val description: String,
    val categories: List<String>,
    val status: BookStatus,
    val wordCount: Long,
    val updateTime: String,
    val chapters: List<Chapter>,
    val readingProgress: ReadingProgress? = null,
    val isFavorite: Boolean = false,
    val isLocal: Boolean = false
)

enum class BookStatus {
    ONGOING,    // 连载中
    COMPLETED,  // 已完结
    PAUSED      // 暂停
}

/**
 * 阅读进度
 */
data class ReadingProgress(
    val chapterId: String,
    val pageIndex: Int,
    val lastReadTime: String,
    val readPercentage: Float
)
