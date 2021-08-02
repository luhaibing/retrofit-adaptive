package com.mercer.adaptive.core.emum

import com.mercer.adaptive.annotate.JsonKey
import retrofit2.http.*

enum class AppendKey(val value: String) {

    HEADER(Header::class.java.canonicalName),

    QUERY(Query::class.java.canonicalName),

    FIELD(Field::class.java.canonicalName),

    PART(Part::class.java.canonicalName),

    JSON_KEY(JsonKey::class.java.canonicalName);

}