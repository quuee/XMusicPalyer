package cn.x.ui.componets

class DrawerControl internal constructor(
    val open: () -> Unit,
    val close: () -> Unit,
    val isOpen: Boolean
) {
    fun toggle() {
        if (isOpen) close() else open()
    }
}