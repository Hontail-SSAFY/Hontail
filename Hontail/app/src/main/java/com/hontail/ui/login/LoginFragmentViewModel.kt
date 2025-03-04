package com.hontail.ui.login

import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.base.ApplicationClass
import com.hontail.data.model.request.LoginRequest
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "LoginFragmentViewModel_SSAFY"

class LoginFragmentViewModel : ViewModel() {

//    private val _refreshTk = MutableLiveData<String>()
//    val refreshTk: LiveData<String?> get() = _refreshTk

    private val _userId = MutableLiveData<String>()
    val userId: LiveData<String?> get() = _userId

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String?> get() = _userEmail

    private val _userNickname = MutableLiveData<String>()
    val userNickname: LiveData<String?> get() = _userNickname


    private val _isUserDataReady = MediatorLiveData<Boolean>().apply {
        addSource(_userId) { checkUserDataReady() }
        addSource(_userNickname) { checkUserDataReady() }
    }
    val isUserDataReady: LiveData<Boolean> get() = _isUserDataReady

    private fun checkUserDataReady() {
        _isUserDataReady.value = !(_userId.value.isNullOrEmpty() || _userNickname.value.isNullOrEmpty())
    }


    fun loginWithNaver(accessToken: String) {
        viewModelScope.launch {
            runCatching {
                Log.d(TAG, "loginWithNaver: $accessToken")
                RetrofitUtil.loginService.socialLogin(LoginRequest(token = accessToken, provider = "Naver"))
            }.onSuccess { response ->
                Log.d(TAG, "loginWithNaver: ${response.code()} - ${response.message()} - ${response.body()?.refreshToken} - ${response}")
                if (response.isSuccessful) {
                    val refreshTk = response.body()?.refreshToken ?: return@onSuccess
                    val accessTK = response.body()?.accessToken ?: return@onSuccess

//                    _refreshTk.value = refreshTk  // JWT 저장
                    Log.d(TAG, "Login Success! JWT: ${refreshTk}")

                    ApplicationClass.sharedPreferencesUtil.saveTokens(refreshTk, accessTK)

                    // JWT 디코딩 및 사용자 정보 저장
//                    refreshTk.let { decodeJwt(it) }
                    decodeJwt(accessTK)

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login Failed: $errorBody")
                    Log.e(TAG, "Response code: ${response.code()}, Message: ${response.message()}")
                }
            }.onFailure { throwable ->
                Log.e(TAG, "Network error: ${throwable.message}")
                throwable.printStackTrace()
            }
        }
    }

//    fun loginWithKakao(accessToken: String) {
//        viewModelScope.launch {
//            runCatching {
//                Log.d(TAG, "loginWithKakao: $accessToken")
//                RetrofitUtil.loginService.socialLogin(LoginRequest(token = accessToken, provider = "Kakao"))
//            }.onSuccess { response ->
//                Log.d(TAG, "loginWithKakao: ${response.code()} - ${response.message()}")
//                if (response.isSuccessful) {
//                    val jwt = response.body()?.jwt
//                    _jwtToken.value = jwt!!  // JWT 저장
//                    Log.d(TAG, "Login Success! JWT: ${_jwtToken.value}")
//
//                    // JWT 디코딩 및 사용자 정보 저장
//                    jwt?.let { decodeJwt(it) }
//
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e(TAG, "Login Failed: $errorBody")
//                    Log.e(TAG, "Response code: ${response.code()}, Message: ${response.message()}")
//                }
//            }.onFailure { throwable ->
//                Log.e(TAG, "Network error: ${throwable.message}")
//                throwable.printStackTrace()
//            }
//        }
//    }

    // 🔹 JWT 디코딩 메서드
    private fun decodeJwt(jwt: String) {
        try {
            val parts = jwt.split(".")
            if (parts.size < 2) {
                Log.e(TAG, "Invalid JWT format")
                return
            }

            val payload = parts[1]  // JWT의 두 번째 부분 (Payload)
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)

            Log.d(TAG, "Decoded JWT Payload: $decodedString")

            // JSON 파싱
            val json = JSONObject(decodedString)
            _userId.value = json.optString("user_id", "Unknown")
            _userEmail.value = json.optString("user_email", "Unknown")
            _userNickname.value = json.optString("user_nickname", "Unknown")

            Log.d(TAG, "Extracted UserId: ${_userId.value}, Email: ${_userEmail.value}, userNickname: ${_userNickname.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding JWT: ${e.message}")
        }
    }
}
