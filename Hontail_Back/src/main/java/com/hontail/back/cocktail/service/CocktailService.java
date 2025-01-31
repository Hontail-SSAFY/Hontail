package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.repository.CocktailSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CocktailService {

    public Page<CocktailSummaryDto> getCocktailsByFilter(
            @RequestParam(required = false, defaultValue = "id", value = "orderBy") String orderBy,
            @RequestParam(required = false, defaultValue = "asc", value = "direction") String direction,
            @RequestParam(required = false) String baseSpirit,
            int page, int size);

}
