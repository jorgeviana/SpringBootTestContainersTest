package com.example.tellyo.greeter;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MySQLContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
//@DataJpaTest // see how to configure these 2 annotations with testcontainers
//@AutoConfigureTestDatabase
@ContextConfiguration(initializers = {DataTest.MyInit.class})
public class DataTest {

    @ClassRule
    public static MySQLContainer mySQLContainer = new MySQLContainer()
            .withDatabaseName("RANDOM_TEST_DB")
            .withUsername("random_user")
            .withPassword("random_password");

    static class MyInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mySQLContainer.getUsername(),
                    "spring.datasource.password=" + mySQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }


    @Autowired
    UserRepository userRepository;

    @Test
    public void dataTest() {
        User anacleto = new User("Anacleto");
        userRepository.save(anacleto);

        List<User> users = userRepository.findAll();

        assertThat(users).hasSize(1);
        assertThat(users).contains(anacleto);
    }
}
