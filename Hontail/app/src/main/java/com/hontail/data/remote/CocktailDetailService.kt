package com.hontail.data.remote

import com.hontail.data.model.response.CocktailDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CocktailDetailService {

    // 칵테일 상세 정보 조회
    @GET("api/recipe/{cocktailId}")
    suspend fun getCocktailDetail(
        @Path("cocktailId") cocktailId: Int
    ): Response<CocktailDetailResponse>
}