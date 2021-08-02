package com.mskj.mercer.app.model

import com.google.gson.annotations.SerializedName

/**
 * @author      ：mercer
 * @date        ：2021-08-09  21:41
 * @description ：
 */
data class Repo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("stargazers_count") val starCount: Int
)