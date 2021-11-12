package io.holixon.axon.testcontainer.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class JavaTestApplication {


  public static class BankAccountDto {

    private final String accountId;
    private final int balance;

    public BankAccountDto(String accountId, int balance) {
      this.accountId = accountId;
      this.balance = balance;
    }

    public String getAccountId() {
      return accountId;
    }

    public int getBalance() {
      return balance;
    }
  }

  public static class CreateBankAccountCommand {

    @TargetAggregateIdentifier
    private final String accountId;
    private final int initialBalance;

    public CreateBankAccountCommand(String accountId, int initialBalance) {
      this.accountId = accountId;
      this.initialBalance = initialBalance;
    }

    public String getAccountId() {
      return accountId;
    }

    public int getInitialBalance() {
      return initialBalance;
    }
  }

  public static class BankAccountCreatedEvent {

    private final String accountId;
    private final int balance;

    public BankAccountCreatedEvent(String accountId, int balance) {
      this.accountId = accountId;
      this.balance = balance;
    }

    public String getAccountId() {
      return accountId;
    }

    public int getBalance() {
      return balance;
    }
  }

  public static class FindBankAccountById {

    private final String accountId;

    public FindBankAccountById(String accountId) {
      this.accountId = accountId;
    }

    public String getAccountId() {
      return accountId;
    }
  }

  @Aggregate
  public static class BankAccountAggregate {

    @CommandHandler
    public static BankAccountAggregate create(CreateBankAccountCommand cmd) {
      AggregateLifecycle.apply(new BankAccountCreatedEvent(cmd.accountId, cmd.initialBalance));
      return new BankAccountAggregate();
    }

    @AggregateIdentifier
    private String accountId;
    private int balance;

    public BankAccountAggregate() {
      // empty, just to please axon
    }

    @EventSourcingHandler
    public void on(BankAccountCreatedEvent evt) {
      accountId = evt.accountId;
      balance = evt.balance;
    }
  }

  @Component
  public static class BankAccountProjection {

    private final Map<String, BankAccountDto> store = new HashMap<>();


    @EventHandler
    public void on(BankAccountCreatedEvent evt) {
      store.put(evt.accountId, new BankAccountDto(evt.accountId, evt.balance));
    }

    @QueryHandler
    public Optional<BankAccountDto> findById(FindBankAccountById query) {
      return Optional.ofNullable(store.get(query.accountId));
    }

  }
}
