package com.test.novel.view.bookStore


import com.test.novel.model.mapper.BookMapper
import com.test.novel.utils.BookDataMaker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookStoreViewModel @Inject constructor() : ViewModel() {

    private val _bookStoreState = MutableStateFlow(BookStoreState())
    val bookStoreState = _bookStoreState.asStateFlow()


    fun sendIntent(intent: BookStoreIntent){
        viewModelScope.launch {
            when(intent) {
                is BookStoreIntent.Refresh -> {

                }
                is BookStoreIntent.LoadMore -> {

                }

                is BookStoreIntent.Search -> {

                }
                is BookStoreIntent.InitData -> {
//                    Log.d("TAG", "processIntent: ${_bookStoreState.value}")
//                    withContext(Dispatchers.IO) {
//                        val hot = SimpleWebCrawler.fetchBookList()
//                        _bookStoreState.value = _bookStoreState.value.copy(rank = hot)
//                    }
                }
                is BookStoreIntent.LoadDataWithNet -> {
//                    withContext(Dispatchers.IO) {
//                        val hot = SimpleWebCrawler.fetchBookList()
//                        _bookStoreState.value = _bookStoreState.value.copy(rank = hot)
//                    }
                }
                is BookStoreIntent.ClearData -> {
                    _bookStoreState.value = _bookStoreState.value.copy(
                        rank = emptyList()
                    )
                }
                else -> {
                    // 模拟网络请求
                    withContext(Dispatchers.IO) {
                        _bookStoreState.value = _bookStoreState.value.copy(
                            isLoading = true
                        )
                        delay(2000)
                        val hotDtoList = BookDataMaker.generateMockBooks()
                        val hot = BookMapper.dtoListToVoList(hotDtoList)
                        _bookStoreState.value = _bookStoreState.value.copy(
                            rank = hot,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }


}