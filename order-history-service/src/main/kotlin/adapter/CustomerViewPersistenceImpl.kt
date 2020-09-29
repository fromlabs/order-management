package org.fromlabs.demo.ordermanagement.orderhistory.adapter

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import org.bson.Document
import org.bson.types.Decimal128
import org.fromlabs.demo.ordermanagement.orderhistory.application.port.CustomerViewPersistence
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.CustomerView
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.Money
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.OrderInfo
import org.fromlabs.demo.ordermanagement.orderhistory.domain.model.OrderState
import javax.inject.Singleton

@Singleton
class CustomerViewPersistenceImpl(private val mongoClient: MongoClient) : CustomerViewPersistence {
    override fun findById(customerId: Long): CustomerView? {
        return customerViews()
            .find(Filters.eq("customerId", customerId))
            .map(Document::toCustomerView)
            .firstOrNull()
    }

    override fun saveCustomer(customerId: Long, name: String, creditLimit: Money): CustomerView {
        val saved = customerViews().findOneAndUpdate(
            Filters.eq("customerId", customerId),
            Updates.combine(
                Updates.setOnInsert("customerId", customerId),
                Updates.set("name", name),
                Updates.set("creditLimit", creditLimit.amount)
            ),
            FindOneAndUpdateOptions()
                .upsert(true)
                .returnDocument(ReturnDocument.AFTER)
        )

        return saved!!.toCustomerView()
    }

    override fun addOrder(customerId: Long, orderInfo: OrderInfo): CustomerView {
        val saved = customerViews().findOneAndUpdate(
            Filters.eq("customerId", customerId),
            Updates.combine(
                Updates.setOnInsert("customerId", customerId),
                Updates.set(
                    "orders.${orderInfo.orderId}",
                    Document("orderId", orderInfo.orderId)
                        .append("state", orderInfo.state.name)
                        .append("orderTotal", orderInfo.orderTotal.amount)
                )
            ),
            FindOneAndUpdateOptions()
                .upsert(true)
                .returnDocument(ReturnDocument.AFTER)
        )

        return saved!!.toCustomerView()
    }

    override fun updateOrder(customerId: Long, orderInfo: OrderInfo): CustomerView {
        val saved = customerViews().findOneAndUpdate(
            Filters.eq("customerId", customerId),
            Updates.combine(
                Updates.set(
                    "orders.${orderInfo.orderId}",
                    Document("orderId", orderInfo.orderId)
                        .append("state", orderInfo.state.name)
                        .append("orderTotal", orderInfo.orderTotal.amount)
                )
            ),
            FindOneAndUpdateOptions()
                .returnDocument(ReturnDocument.AFTER)
        )

        return saved!!.toCustomerView()
    }

    private fun customerViews(): MongoCollection<Document> {
        return mongoClient.getDatabase("order-history").getCollection("customer-views")
    }
}

private fun Document.toCustomerView(): CustomerView =
    CustomerView(
        customerId = getLong("customerId"),
        name = getString("name"),
        creditLimit = Money(get("creditLimit", Decimal128::class.java).bigDecimalValue()),
        orders = get("orders", Document::class.java)?.let { ordersDocument ->
            ordersDocument.keys
                .map { orderKey -> ordersDocument.get(orderKey, Document::class.java) }
                .map { orderDocument ->
                    OrderInfo(
                        orderId = orderDocument.getLong("orderId"),
                        state = OrderState.valueOf(orderDocument.getString("state")),
                        orderTotal = Money(orderDocument.get("orderTotal", Decimal128::class.java).bigDecimalValue())
                    )
                }
                .associateBy(OrderInfo::orderId)
        } ?: mapOf()
    )