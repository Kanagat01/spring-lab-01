package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.lifecycle.LifecycleDemo;
import kz.iitu.spring_lab_01.notify.NotificationService;
import kz.iitu.spring_lab_01.scope.TicketOffice;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lab2")
public class Lab2Controller {

    private final NotificationService notifications;
    private final LifecycleDemo lifecycle;
    private final TicketOffice office;

    public Lab2Controller(NotificationService notifications,
            LifecycleDemo lifecycle,
            TicketOffice office) {
        this.notifications = notifications;
        this.lifecycle = lifecycle;
        this.office = office;
    }

    @GetMapping("/notify")
    public Map<String, Object> notify(@RequestParam(defaultValue = "Hello") String text) {
        return Map.of("primary", notifications.viaPrimary(text),
                "console", notifications.viaConsole(text),
                "all", notifications.viaAll(text),
                "beanNames", notifications.names());
    }

    @GetMapping("/lifecycle")
    public List<String> lifecycle() {
        return lifecycle.events();
    }

    @GetMapping("/scopes")
    public Map<String, Object> scopes() {
        return office.demo();
    }
}