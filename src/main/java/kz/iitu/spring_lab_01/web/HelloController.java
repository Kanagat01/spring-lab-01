package kz.iitu.spring_lab_01.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class HelloController {
    @Value("${app.owner:unknown}")
    private String owner;

    @GetMapping("/hello")
    public Greeting hello(@RequestParam(defaultValue = "world") String name) {
        return new Greeting("Hello, " + name + "!", owner, LocalDateTime.now());
    }

    @GetMapping("/info")
    public Info info() {
        return new Info(owner,
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors());
    }

    public record Greeting(String message, String owner, LocalDateTime timestamp) {
    }

    public record Info(String owner, String javaVersion, int cpuCores) {
    }

    @GetMapping("/fibonacci")
    public FibonacciResult fibonacci(@RequestParam(defaultValue = "10") int n) {
        if (n < 1 || n > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "n must be between 1 and 50");
        }
        List<Long> numbers = new ArrayList<>();
        long a = 0, b = 1;
        for (int i = 0; i < n; i++) {
            numbers.add(a);
            long next = a + b;
            a = b;
            b = next;
        }
        return new FibonacciResult(n, numbers);
    }

    public record FibonacciResult(int n, List<Long> numbers) {
    }
}