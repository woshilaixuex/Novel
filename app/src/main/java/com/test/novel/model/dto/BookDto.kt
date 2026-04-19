package com.test.novel.model.dto

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

/**
 * 网络API响应的书籍数据传输对象
 * 对应后端API返回的数据结构
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class BookDto(
    val bookId: String,
    val bookName: String,
    val author: String,
    val coverUrl: String,
    val brief: String,
    val type: List<String>,
    val status: String,
    val wordCount: String,
    val updateTime: String,
    val lastChapterTitle: String? = null,
    val isFavorite: Boolean = false,
)

/**
 * 网络API响应的章节数据传输对象
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ChapterDto(
    val chapterId: String = "id",
    val bookId: String = "",
    val title: String = "",
    val content: String = "",
    val index: Int = 0,
    val wordCount: Int = 0,
    val updateTime: String = "",
    val isVip: Boolean = false,
)
