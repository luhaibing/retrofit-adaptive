package com.mskj.mercer.app.network

import android.service.autofill.UserData
import com.google.gson.internal.LinkedTreeMap
import com.mercer.adaptive.annotate.Adaptive
import com.mercer.adaptive.annotate.JsonContent
import com.mercer.adaptive.annotate.JsonKey
import com.mskj.mercer.app.model.NetResponse
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import retrofit2.http.*

@Adaptive()
interface TestApi {

    @DELETE("/orders/food/operation/order/takeaway/finish")
    fun test1(
        @Query(value = "id", encoded = true) id: Long
    ): Flow<NetResponse<Any>>

    @POST("/orders/food/operation/order/takeaway/finish")
    @JsonContent("/orders/food/operation/order/takeaway/finish")
    suspend fun test2(
        @Query("id") id: Long?
    ): NetResponse<Any>

    /**
     * 店铺人员查看某台桌当天的预约/临近是只显示当前半小时内的预约
     * @param subSeatId 桌子id
     */
    // https://swagger.ihk.ltd/food/doc.html#/%E9%A4%90%E9%A5%AE%EF%BC%9A%E5%95%86%E5%AE%B6%E7%AB%AF/%E9%A4%90%E6%A1%8C%E9%A2%84%E7%BA%A6%E8%A1%A8api/seatSubForDayUsingGET_1
    @GET("/food/merchant/subscribe/seat_sub_day/{id}")
    fun test3(
        @Query("subSeatId") subSeatId: Long,
        @Path("id") id: Long
    ): Deferred<NetResponse<List<String>>>

    /**
     * 商家预约餐桌
     */
    // https://swagger.ihk.ltd/food/doc.html#/%E9%A4%90%E9%A5%AE%EF%BC%9A%E5%95%86%E5%AE%B6%E7%AB%AF/%E9%A4%90%E6%A1%8C%E9%A2%84%E7%BA%A6%E8%A1%A8api/subUsingPOST_1
    @POST("/food/merchant/subscribe/sub_seat/{id}")
    @JsonContent
    fun test4(
        @Path("id") id: Long,
        @Query("subSeatId") subSeatId: Long,

        @JsonKey("subSeatName") subSeatName: String,

        @JsonKey("subName") subName: String,
        @JsonKey("subPhone") subPhone: String,
        @JsonKey("subPersons") subPersons: Int,

        @JsonKey("subStartTime") subStartTime: Long,
        @JsonKey("subEndTime") subEndTime: Long,
    ): Deferred<NetResponse<Any>>

    @POST("/order/app/order/start")
    @JsonContent
    fun test5(
        @JsonKey("subSeatId") subSeatId: Long,
    ): Deferred<NetResponse<Any>>

    @PUT("/food/merchant/takeout/submit_order")
    @FormUrlEncoded
    fun test6(
        @Query("businessId") businessId: Long,
        // 0:堂食,1:外带，2.加菜
        @Field("orderType") orderType: Int,
        @Field("remark") remark: String,
        @Field("serviceAmount") serviceAmount: Double,
        @Field("tablewareCount") tablewareCount: Int,
        @Field("totalAmount") totalAmount: Double,
        @Field("goodsCount") goodsCount: Int,
    ): Deferred<NetResponse<Any>>

    @POST("/food/merchant/takeout/finish_order")
    @Multipart
    fun test7(
        @Query("businessId") businessId: Long,
        // 0:堂食,1:外带，2.加菜
        @Part("orderType") orderType: Int,
        @Part("remark") remark: String,
        @Part("serviceAmount") serviceAmount: Double,
        @Part("tablewareCount") tablewareCount: Int,
        @Part("totalAmount") totalAmount: Double,
        @Part("goodsCount") goodsCount: Int,
    ): Deferred<NetResponse<Any>>

    @DELETE("/orders/food/operation/order/takeaway/finish")
    fun test8(
        @Query("id") id: Long
    ): Deferred<NetResponse<Any>>

    /////////////////////

    @POST("/food/merchant/takeout/finish_order/{id}")
    @Multipart
    fun test9(
        id: Long,
        @Query("businessId") businessId: Long,
        // 0:堂食,1:外带，2.加菜
        orderType: Int,
        remark: String,
        serviceAmount: Double,
        tablewareCount: Int,
        totalAmount: Double,
        goodsCount: Int,
        map1: HashMap<String, String>,
        map2: LinkedTreeMap<String, String>,
        map3: Map<String, String>,
        list1: List<String>,
        collection: AbstractCollection<String>,
        set: HashSet<String>,

        ): Deferred<NetResponse<Any>>

    @POST("/food/merchant/takeout/finish_order/{id}")
    @FormUrlEncoded
    fun test10(
        @Query("businessId") businessId: Long,
        id: Long,
        // 0:堂食,1:外带，2.加菜
        orderType: Int,
        remark: String,
        serviceAmount: Double,
        tablewareCount: Int,
        totalAmount: Double,
        goodsCount: Int,
        user: UserData,
        map1: HashMap<String, String>,
        map2: LinkedTreeMap<String, String>,
        map3: Map<String, String>,
        list1: List<String>,
        collection: AbstractCollection<String>,
        set: HashSet<String>,
    ): Deferred<NetResponse<Any>>

    @POST("/food/merchant/takeout/finish_order/{id}")
    @JsonContent
    fun test11(
        @Query("businessId") businessId: Long,
        // 0:堂食,1:外带，2.加菜
        orderType: Int,
        remark: String,
        serviceAmount: Double,
        tablewareCount: Int,
        totalAmount: Double,
        goodsCount: Int,
        id: Long
    ): Deferred<NetResponse<Any>>

    @DELETE("/orders/food/operation/order/takeaway/finish/{id}")
    fun test12(
        id: Long
    ): Flow<NetResponse<Any>>

    @DELETE("/orders/food/operation/order/takeaway/finish")
    @JsonContent
    suspend fun test13(
        @Header("Accept-Language") lang: String,
        @Url url: Long,
        map1: HashMap<String, String>,
    ): NetResponse<Any>

    @POST("/orders/food/operation/order/takeaway/finish/id")
    @JsonContent
    fun test13(
        id: Long
    ): Flow<NetResponse<Any>>

    @POST("/orders/food/operation/order/takeaway/finish/id")
    @JsonContent
    fun test14(
        id: String
    ): Flow<NetResponse<Any>>

}