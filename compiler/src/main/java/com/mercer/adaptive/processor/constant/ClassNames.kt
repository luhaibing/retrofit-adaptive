package com.mercer.adaptive.processor.constant

import com.mercer.adaptive.processor.util.toClassName
import com.mercer.adaptive.annotate.Adaptive
import com.mercer.adaptive.annotate.JsonKey
import com.mercer.adaptive.core.AppendProvider
import com.mercer.adaptive.core.EmptyDynamicUrlProvider
import com.mercer.adaptive.core.OnAdaptiveRetrofit
import com.mercer.adaptive.core.ParameterValueConverter
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import retrofit2.http.*

val ADAPTIVE_FACTORY_CLASS_NAME = Adaptive::class.java.typeName.toString()

/**
 * 返回值类型
 */
val CONTINUATION_CLASS_NAME = "kotlin.coroutines.Continuation".toClassName()
val DEFERRED_CLASS_NAME = "kotlinx.coroutines.Deferred".toClassName()
val FLOW_CLASS_NAME = "kotlinx.coroutines.flow.Flow".toClassName()

/**
 * 原始类型的映射
 */
val PRIMEVAL_SHINE_UPON = hashMapOf(
    java.lang.Object::class.java.asTypeName() to ANY,
    java.lang.String::class.java.asTypeName() to STRING,
    java.lang.Boolean::class.java.asTypeName() to BOOLEAN,
    java.lang.Long::class.java.asTypeName() to LONG,
    java.lang.Short::class.java.asTypeName() to SHORT,
    java.lang.Integer::class.java.asTypeName() to INT,
    java.lang.Float::class.java.asTypeName() to FLOAT,
    java.lang.Double::class.java.asTypeName() to DOUBLE,
    java.lang.Number::class.java.asTypeName() to NUMBER,
)

val PRIMEVAL_MAP = hashMapOf(
    java.lang.String::class.java.asTypeName() to STRING,
    java.lang.Boolean::class.java.asTypeName() to BOOLEAN,
    java.lang.Long::class.java.asTypeName() to LONG,
    java.lang.Short::class.java.asTypeName() to SHORT,
    java.lang.Integer::class.java.asTypeName() to INT,
    java.lang.Float::class.java.asTypeName() to FLOAT,
    java.lang.Double::class.java.asTypeName() to DOUBLE,
    java.lang.Number::class.java.asTypeName() to NUMBER,

    STRING to STRING,
    BOOLEAN to BOOLEAN,
    LONG to LONG,
    SHORT to SHORT,
    INT to INT,
    FLOAT to FLOAT,
    DOUBLE to DOUBLE,
    NUMBER to NUMBER,

    )

/**
 * 集合的映射
 */
val COLLECTION_SHINE_UPON = hashMapOf(
    java.util.Collection::class.java.asTypeName() to COLLECTION,
    java.util.List::class.java.asTypeName() to LIST,
    java.util.Set::class.java.asTypeName() to SET,
    java.util.Map::class.java.asTypeName() to MAP,
)

val CLASS_NAME_KEEP = "androidx.annotation.Keep".toClassName()


// retrofit 第一梯队
const val CLASS_NAME_RETROFIT_FIRST_ECHELON_INDEX = 1
val CLASS_NAME_RETROFIT_FIRST_ECHELON = arrayListOf(
    Header::class.asClassName(),
    HeaderMap::class.asClassName()
)

// retrofit 第二梯队
const val CLASS_NAME_RETROFIT_SECOND_ECHELON_INDEX = 2
val CLASS_NAME_RETROFIT_SECOND_ECHELON = arrayListOf(
    Path::class.asClassName()
)

// retrofit 第三梯队
const val CLASS_NAME_RETROFIT_THIRD_ECHELON_INDEX = 3
val CLASS_NAME_RETROFIT_THIRD_ECHELON = arrayListOf(
    Url::class.asClassName(),
)

// retrofit 第四梯队
const val CLASS_NAME_RETROFIT_FOURTH_ECHELON_INDEX = 4
val CLASS_NAME_RETROFIT_FOURTH_ECHELON = arrayListOf(
    Query::class.asClassName(),
    QueryName::class.asClassName(),
    QueryMap::class.asClassName(),
)

// retrofit 第五梯队
const val CLASS_NAME_RETROFIT_FIFTH_ECHELON_INDEX = 5
val CLASS_NAME_RETROFIT_FIFTH_ECHELON = arrayListOf(
    Field::class.asClassName(),
    FieldMap::class.asClassName(),

    Part::class.asClassName(),
    PartMap::class.asClassName(),

    Body::class.asClassName(),
)

// retrofit 第六梯队
const val CLASS_NAME_RETROFIT_SIXTH_ECHELON_INDEX = 6
val CLASS_NAME_RETROFIT_SIXTH_ECHELON = arrayListOf(
    JsonKey::class.asClassName(),
)

////////////////////////////////////////////

val CLASS_NAME_RETROFIT_MANAGER = ClassName(CORE_PACKAGE_NAME, "Watermelon")

val STRING_NULLABLE = STRING.copy(nullable = true)

val ANY_NULLABLE = ANY.copy(nullable = true)

val MUTABLE_MAP_STRING_PARAMETER_VALUE_CONVERTER =
    MUTABLE_MAP.parameterizedBy(STRING, ParameterValueConverter::class.asTypeName())

val MUTABLE_MAP_STRING_APPEND_PROVIDER =
    MUTABLE_MAP.parameterizedBy(STRING, AppendProvider::class.asTypeName())

val NULL_POINTER_EXCEPTION = NullPointerException::class.asClassName()

// 锚点
val ANCHOR = OnAdaptiveRetrofit::class.asClassName()

val CLASS_NAME_EMPTY_DYNAMIC_URL = EmptyDynamicUrlProvider::class.asClassName()