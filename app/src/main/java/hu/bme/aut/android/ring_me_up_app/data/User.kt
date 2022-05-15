package hu.bme.aut.android.ring_me_up_app.data

data class User(
    val uid: String? = "",
    val userName: String? = "",
    var totalOwedTo: String? = "",
    var totalOwedFrom: String? = "",
)
