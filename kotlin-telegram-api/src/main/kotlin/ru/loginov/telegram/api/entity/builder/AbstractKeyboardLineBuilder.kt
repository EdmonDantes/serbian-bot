package ru.loginov.telegram.api.entity.builder

/**
 * Abstract class for creating custom keyboard lines builders
 *
 * @param T buttons class
 * @param B button builder class
 */
abstract class AbstractKeyboardLineBuilder<T, B : AbstractKeyboardButtonBuilder<T>> {
    private var line: MutableList<B> = ArrayList()
    private var width: Long? = null

    val size: Int
        get() = line.size

    fun add(block: B.() -> Unit) {
        if (width == null || line.size < width!!) {
            val builder = createKeyboardBuilder()
            block(builder)
            line.add(builder)
        } else {
            error("Limit buttons reached = '$width'")
        }
    }

    fun maxWidth(width: Number?) {
        this.width = width?.toLong()
    }

    fun maxWidth(block: () -> Number?) {
        this.width = block()?.toLong()
    }

    fun build(): List<T> = line.map { it.build() }

    protected abstract fun createKeyboardBuilder(): B
}