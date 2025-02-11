package com.hontail.ui.custom.screen

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
//import com.hontail.ui.custom.adapter.CustomCocktailRecipeAdapter
import com.hontail.ui.custom.viewmodel.CustomCocktailRecipeViewModel
import com.hontail.util.CommonUtils
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

private const val TAG = "CustomCocktailRecipeFra"
class CustomCocktailRecipeFragment: BaseFragment<FragmentCustomCocktailRecipeBinding>(
    FragmentCustomCocktailRecipeBinding::bind,
    R.layout.fragment_custom_cocktail_recipe
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CustomCocktailRecipeViewModel by viewModels()

//    private lateinit var customCocktailRecipeAdapter: CustomCocktailRecipeAdapter

    private lateinit var recipeMode: CommonUtils.CustomCocktailRecipeMode

    private lateinit var getImageLauncher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerImageLauncher()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCustomCocktailRecipe()
        initToolbar()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarCustomCocktailRecipe.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack(
                        "CustomCocktailRecipeFragment",
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        }
    }

    // ViewModel Observe 등록
    private fun observeCustomCocktailRecipe() {

        binding.apply {

            // 레시피 모드 등록 / 수정
            activityViewModel.recipeMode.observe(viewLifecycleOwner) { mode ->
                recipeMode = mode
                viewModel.initializeRecipeData(mode)
            }

            // 칵테일 이미지
            viewModel.recipeImage.observe(viewLifecycleOwner) { imageUri ->

                if(imageUri != Uri.EMPTY) {
                    constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.GONE
                    imageViewListItemCustomCocktailRecipeImage.setImageURI(imageUri)
                }
                else {
                    imageViewListItemCustomCocktailRecipeImage.setImageResource(R.color.basic_gray)
                    constraintLayoutListItemCustomCocktailRecipeImageGuide.visibility = View.VISIBLE
                }
            }
            
            
            // 칵테일 이름 EditText와 ViewModel 연결
            editTextListItemCustomCocktailRecipeName.addTextChangedListener { text ->
                viewModel.updateRecipeName(text.toString())
            }
            
            // LiveData로 EditText 값 유지
            viewModel.recipeName.observe(viewLifecycleOwner) { name ->
                if(editTextListItemCustomCocktailRecipeName.text.toString() != name) {
                    editTextListItemCustomCocktailRecipeName.setText(name)
                }
            }

            // 칵테일 설명 EditText와 ViewModel 연결
            editTextListItemCustomCocktailRecipeDescription.addTextChangedListener { text ->
                viewModel.updateDescription(text.toString())
            }

            // LiveData로 EditText 값 유지
            viewModel.description.observe(viewLifecycleOwner) { description ->
                if(editTextListItemCustomCocktailRecipeDescription.text.toString() != description) {
                    editTextListItemCustomCocktailRecipeDescription.setText(description)
                }
            }
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 이미지 추가.
            constraintLayoutListItemCustomCocktailRecipeImageGuide.setOnClickListener {
                getImageLauncher.launch("image/*")
            }

            // 이미지 수정
            imageViewListItemCustomCocktailRecipeImage.setOnClickListener {
                getImageLauncher.launch("image/*")
            }
            
            // 등록
            buttonCustomCocktailRecipeRegister.setOnClickListener { 
                
                val imageUrl = viewModel.uploadedImageUrl.value
                Log.d(TAG, "initEvent: imageUrl: $imageUrl")
                
                val name = viewModel.recipeName.value
                Log.d(TAG, "initEvent: name: $name")

                val description = viewModel.description.value
                Log.d(TAG, "initEvent: description: $description")
            }
        }
    }

    // 이미지 런처 등록
    private fun registerImageLauncher() {

        getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            if(uri != null) {

                viewModel.updateRecipeImage(uri)

                val imageFileName = getImageFileName(uri)

                lifecycleScope.launch {
                    val presignedUrl = viewModel.uploadImageToServer(imageFileName)
                    Log.d(TAG, "registerImageLauncher: $presignedUrl")

                    if (presignedUrl.isNotBlank()) {
                        // presignedUrl이 정상적으로 받아졌다면 S3로 업로드를 진행합니다.
                        uploadImageToS3(presignedUrl, uri, requireContext(),
                            onSuccess = {
                                Log.d(TAG, "registerImageLauncher: 이미지 업로드 성공")
                                viewModel.setUploadedImageUrl(presignedUrl)
                            },
                            onFailure = { error ->
                                Log.d(TAG, "registerImageLauncher: 이미지 업로드 실패")
                            }
                        )
                    } else {
                        // 오류 처리: presignedUrl이 비어 있다면, 서버 호출에 실패한 것임.
                        Log.d(TAG, "registerImageLauncher: 서버 통신 오류: presigned URL을 받아오지 못했습니다.")
                    }
                }
            }
        }
    }

    // 이미지 파일 이름 가져오기
    @SuppressLint("Range")
    private fun getImageFileName(uri: Uri): String {

        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = mainActivity.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "default_filename"
    }

    private fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImageToS3(
        presignedUrl: String,
        imageUri: Uri,
        context: Context,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        // 1. 이미지 데이터를 바이트 배열로 읽음
        val imageBytes = readBytesFromUri(context, imageUri)
        if (imageBytes == null) {
            onFailure(Exception("이미지 데이터를 읽어오지 못했습니다."))
            return
        }

        // 2. ContentResolver로 MIME 타입을 가져옴
        val mimeType = context.contentResolver.getType(imageUri) ?: "image/jpeg"

        // 3. OkHttp를 사용하여 PUT 요청 생성
        val client = OkHttpClient()
        val mediaType = mimeType.toMediaType()
        val requestBody = imageBytes.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(presignedUrl)
            .put(requestBody)
            .addHeader("Content-Type", mimeType)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 업로드 실패 처리
                onFailure(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // 업로드 성공
                    onSuccess()
                } else {
                    onFailure(Exception("업로드 실패: ${response.code} ${response.message}"))
                }
                response.close()
            }
        })
    }

}

data class CocktailRecipeStep(var stepNumber: Int, val selectedAnimation: CommonUtils.CustomCocktailRecipeAnimationType, var description: String)