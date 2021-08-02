package com.mskj.mercer.app.model

import com.google.gson.annotations.SerializedName

/**
 * @author      ：mercer
 * @date        ：2021-08-09  21:42
 * @description ：
 */
class RepoResponse(
    @SerializedName("items") val items: List<Repo> = emptyList()
)