package com.test.novel.view.introductionPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.novel.model.vo.BookVo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class IntroductionViewModel(
) : ViewModel() {
    private val _introductionState = MutableStateFlow(IntroductionUiState())
    val introductionState = _introductionState.asStateFlow()
    private val _effect = Channel<IntroductionEffect>()
    val effect = _effect.receiveAsFlow()

    fun sendIntent(intent: IntroductionIntent) {
        when(intent) {
            is IntroductionIntent.LoadBookDetailVo -> {
                loadBookDetailVo(intent.bookVo)
            }
            is IntroductionIntent.StartingRead ->{
                readingChapter(0)
            }
            is IntroductionIntent.StartingReadWithChapterId -> {
                readingChapter(intent.chapterId)
            }
            is IntroductionIntent.RefreshData -> {
                refreshData()
            }
            is IntroductionIntent.ToggleFavorite -> {
                toggleFavorite()
            }
        }
    }

    // 新版数据加载
    private fun loadBookDetailVo(bookVo: BookVo) {
        _introductionState.value = _introductionState.value.copy(
            isLoading = true,
            error = null
        )
        viewModelScope.launch {
            _introductionState.value = _introductionState.value.copy(
                bookVo = bookVo,           // 书籍基本信息
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
        val currentBook = _introductionState.value.bookVo
        if (currentBook != null) {
            loadBookDetailVo(currentBook)
        }
    }

    private fun readingChapter(chapterId: Int) {
        viewModelScope.launch {
            _effect.send(IntroductionEffect.NavigateToRead(chapterId))
        }
    }

    /**
     * 切换收藏状态
     */
    private fun toggleFavorite() {

    }

}