package com.example.gunlukis.models

data class PostJob(
    var calismaSaati : String? = null,
    var ilanAciklama : String? = null,
    var ilanAdi : String? = null,
    var ilanFiyati : String? = null,
    var isAdresi : String? = null,
    var postId : String? = null,
    var uid : String? = null

){
    var worker : Boolean? = null
}