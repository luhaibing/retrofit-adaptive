package com.mskj.mercer.processor.model

/**
 * 来源
 */
sealed interface Source

sealed interface AnnotationSource : Source

sealed interface ParameterSource : Source

/**
 * 正常
 */
object NORMAL : AnnotationSource, ParameterSource

/**
 * 补充
 */
object SUPPLEMENT : AnnotationSource

/**
 * 注入
 */
object APPEND : ParameterSource, AnnotationSource