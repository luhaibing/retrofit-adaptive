@file:Suppress("unused")

package com.mercer.adaptive.processor.util

import com.mercer.adaptive.processor.constant.PRIMEVAL_MAP
import com.mercer.adaptive.processor.model.AppendRecord
import com.mercer.adaptive.annotate.Append
import com.mercer.adaptive.annotate.Appends
import com.mercer.adaptive.annotate.Conversion
import com.mercer.adaptive.core.AppendProvider
import com.mercer.adaptive.core.ParameterValueConverter
import com.squareup.kotlinpoet.*
import java.lang.IllegalArgumentException
import java.security.MessageDigest
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.reflect.KClass

fun <A : Annotation> Element.hasAnnotation(zlass: Class<A>) = getAnnotation(zlass) != null

fun <A : Annotation> Element.hasAnnotation(zlass: KClass<A>) = getAnnotation(zlass.java) != null

/**
 * 去除首部符号
 */
fun clearHeaderSymbol(input: String): String {
    return recursion(input, { it.startsWith("/") }, { it.substring(1) })
}

/**
 * 递归
 */
fun <T> recursion(input: T, predicate: (T) -> Boolean, block: (T) -> T): T {
    if (!predicate(input)) {
        return input
    }
    return recursion(block(input), predicate, block)
}

fun encrypt(dataStr: String): String {
    try {
        val m = MessageDigest.getInstance("MD5")
        m.update(dataStr.toByteArray(charset("UTF8")))
        val s = m.digest()
        var result = ""
        for (i in s.indices) {
            result += Integer.toHexString(0x000000FF and s[i].toInt() or -0x100).substring(6)
        }
        return result.uppercase(Locale.ROOT)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun ClassName.toClass(): Class<*> = Class.forName(toString().replace("`", ""))

fun String.toClassName(): ClassName = ClassName.bestGuess(this)

fun TypeMirror.toTypeName(): TypeName = asTypeName()

fun DeclaredType.toTypeName(): TypeName = asElement().asType().toTypeName()

fun Element.toTypeName(): TypeName = asType().toTypeName()

fun Element.name(): String = this.simpleName.toString()

/**
 * 判定是否为键值对集合
 */
fun TypeName.isMap(): Boolean {
    if (this !is ParameterizedTypeName) {
        return false
    }
    val rawType = rawType
    if (rawType == MAP) {
        return true
    }
    return try {
        Map::class.java.isAssignableFrom(rawType.toClass())
    } catch (e: Exception) {
        false
    }
}

/**
 * 判定是否为集合
 */
fun TypeName.isCollection(): Boolean {
    if (this !is ParameterizedTypeName) {
        return false
    }
    val rawType = this.rawType
    if (rawType == COLLECTION) {
        return true
    }
    return try {
        Collection::class.java.isAssignableFrom(rawType.toClass())
    } catch (e: Exception) {
        false
    }
}

/**
 * 判定是否为键值对集合
 */
fun VariableElement.isMap(): Boolean = toTypeName().isMap()

/**
 * 判定是否为集合
 */
fun VariableElement.isCollection(): Boolean = toTypeName().isCollection()

/**
 * 判定是否为基本类型
 */
fun VariableElement.isPrimitive(): Boolean = toTypeName().isPrimitive()

/**
 * 判定是否为基本类型
 */
fun TypeName.isPrimitive(): Boolean = PRIMEVAL_MAP.containsKey(this)

/**
 * 采集
 * 收集附加是的参数
 */
fun Element.collectProviders(types: Types, elements: Elements): List<AppendRecord> {
    val values = arrayListOf<AppendRecord>()
    val typeMirror = elements.getTypeElement(AppendProvider::class.java.canonicalName).asType()
    getAnnotation(Append::class.java)?.let {
        values.add(AppendRecord(it.key, it.appendKey,
            parseTryMirroredTypeException(types, typeMirror) { it.provider }
        ))
    }
    getAnnotation(Appends::class.java)?.values?.onEach {
        values.add(AppendRecord(it.key, it.appendKey,
            parseTryMirroredTypeException(types, typeMirror) { it.provider }
        ))
    }
    return values
}

fun Element.collectConvert(types: Types, elements: Elements): TypeName? {
    val typeMirror =
        elements.getTypeElement(ParameterValueConverter::class.java.canonicalName).asType()
    return getAnnotation(Conversion::class.java)?.let {
        parseTryMirroredTypeException(types, typeMirror) { it.value }
    }
}

fun parseTryMirroredTypeException(
    types: Types,
    typeMirror: TypeMirror,
    block: () -> Unit
): TypeName {
    var value: TypeName? = null
    try {
        block()
    } catch (e: MirroredTypeException) {
        if (!types.isSubtype(e.typeMirror, typeMirror)) {
            throw IllegalArgumentException("${e.typeMirror} Not a subclass implementation of ${typeMirror}.")
        }
        value = types.asElement(e.typeMirror).asType().toTypeName()
    }
    return value!!
}

/**
 * 避免重复命名
 */
tailrec fun avoidNameDuplication(name: String, names: List<String>, offset: Int = 0): String {
    val value = name + if (offset == 0) "" else offset.toString()
    if (!names.contains(value)) {
        return value
    }
    return avoidNameDuplication(name, names, offset + 1)
}