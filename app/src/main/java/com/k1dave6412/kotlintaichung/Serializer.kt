package com.k1dave6412.kotlintaichung

import kotlinx.serialization.Serializable


@Serializable
data class PageInfo(
    /*
    "page": {
        "has_next": true,
        "has_prev": false,
        "page": 1,
        "pages": 3,
        "total": 65
    }
     */
    val has_next: Boolean,
    val has_prev: Boolean,
    val page: Int,
    val pages: Int,
    val total: Int
)

@Serializable
data class Order(
    /*
    {
      "address_id": "5946cd5b-29eb-4a33-a8a9-d57c135011ab",
      "id": "494365fd-625c-4a85-979c-2d972011d68d",
      "order_completed_at": "2017-03-01 17:45:00",
      "order_created_at": "2017-02-23 01:59:00",
      "order_deadline": "2017-02-28 02:00:00",
      "order_id": "17022301597NM8J",
      "order_shipped_at": "2017-02-23 12:06:00",
      "package_no": "07905406481",
      "receiver_id": "99654f93-8485-436c-a2d6-72e3b2d3b073",
      "refund_status": null,
      "remark": null,
      "shipping_method": "寄件",
      "status": "完成",
      "stocking_time": ""
    }
     */
    val id: String,
    val address_id: String,
    val receiver_id: String,
    val order_id: String,
    val order_created_at: String,
    val shipping_method: String,
    val stocking_time: String,
    val order_completed_at: String? = null,
    val order_deadline: String? = null,
    val order_shipped_at: String? = null,
    val package_no: String? = null,
    val refund_status: String? = null,
    val remark: String? = null,
    val status: String
)


@Serializable
data class Receiver(
    /*
    {
        "customer_id": "6fb20455-57d5-4c17-b93a-b50b6379fa8c",
        "id": "0e600638-f052-4e58-be03-c394254dcb29",
        "name": "譚*立",
        "phone": "886912***654"
    }
     */
    val id: String,
    val customer_id: String,
    val name: String,
    val phone: String
)

@Serializable
data class Orders(
    val data: MutableList<Order>,
    val page: PageInfo
)