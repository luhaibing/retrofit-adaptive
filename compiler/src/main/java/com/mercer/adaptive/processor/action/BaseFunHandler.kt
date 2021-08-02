package com.mercer.adaptive.processor.action

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

abstract class BaseFunHandler(
    private val messenger: Messager,
) : FunHandler {

    protected fun error(message: String) {
        messenger.printMessage(Diagnostic.Kind.ERROR, message)
    }

}