package com.example.gunlukis.models

data class PostJob(
    var ilanAdi : String? = null,
    var ilanFiyati : String? = null,
    var ilanAciklama : String? = null,
    var postId : String? = null,
    var uid : String? = null,
    var isYeri : String? = null,
    var calismaSaati : String? = null
){
    var worker : Boolean? = null
}