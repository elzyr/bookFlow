package com.bookflow.author;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorService authorService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Author> addAuthor(@RequestBody Author author) {
        Author created = authorService.addAuthor(author);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/authors/{id}")
                .buildAndExpand(created.getAuthor_id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthorById(@PathVariable long id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

}
