package com.hontail.data.local

import android.content.Context
import androidx.room.Room
import com.hontail.data.local.dao.SearchHistoryDao
import com.hontail.data.model.dto.SearchHistoryTable

class SearchHistoryRepository private constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        SearchHistoryDatabase::class.java,
        "search_history_database.db"
    ).build()

    private val searchHistoryDao: SearchHistoryDao = database.getSearchHistoryDao()

    companion object {
        @Volatile
        private var INSTANCE: SearchHistoryRepository? = null

        fun getInstance(context: Context): SearchHistoryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SearchHistoryRepository(context).also { INSTANCE = it }
            }
        }
    }

    // 검색어 삽입 (중복이면 삭제 후 삽입)
    suspend fun insertSearch(searchText: String) {
        searchHistoryDao.deleteByHistory(searchText) // 중복 삭제
        searchHistoryDao.insertSearch(SearchHistoryTable(searchHistory = searchText))
        searchHistoryDao.deleteOldestIfNeeded() // 10개 초과 시 삭제
    }

    // ID로 삭제
    suspend fun deleteById(id: Int) {
        searchHistoryDao.deleteById(id)
    }

    // 검색어로 삭제
    suspend fun deleteByHistory(searchText: String) {
        searchHistoryDao.deleteByHistory(searchText)
    }

    // 전체 검색어 가져오기
    suspend fun getAllSearches(): List<SearchHistoryTable> {
        return searchHistoryDao.getAllSearches()
    }
}
