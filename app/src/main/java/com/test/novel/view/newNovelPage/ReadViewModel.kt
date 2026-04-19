package com.test.novel.view.newNovelPage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.novel.database.chapter.Chapter
import com.test.novel.database.chapter.ChapterDao
import com.test.novel.database.readHistory.ReadHistoryDao
import com.test.novel.model.mapper.ChapterMapper
import com.test.novel.model.vo.ReadingPageVo
import com.test.novel.utils.BookDataMaker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReadViewModel @Inject constructor() : ViewModel() {

    private val _showBar = MutableStateFlow(false)
    val showBar = _showBar.asStateFlow()

    private val _readState = MutableStateFlow(ReadViewUiState())
    val readState = _readState.asStateFlow()
    fun isNotEmpty(){

    }
    fun showOrHideBar(){
        _showBar.value = !_showBar.value
    }
    fun sendIntent(readIntent: ReadIntent){
        when(readIntent){
            is ReadIntent.ShowOrHideBar -> {
                showOrHideBar()
            }
            is ReadIntent.LoadChapterWithId -> {
                loadChapter(readIntent.chapterId)
            }
            is ReadIntent.TurnPage -> {
                turnPage(readIntent.pageIndex)
            }
        }

    }

    private fun turnPage(pageIndex: Int) {
        if (_readState.value.readingPageVo == null){
            return
        }
        if (pageIndex < 0) {

        }
        if (pageIndex > _readState.value.readingPageVo!!.totalIndex){

        }
        _readState.value = _readState.value.copy(

        )
    }

    fun loadChapter(chapterId: Int){
        Log.d("ReadViewModel", "loadChapter called with chapterId: $chapterId")
        viewModelScope.launch {

            if (_readState.value.isLoading) {
                Log.d("ReadViewModel", "Already loading, skipping")
                return@launch // 防止重复加载
            }else {
                _readState.value = _readState.value.copy(
                    isLoading = true
                )
            }
            val chapterDto = BookDataMaker.generateMockChapter(chapterId)
            val chapterVo = ChapterMapper.dtoToVo(chapterDto)
            Log.d("ReadViewModel", "Chapter loaded, content length: ${chapterDto.content.length}")
            _readState.value = _readState.value.copy(
                readingPageVo = ReadingPageVo(
                    chapterId = chapterVo.id,
                    chapterTitle = chapterVo.title,
                    pageIndex = 0,
                    totalIndex = 1,
                    content = chapterDto.content
                )
            )
            Log.d("ReadViewModel", "readState updated: ${_readState.value}")
            _readState.value = _readState.value.copy(
                isLoading = false
            )
        }
    }
}