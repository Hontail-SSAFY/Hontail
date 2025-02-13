package com.hontail.ui.picture.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.Cocktail
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "CocktailPictureResultFr_SSAFY"

class CocktailPictureResultFragmentViewModel(private val handle: SavedStateHandle): ViewModel() {
    // Vision API로 분석한 텍스트 저장
    var detectedTextList = handle.get<List<String>>("detectedTextList") ?: emptyList()
        set(value){
            handle.set("detectedTextList", value)
            field = value
        }

    var userId = handle.get<Int>("userId") ?: 0
        set(value) {
            handle.set("userId", value)
            field = value
        }

    // 분석 결과 재료 리스트
    private val _ingredientList = MutableLiveData<List<String>>()
    val ingredientList: LiveData<List<String>>
        get() = _ingredientList

    // Vision API로 분석한 결과 칵테일 리스트
    private val _ingredientAnalyzeCoctailList = MutableLiveData<List<CocktailListResponse>>()
    val ingredientAnalyzeCoctailList: LiveData<List<CocktailListResponse>>
        get() = _ingredientAnalyzeCoctailList

    fun getIngredientAnalyze(){
        getIngredientAnalyze(detectedTextList)
    }

    fun getIngredientAnalyze(analyzeList: List<String>){

        Log.d(TAG, "getIngredientAnalyze: ${analyzeList}")
        viewModelScope.launch {
            runCatching {
                RetrofitUtil.pictureService.ingredientAnalyze(userId, analyzeList)
            }.onSuccess {
                Log.d(TAG, "AI getIngredientAnalyze: ${it}")
                it.body()?.let {
                    Log.d(TAG, "AI body getIngredientAnalyze: ${it}")
                    _ingredientAnalyzeCoctailList.postValue(it)
                    Log.d(TAG, "AI my getIngredientAnalyze: ${_ingredientAnalyzeCoctailList.value}")
                }
            }.onFailure {
                Log.d(TAG, "AI getIngredientAnalyze: ${it.message}")
                _ingredientAnalyzeCoctailList.value = emptyList()
            }
        }
    }
}