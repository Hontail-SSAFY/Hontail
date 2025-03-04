package com.hontail.data.remote

import com.hontail.base.ApplicationClass

class RetrofitUtil {
    companion object{
        // 소셜 로그인 서비스
        val loginService = ApplicationClass.retrofit.create(LoginService::class.java)

        // 댓글 서비스
        val commentService = ApplicationClass.retrofit.create(CommentService::class.java)

        // S3 이미지 등록 서비스
        val s3Service = ApplicationClass.retrofit.create(S3Service::class.java)

        // 칵테일 좋아요 서비스
        val likeService = ApplicationClass.retrofit.create(LikeService::class.java)

        // 바텐더 서비스
        val bartenderService = ApplicationClass.retrofit.create(BartenderService::class.java)

        // 칵테일 상세 서비스
        val cocktailDetailService = ApplicationClass.retrofit.create(CocktailDetailService::class.java)

        // 칵테일 서비스
        val cocktailService = ApplicationClass.retrofit.create(CocktailService::class.java)

        // 칵테일 추천 서비스
        val recommendedCocktailService = ApplicationClass.retrofit9091.create(CocktailService::class.java)

        // 이미지 분석 서비스
        val pictureService = ApplicationClass.retrofit.create(PictureService::class.java)

        // 재료 서비스
        val ingredientService = ApplicationClass.retrofit.create(IngredientService::class.java)

        // 커스텀 칵테일 서비스
        val customCocktailService = ApplicationClass.retrofit.create(CustomCocktailService::class.java)

        // 마이페이지 서비스
        val myPageService = ApplicationClass.retrofit.create(MyPageService::class.java)
    }
}