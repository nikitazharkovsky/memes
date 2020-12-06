package com.zharkovsky.memes.models

class MemDto(
        var id: String,
        var title: String,
        var description: String,
        var isFavorite: Boolean,
        var createdDate: Int,
        var photoUrl: String
)