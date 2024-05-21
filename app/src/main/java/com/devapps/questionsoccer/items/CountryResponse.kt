package com.devapps.questionsoccer.items

data class CountryResponse(
    val get: String,
    val parameters: Any,
    val errors: Any,
    val results: Int,
    val paging: CountryPaging,
    val response: List<ItemCountry>
)

data class CountryPaging(
    val current: Int,
    val total: Int
)

data class ItemCountry(
    val name: String,
    val code: String,
    val flag: String
)