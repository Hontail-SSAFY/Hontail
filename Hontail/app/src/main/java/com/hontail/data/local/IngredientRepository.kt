package com.hontail.data.local

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.hontail.base.ApplicationClass
import com.hontail.data.model.dto.IngredientsTable
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "IngredientRepository"
class IngredientRepository private constructor(context: Context) {

    private val database = Room.databaseBuilder(
        context.applicationContext,
        IngredientDatabase::class.java,
        "ingredient_database.db"
    ).build()
    private val ingredientDAO = database.getIngredientDAO()
    private val ingredientService = RetrofitUtil.ingredientService

    // 서버에서 재료 데이터를 가져와 Room에 저장
    fun refreshIngredients() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "refreshIngredients 시작")
            try {
                val ingredients: List<IngredientsTable> = ingredientService.getAllIngredients()
                Log.d(TAG, "서버 응답: ${ingredients.size}")
                ingredientDAO.insertAll(ingredients)
                Log.d(TAG, "데이터 삽입 완료")
            } catch (e: Exception) {
                Log.e(TAG, "예외 발생: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }


    // Room에 저장된 재료 데이터를 가져오기
    suspend fun getIngredients(): List<IngredientsTable> {
        return ingredientDAO.getAllIngredients()
    }

    // Room에 저장된 재료 데이터 검색어로 가져오기.
    fun getIngredientsByNameKor(query: String): LiveData<List<IngredientsTable>> {
        return ingredientDAO.getIngredientsByNameKor(query)
    }

    // Room에 저장된 재료 데이터 Id로 가져오기.
    fun getIngredientsById(ingredientId: Int): LiveData<IngredientsTable> {
        return ingredientDAO.getIngredientsById(ingredientId)
    }

    // Singleton 패턴 적용
    companion object {
        private var INSTANCE: IngredientRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = IngredientRepository(context)
            }
        }

        fun getInstance(): IngredientRepository {
            return INSTANCE ?: throw IllegalStateException("IngredientRepository must be initialized")
        }
    }

}