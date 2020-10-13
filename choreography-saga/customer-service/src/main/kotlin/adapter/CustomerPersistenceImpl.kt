package org.fromlabs.demo.ordermanagement.customer.adapter

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import org.fromlabs.demo.ordermanagement.core.jpa.JpaEntity
import org.fromlabs.demo.ordermanagement.customer.application.port.CustomerPersistence
import org.fromlabs.demo.ordermanagement.customer.domain.model.Customer
import org.fromlabs.demo.ordermanagement.customer.domain.model.Money
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Singleton
import javax.persistence.*

@Embeddable
data class MoneyEmbeddable(val amount: BigDecimal)

@Entity
@Table(name = "customers")
data class CustomerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,
    @Version
    val version: Int = 0,
    val name: String,
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "amount", column = Column(name = "creditlimit"))
    )
    val creditLimit: MoneyEmbeddable,
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "orderid")
    @CollectionTable(
        name = "customers_credit_reservations",
        joinColumns = [JoinColumn(name = "customerid")]
    )
    val creditReservations: Map<Long, MoneyEmbeddable> = mapOf(),
    val creationTime: Instant

) : JpaEntity<Long>()

@Repository
interface CustomerRepository : CrudRepository<CustomerEntity, Long> {
    //
}

@Singleton
class CustomerPersistenceImpl(private val customerRepository: CustomerRepository) : CustomerPersistence {
    override fun findById(id: Long): Customer? {
        return customerRepository
            .findById(id)
            .orElse(null)?.toCustomer()
    }

    override fun create(customer: Customer): Customer {
        return customerRepository.save(customer.toCustomerEntity()).toCustomer()
    }

    override fun update(customer: Customer): Customer {
        return customerRepository.update(customer.toCustomerEntity()).toCustomer()
    }
}

private fun CustomerEntity.toCustomer(): Customer =
    Customer(
        id = id,
        version = version,
        name = name,
        creditLimit = creditLimit.toMoney(),
        creditReservations = creditReservations.entries.associateBy(
            keySelector = { it.key },
            valueTransform = { it.value.toMoney() }
        ),
        creationTime = creationTime
    )

private fun MoneyEmbeddable.toMoney(): Money =
    Money(amount = amount)

private fun Customer.toCustomerEntity(): CustomerEntity =
    CustomerEntity(
        id = id,
        version = version,
        name = name,
        creditLimit = creditLimit.toMoneyEmbeddable(),
        creditReservations = creditReservations.entries.associateBy(
            keySelector = { it.key },
            valueTransform = { it.value.toMoneyEmbeddable() }
        ),
        creationTime = creationTime
    )

private fun Money.toMoneyEmbeddable(): MoneyEmbeddable =
    MoneyEmbeddable(amount = amount)
