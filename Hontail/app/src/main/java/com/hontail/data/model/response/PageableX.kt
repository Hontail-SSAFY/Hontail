package com.hontail.data.model.response

data class PageableX(
    val offset: Int,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    val sort: SortXX,
    val unpaged: Boolean
)