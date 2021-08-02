package com.mercer.adaptive.annotate

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Appends(vararg val values: Append)

/*
@Repeatable
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Annotation(val role: String)
annotation class Annotations1(vararg val values: Annotation)
annotation class Annotations2(val values: Array<Annotation>)

@Annotations1(
    Annotation("111"),
    Annotation("222"),
)
class Annotations1Demo {
    @Annotations1(
        *arrayOf(
            Annotation("333"),
            Annotation("444"),
        )
    )
    fun test1() {
    }
}


@Annotations2(
    arrayOf(
        Annotation("333"),
        Annotation("444")
    )
)
class Annotations2Demo {

    @Annotations2(
        [
            Annotation("111"),
            Annotation("222"),
        ]
    )
    fun test2() {
    }
}
*/