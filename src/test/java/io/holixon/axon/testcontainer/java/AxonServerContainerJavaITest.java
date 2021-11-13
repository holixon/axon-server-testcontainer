package io.holixon.axon.testcontainer.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import io.holixon.axon.testcontainer.AxonServerContainer;
import io.holixon.axon.testcontainer.spring.AxonServerContainerSpring;
import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = JavaTestApplication.class, webEnvironment = NONE)
@Testcontainers
class AxonServerContainerJavaITest {

  @Container
  public static final AxonServerContainer AXON = AxonServerContainer.builder()
    .enableDevMode()
    .build();

  @DynamicPropertySource
  public static void axonProperties(final DynamicPropertyRegistry registry) {
    AxonServerContainerSpring.addDynamicProperties(AXON, registry);
  }

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private QueryGateway queryGateway;

  @Test
  void start_axonServer_run_command_and_query() {
    String accountId = UUID.randomUUID().toString();

    commandGateway.sendAndWait(new JavaTestApplication.CreateBankAccountCommand(accountId, 100));

    JavaTestApplication.BankAccountDto account = queryGateway.query(new JavaTestApplication.FindBankAccountById(accountId), ResponseTypes.optionalInstanceOf(JavaTestApplication.BankAccountDto.class))
      .join().orElseThrow();

    assertThat(account.getAccountId()).isEqualTo(accountId);
    assertThat(account.getBalance()).isEqualTo(100);
  }

  @AfterAll
  static void afterAll() {
    AXON.stop();
  }
}
