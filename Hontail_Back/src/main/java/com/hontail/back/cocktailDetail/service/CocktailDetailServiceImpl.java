package com.hontail.back.cocktailDetail.service;

import com.hontail.back.cocktailDetail.dto.CocktailDetailDto;
import com.hontail.back.cocktailDetail.dto.CocktailIngredientDetailDto;
import com.hontail.back.cocktailDetail.dto.IngredientDetailDto;
import com.hontail.back.cocktailDetail.dto.RecipeDetailDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CocktailDetailServiceImpl implements CocktailDetailService {

    @Autowired
    private CocktailRepository cocktailRepository;

    @Override
    public CocktailDetailDto getCocktailDetail(Integer cocktailId) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new RuntimeException("칵테일을 찾을 수 없습니다."));

        return new CocktailDetailDto(
                cocktail.getId(),
                cocktail.getCocktailName(),
                cocktail.getCocktailDescription(),
                cocktail.getImageUrl(),
                cocktail.getCocktailIngredients().stream()
                        .map(ci -> new CocktailIngredientDetailDto(
                                ci.getId(),
                                ci.getIngredientQuantity(),
                                new IngredientDetailDto(
                                        ci.getIngredient().getId(),
                                        ci.getIngredient().getIngredientNameKor(),
                                        ci.getIngredient().getIngredientImage()
                                )
                        ))
                        .toList(),
                cocktail.getRecipes().stream()
                        .map(r -> new RecipeDetailDto(
                                r.getId(),
                                r.getRecipeGuide(),
                                r.getSequence()
                        ))
                        .toList()
        );
    }
}