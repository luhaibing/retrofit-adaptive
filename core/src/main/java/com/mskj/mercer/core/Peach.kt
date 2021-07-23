package com.mskj.mercer.core

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
class Peach private constructor() {

    /**
     * 第一阶段
     * 暂时由反射去获取
     * 也可直接在编译后去调用生成的类
     */
    companion object {

        const val IMPL_SUFFIX = "Impl"

        private fun <T> get(name: String, url: String?): T {
            val zlass: Class<*> = Class.forName(name + IMPL_SUFFIX)
            return if (url == null) {
                zlass.getDeclaredMethod("invoke").invoke(zlass)
            } else {
                zlass.getDeclaredMethod("invoke", String::class.java).invoke(zlass, url)
            } as T
        }

        @JvmStatic
        fun <T> get(zlass: Class<T>): T {
            return get(zlass.canonicalName, null)
        }

        inline fun <reified T> get(): T {
            return get(T::class.java)
        }

        @JvmStatic
        fun <T> get(zlass: Class<T>, url: String): T {
            return get(zlass.canonicalName, url)
        }

        inline fun <reified T> get(url: String): T {
            return get(T::class.java, url)
        }

    }

}