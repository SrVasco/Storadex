package com.example.storadex.model

data class CollectionData(
    val id: String = "",
    val name: String = "",
    val cards: List<CardData> = emptyList()
)

data class CardData(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val idcol: Int = 0,
    val isCollected: Boolean = false // nuevo campo
)