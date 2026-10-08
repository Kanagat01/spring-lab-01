package kz.iitu.spring_lab_01.web;

import kz.iitu.spring_lab_01.lifecycle.LifecycleDemo;
import kz.iitu.spring_lab_01.notify.NotificationService;
import kz.iitu.spring_lab_01.notify.Notifier;
import kz.iitu.spring_lab_01.scope.TicketOffice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lab2")
public class Lab2Controller {

    private final NotificationService notifications;
    private final LifecycleDemo lifecycle;
    private final TicketOffice office;
    private final Notifier base64;

    public Lab2Controller(NotificationService notifications,
            LifecycleDemo lifecycle,
            TicketOffice office,
            @Qualifier("base64") Notifier base64) {
        this.notifications = notifications;
        this.lifecycle = lifecycle;
        this.office = office;
        this.base64 = base64;
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

    @GetMapping("/custom")
    public Map<String, String> custom(@RequestParam(defaultValue = "Hello") String text) {
        return Map.of("input", text, "result", base64.send(text));
    }
}