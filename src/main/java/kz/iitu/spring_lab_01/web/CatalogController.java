package kz.iitu.spring_lab_01.web;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.support.AopUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kz.iitu.spring_lab_01.service.CatalogService;

@RestController
@RequestMapping("/api/lab4")
public class CatalogController {

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping("/item/{id}")
    public String item(@PathVariable long id) {
        return service.findById(id);
    }

    @GetMapping("/items")
    public List<String> items(@RequestParam(defaultValue = "5") int limit) {
        return service.findAll(limit);
    }

    @DeleteMapping("/item/{id}")
    public String remove(@PathVariable long id) {
        return service.remove(id);
    }

    @GetMapping("/proxy")
    public Map<String, Object> proxy() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("className", service.getClass().getName());
        m.put("superClass", service.getClass().getSuperclass().getName());
        m.put("isAopProxy", AopUtils.isAopProxy(service));
        m.put("isCglib", AopUtils.isCglibProxy(service));
        return m;
    }

    @GetMapping("/remove-twice/{id}")
    public String removeTwice(@PathVariable long id) {
        return service.removeTwice(id);
    }
}