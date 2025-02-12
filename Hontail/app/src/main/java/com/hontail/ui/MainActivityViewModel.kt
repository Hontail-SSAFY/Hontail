package com.hontail.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.Cocktail
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch

private const val TAG = "MainActivityViewModel_SSAFY"

class MainActivityViewModel(private val handle: SavedStateHandle) : ViewModel() {
    // 사용자 아이디
    var userId = handle.get<Int>("userId") ?: 0
        set(value) {
            handle.set("userId", value)
            field = value
        }

    var userNickname = handle.get<String>("userNickname") ?: ""
        set(value) {
            handle.set("userNickname", value)
            field = value
        }

    // 선택된 cocktailId
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int>
        get() = _cocktailId

    // Vision API로 분석한 텍스트 저장
    private val _ingredientList = MutableLiveData<List<String>>()
    val ingredientList: LiveData<List<String>>
        get() = _ingredientList

    fun setCocktailId(cocktailId: Int){
        _cocktailId.postValue(cocktailId)
    }

    fun setIngredientList(ingredientList: List<String>){
        _ingredientList.postValue(ingredientList)
    }

    // 선택된 칵테일 베이스주 타입
    private val _baseSpirit = MutableLiveData<String>("")
    val baseSpirit: LiveData<String>
        get() = _baseSpirit

    fun setBaseSpirit(baseSpirit: String){
        _baseSpirit.postValue(baseSpirit)
    }
    // 선택된 ingredientId
    private val _ingredientId = MutableLiveData<Int>()
    val ingredientId: LiveData<Int> get() = _ingredientId

    fun setIngredientId(ingredientId: Int) {
        _ingredientId.postValue(ingredientId)
    }

    // 필터 관련 코드 추가
    private val _selectedZzimFilter = MutableLiveData<Int?>()
    val selectedZzimFilter: LiveData<Int?> = _selectedZzimFilter

    private val _selectedTimeFilter = MutableLiveData<Int?>()
    val selectedTimeFilter: LiveData<Int?> = _selectedTimeFilter

    private val _selectedAlcoholFilter = MutableLiveData<Int?>()
    val selectedAlcoholFilter: LiveData<Int?> = _selectedAlcoholFilter

    private val _selectedBaseFilter = MutableLiveData<Int?>()
    val selectedBaseFilter: LiveData<Int?> = _selectedBaseFilter

    private val _zzimButtonSelected = MutableLiveData<Boolean>()
    val zzimButtonSelected: LiveData<Boolean> = _zzimButtonSelected

    private val _timeButtonSelected = MutableLiveData<Boolean>()
    val timeButtonSelected: LiveData<Boolean> = _timeButtonSelected

    private val _alcoholButtonSelected = MutableLiveData<Boolean>()
    val alcoholButtonSelected: LiveData<Boolean> = _alcoholButtonSelected

    private val _baseButtonSelected = MutableLiveData<Boolean>()
    val baseButtonSelected: LiveData<Boolean> = _baseButtonSelected

    fun setZzimFilter(radioButtonId: Int) {
        _selectedZzimFilter.value = radioButtonId
    }

    fun setTimeFilter(radioButtonId: Int) {
        _selectedTimeFilter.value = radioButtonId
    }

    fun setAlcoholFilter(radioButtonId: Int) {
        _selectedAlcoholFilter.value = radioButtonId
    }

    fun setBaseFilter(radioButtonId: Int) {
        _selectedBaseFilter.value = radioButtonId
    }

    fun updateZzimButtonState(selected: Boolean) {
        _zzimButtonSelected.value = selected
    }

    fun updateTimeButtonState(selected: Boolean) {
        _timeButtonSelected.value = selected
    }

    fun updateAlcoholButtonState(selected: Boolean) {
        _alcoholButtonSelected.value = selected
    }

    fun updateBaseButtonState(selected: Boolean) {
        _baseButtonSelected.value = selected
    }
}