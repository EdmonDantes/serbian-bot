package ru.loginov.serbian.bot.telegram.entity

abstract class AbstractKeyboardBuilder<T> {

    protected var keyboardButtons = ArrayList<MutableList<T>>()

    protected var width: Int? = null
    protected var height: Int? = null
    protected var index: Int = 0

    protected fun addButton(button: T) : Boolean {
        val y = width?.let { index / it } ?: 0

        if (height != null && y >= height!!) {
            return false
        }

        while (y >= keyboardButtons.size) {
            keyboardButtons.add(ArrayList())
        }

        val candidate = keyboardButtons[y]
        if (width != null && candidate.size >= width!!) {
            return false
        }
        candidate.add(button)
        index++
        return true
    }

    protected fun setRect(width: Number, height: Number) : Boolean {
        this.width = width.toInt()
        this.height = height.toInt()
        return moveOnResize()
    }

    protected fun setWidth(width: Number) : Boolean {
        this.width = width.toInt()
        return moveOnResize()
    }

    protected fun setHeight(height: Number) : Boolean {
        this.height = height.toInt()
        return moveOnResize()
    }

    private fun moveOnResize(): Boolean {
        index = 0
        if (keyboardButtons.isEmpty()) {
            return true
        }

        val prev = keyboardButtons
        keyboardButtons = ArrayList()

        prev.forEach {
            it.forEach {
                if (!addButton(it)) {
                    return false
                }
            }
        }
        return true
    }

}