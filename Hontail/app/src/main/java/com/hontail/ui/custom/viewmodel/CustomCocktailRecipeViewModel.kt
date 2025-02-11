package com.hontail.ui.custom.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hontail.data.model.request.Ingredient
import com.hontail.data.model.request.Recipe
import com.hontail.data.remote.RetrofitUtil
import com.hontail.ui.custom.screen.CustomCocktailItem
import com.hontail.util.CommonUtils

private const val TAG = "CustomCocktailRecipeVie"
class CustomCocktailRecipeViewModel: ViewModel() {

    private val s3Service = RetrofitUtil.s3Service

    // 1. 레시피 이미지 (CustomCocktailRecipeItem.CustomCocktailRecipeImage는 파라미터가 없는 object로 가정)
    private val _recipeImage = MutableLiveData<Uri>()
    val recipeImage: LiveData<Uri> get() = _recipeImage

    // 2. 칵테일 이름
    private val _recipeName = MutableLiveData<String>()
    val recipeName: LiveData<String> get() = _recipeName

    // 3. 도수 (예시로 기본 도수 25%)
    private val _alcoholLevel = MutableLiveData<Int>()
    val alcoholLevel: LiveData<Int> get() = _alcoholLevel

    // 4. 칵테일 설명
    private val _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description



    // ※ 이미지 업로드 후 최종 이미지 URL을 저장할 LiveData (uploadImageToServer의 응답 URL에서 확장자까지의 부분)
    private val _uploadedImageUrl = MutableLiveData<String>()
    val uploadedImageUrl: LiveData<String> get() = _uploadedImageUrl

    // ✅ 새롭게 관리할 ingredient 리스트 (ingredientId, ingredientQuantity만 포함)
    private val _recipeIngredients = MutableLiveData<List<Ingredient>>()
    val recipeIngredients: LiveData<List<Ingredient>> get() = _recipeIngredients

    /**
     * ✅ ActivityViewModel에서 받아온 ingredientList를 가공하여 저장
     */
    fun setRecipeIngredients(ingredientList: List<CustomCocktailItem>) {
        _recipeIngredients.value = ingredientList
            .filterIsInstance<CustomCocktailItem.IngredientItem>() // ✅ IngredientItem만 필터링
            .map { ingredient ->
                Ingredient(
                    ingredientId = ingredient.ingredientId,
                    ingredientQuantity = ingredient.ingredientQuantity
                )
            } // ✅ 필요한 데이터만 추출
    }

    /**
     * 레시피 데이터를 초기화.
     *
     * - REGISTER 모드.
     * - MODIFY 모드:
     */
    fun initializeRecipeData(mode: CommonUtils.CustomCocktailRecipeMode) {
        if (mode == CommonUtils.CustomCocktailRecipeMode.REGISTER) {
            Log.d(TAG, "initializeRecipeData: 등록모드입니다.")
            _recipeImage.value = Uri.EMPTY
            _recipeName.value = ""
            _alcoholLevel.value = 25
            _description.value = ""
        }
        else {
            Log.d(TAG, "initializeRecipeData: 수정모드입니다.")
            loadExistingRecipeData()
        }
    }

    /**
     * 기존 레시피 데이터를 불러오는 함수 (수정 모드용)
     */
    private fun loadExistingRecipeData() {
        // TODO: 실제 데이터를 불러오는 로직을 구현
        // 예시로 더미 데이터를 설정합니다.
        _recipeImage.value = Uri.EMPTY
        _recipeName.value = "완성된 칵테일 이름 수정할 것."
        _alcoholLevel.value = 30
        _description.value = "수정된 칵테일 설명"
    }

    // === 각 항목별 업데이트 메서드 ===

    fun updateRecipeImage(uri: Uri) {
        _recipeImage.value = uri
    }

    fun updateRecipeName(name: String) {
        _recipeName.value = name
    }

    fun updateAlcoholLevel(level: Int) {
        _alcoholLevel.value = level
    }

    fun updateDescription(description: String) {
        _description.value = description
    }

    // === 레시피 단계 리스트 관련 메서드 ===



    // s3 URL 받아오기.
    suspend fun uploadImageToServer(fileName: String): String {

        return try {
            val response = s3Service.insertS3(fileName)
            Log.d("S3Upload", "서버 응답: $response")
            response
        }
        catch (e: Exception) {
            Log.e("S3Upload", "이미지 업로드 실패", e)
            ""
        }
    }

    // 서버에 올릴 이미지 url 뽑아내기
    private fun extractImageUrl(fullUrl: String): String {
        val extensions = listOf(".jpg", ".jpeg", ".png")
        for (ext in extensions) {
            val index = fullUrl.indexOf(ext)
            if (index != -1) {
                return fullUrl.substring(0, index + ext.length)
            }
        }
        return fullUrl
    }

    // S3 업로드 성공 시, 최종 URL을 저장하는 함수 (Fragment의 onSuccess 콜백 등에서 호출)
    fun setUploadedImageUrl(fullUrl: String) {
        _uploadedImageUrl.postValue(extractImageUrl(fullUrl))
    }
}