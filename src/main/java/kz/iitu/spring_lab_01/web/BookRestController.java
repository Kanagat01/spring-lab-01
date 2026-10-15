package kz.iitu.spring_lab_01.web;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kz.iitu.spring_lab_01.catalog.Book;
import kz.iitu.spring_lab_01.catalog.BookService;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private static final Map<String, String[]> NAMES = Map.of(
            "en", new String[] { "id", "title", "author", "year" },
            "ru", new String[] { "id", "название", "автор", "год" },
            "kk", new String[] { "id", "атауы", "автор", "жылы" });

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    // variant 8: the field names depend on the Accept-Language header
    @GetMapping
    public List<Map<String, Object>> list(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String lang) {

        return service.findAll(author).stream()
                .limit(limit)
                .map(b -> localize(b, lang))
                .toList();
    }

    private Map<String, Object> localize(Book b, String lang) {
        String code = lang.trim().toLowerCase();
        code = code.substring(0, Math.min(2, code.length()));
        String[] n = NAMES.getOrDefault(code, NAMES.get("en"));

        Map<String, Object> m = new LinkedHashMap<>();
        m.put(n[0], b.id());
        m.put(n[1], b.title());
        m.put(n[2], b.author());
        m.put(n[3], b.year());
        return m;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> find(@PathVariable long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        Book saved = service.create(book);
        return ResponseEntity
                .created(URI.create("/api/books/" + saved.id()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(@PathVariable long id, @RequestBody Book book) {
        return service.update(id, book)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}