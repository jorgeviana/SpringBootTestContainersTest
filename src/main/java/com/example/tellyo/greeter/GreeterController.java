package com.example.tellyo.greeter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;
import java.util.List;

import static java.lang.String.format;

@RestController
@RequestMapping("/greet")
class GreeterController {

    private static final String GOOD_MORNING = "Hello %s, good morning";
    private static final String GOOD_AFTERNOON = "Hello %s, good afternoon!";

    @Autowired
    UserRepository userRepository;

    @GetMapping
    Object greet(@RequestParam(value = "name", defaultValue = "Stranger") String name) {

        if (userSeen(name)) {
            return new Greet(format("I think I saw you before, %s!", name));
        }

        registerUserSeen(name);

        if (LocalTime.now().getHour() < 12) {
            return new Greet(format(GOOD_MORNING, name));
        }
        return new Greet(format(GOOD_AFTERNOON, name));
    }

    private boolean userSeen(String name) {
        return !userRepository.findByName(name).isEmpty();
    }

    private void registerUserSeen(String name) {
        userRepository.save(new User(name));
    }
}

interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByName(String name);
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
class User {

    User(String name) {
        this.name = name;
    }

    @Id @GeneratedValue
    private int id;
    private String name;
}

@Value
class Greet {
    private String greet;
}
