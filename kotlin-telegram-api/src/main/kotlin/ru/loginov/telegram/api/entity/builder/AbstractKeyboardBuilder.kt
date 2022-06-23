package ru.loginov.telegram.api.entity.builder

/**
 * Abstract class for create custom keyboard builders
 *
 * @param K finally keyboard type
 * @param B buttons class
 * @param LB line builder class
 * @param BB button builder class
 */
abstract class AbstractKeyboardBuilder<K, B, LB : AbstractKeyboardLineBuilder<B, BB>, BB : AbstractKeyboardButtonBuilder<B>> {

    private var keyboardLines = ArrayList<LB>()
    private var height: Int? = null

    val size: Int
        get() = keyboardLines.size

    fun height(height: Number?) {
        this.height = height?.toInt()
    }

    fun height(block: () -> Number?) {
        this.height = block()?.toInt()
    }

    fun line(block: LB.() -> Unit) {
        if (height == null || keyboardLines.size < height!!) {
            val builder = createLineBuilder()
            block(builder)
            if (builder.size > 0) {
                keyboardLines.add(builder)
            }
        } else {
            error("Limit lines reached = '$height'")
        }
    }

    fun build(): K = internalBuild(keyboardLines.map { it.build() })

    protected abstract fun createLineBuilder(): LB

    protected abstract fun internalBuild(keyboard: List<List<B>>): K

}