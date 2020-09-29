package org.fromlabs.demo.ordermanagement.core.jpa

abstract class JpaEntity<T> {
    abstract val id: T

    final override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (javaClass != other::class.java) return false
        other as JpaEntity<*>
        return this.id != null && this.id == other.id
    }

    final override fun hashCode(): Int = 31

    final override fun toString() =
        "Entity of type ${this.javaClass.simpleName} [id: $id, hashcode: ${System.identityHashCode(this)}]"
}