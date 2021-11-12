package io.holixon.axon.testcontainer.kotlin

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.queryhandling.QueryHandler
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.stereotype.Component
import java.util.*
import org.axonframework.modelling.command.AggregateLifecycle.apply as publishEvent

typealias BankAccountId = String

@SpringBootApplication
class KotlinTestApplication {

  data class BankAccountDto(val accountId: BankAccountId, val balance: Int)
  data class CreateBankAccountCommand(@TargetAggregateIdentifier val accountId: BankAccountId, val initialBalance: Int = 0)
  data class BankAccountCreatedEvent(val accountId: BankAccountId, val balance: Int)
  data class FindBankAccountById(val accountId: BankAccountId)

  @Aggregate
  class BankAccountAggregate {
    companion object {

      @JvmStatic
      @CommandHandler
      fun create(cmd: CreateBankAccountCommand): BankAccountAggregate {
        publishEvent(BankAccountCreatedEvent(cmd.accountId, cmd.initialBalance))
        return BankAccountAggregate()
      }
    }

    @AggregateIdentifier
    private lateinit var accountId: BankAccountId
    private var balance: Int = 0

    @EventSourcingHandler
    fun on(evt: BankAccountCreatedEvent) {
      accountId = evt.accountId
      balance = evt.balance
    }
  }

  @Component
  class BankAccountProjection() {
    private val store = mutableMapOf<BankAccountId, BankAccountDto>()

    @EventHandler
    fun on(evt: BankAccountCreatedEvent) {
      store.put(evt.accountId, BankAccountDto(evt.accountId, evt.balance))
    }

    @QueryHandler
    fun findById(query: FindBankAccountById) = Optional.ofNullable(store[query.accountId])
  }
}
