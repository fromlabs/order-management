package org.fromlabs.demo.ordermanagement.customer

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.fromlabs.demo.ordermanagement.customer.adapter.CustomerEntity
import org.fromlabs.demo.ordermanagement.customer.adapter.CustomerRepository
import org.fromlabs.demo.ordermanagement.customer.adapter.MoneyEmbeddable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import javax.inject.Inject
import javax.persistence.EntityManager

@MicronautTest
class CustomerRepositoryTest {

    @Inject
    lateinit var entityManager: EntityManager

    @Inject
    lateinit var customerRepository: CustomerRepository

    @Test
    fun testItWorks() {
        Assertions.assertNotNull(customerRepository)

        val customerEntity = CustomerEntity(
            name = "Roberto Tassi",
            creationTime = Instant.now(),
            creditLimit = MoneyEmbeddable(
                amount = 1000.toBigDecimal()
            )
        )

        println(customerEntity)

        val savedCustomerEntity = customerRepository.save(
            customerEntity
        )

        println(savedCustomerEntity)

        entityManager.flush()
        entityManager.clear()

        val foundCustomerEntity = customerRepository.findById(
            savedCustomerEntity.id
        ).get()

        println(foundCustomerEntity)

        val customerEntity2 = foundCustomerEntity.copy(creditLimit = MoneyEmbeddable(amount = 2000.toBigDecimal()))

        val savedCustomerEntity2 = customerRepository.update(
            customerEntity2
        )

        println(savedCustomerEntity2)

        entityManager.flush()
        entityManager.clear()

        val foundCustomerEntity2 = customerRepository.findById(
            savedCustomerEntity.id
        ).get()

        println(foundCustomerEntity2)

        val customerEntity3 =
            foundCustomerEntity2.copy(
                creditReservations = mapOf(
                    10L to MoneyEmbeddable(100.toBigDecimal()),
                    20L to MoneyEmbeddable(200.toBigDecimal())
                )
            )

        val savedCustomerEntity3 = customerRepository.update(
            customerEntity3
        )

        println(savedCustomerEntity3)

        entityManager.flush()
        entityManager.clear()

        val foundCustomerEntity3 = customerRepository.findById(
            savedCustomerEntity.id
        ).get()

        println(foundCustomerEntity3)

        val customerEntity4 =
            foundCustomerEntity3.copy(
                creditReservations = foundCustomerEntity3.creditReservations.plus(30L to MoneyEmbeddable(300.toBigDecimal()))
            )

        val savedCustomerEntity4 = customerRepository.update(
            customerEntity4
        )

        println(savedCustomerEntity4)

        entityManager.flush()
        entityManager.clear()
    }
}