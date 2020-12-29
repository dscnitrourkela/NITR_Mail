package github.sachin2dehury.nitrmail.ui

interface ActivityExt {

    fun toggleActionBar(isEnabled: Boolean)

    fun showSnackbar(message: String)

    fun toggleDrawer(isEnabled: Boolean)

    fun toggleSyncService(isRunning: Boolean)

    fun hideKeyBoard()
}