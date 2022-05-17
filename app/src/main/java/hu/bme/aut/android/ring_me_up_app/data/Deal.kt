package hu.bme.aut.android.ring_me_up_app.data

data class Deal(
    val uid: String? = null,
    val author: String? = null,
    val debtor: String? = null,
    val debtSum: String? = null,
    val item: String? = null,
    val date: String? = null,
    val isSettled: Boolean = false
)