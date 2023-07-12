package com.example.gunlukis.models

data class chat(
    val uid: String? = null,
    val chat: String? = null,
    val time: Long? = null,
    val goruldu: Boolean? = null,
    val type: String? = null,
    var konusulananKullaniciId : String? = null,
    var sonMesaj: String? = null,
    var whichUser: String? = null,
    var postId: String? = null
) {
}