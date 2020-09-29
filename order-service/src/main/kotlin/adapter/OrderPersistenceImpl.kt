package org.fromlabs.demo.ordermanagement.order.adapter

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.fromlabs.demo.ordermanagement.core.jpa.JpaEntity
import org.fromlabs.demo.ordermanagement.order.application.port.OrderPersistence
import org.fromlabs.demo.ordermanagement.order.domain.model.Money
import org.fromlabs.demo.ordermanagement.order.domain.model.Order
import org.fromlabs.demo.ordermanagement.order.domain.model.OrderDetails
import org.fromlabs.demo.ordermanagement.order.domain.model.OrderState
import java.math.BigDecimal
import javax.inject.Singleton
import javax.persistence.*

@Embeddable
data class MoneyEmbeddable(val amount: BigDecimal)

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    @Version
    val version: Int = 0,
    val customerId: Long,
    @Enumerated(EnumType.STRING)
    val orderState: OrderState,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "ordertotal"))
    )
    val orderTotal: MoneyEmbeddable

) : JpaEntity<Long>()

@Repository
interface OrderRepository : CrudRepository<OrderEntity, Long> {
    //
}

@Singleton
class OrderPersistenceImpl(private val orderRepository: OrderRepository) : OrderPersistence {
    override fun findById(id: Long): Order? {
        return orderRepository
            .findById(id)
            .orElse(null)?.toOrder()
    }

    override fun create(order: Order): Order {
        return orderRepository.save(order.toOrderEntity()).toOrder()
    }

    override fun update(order: Order): Order {
        return orderRepository.update(order.toOrderEntity()).toOrder()
    }
}

private fun OrderEntity.toOrder(): Order =
    Order(
        id = id,
        version = version,
        orderState = orderState,
        orderDetails = OrderDetails(
            customerId = customerId,
            orderTotal = orderTotal.toMoney()
        )
    )

private fun MoneyEmbeddable.toMoney(): Money =
    Money(amount = amount)

private fun Order.toOrderEntity(): OrderEntity =
    OrderEntity(
        id = id,
        version = version,
        orderState = orderState,
        customerId = orderDetails.customerId,
        orderTotal = orderDetails.orderTotal.toMoneyEmbeddable()
    )

private fun Money.toMoneyEmbeddable(): MoneyEmbeddable =
    MoneyEmbeddable(amount = amount)
