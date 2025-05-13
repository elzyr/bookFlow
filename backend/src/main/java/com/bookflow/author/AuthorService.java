package com.bookflow.author;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final AuthorRepository authorRepository;

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author addAuthor(Author author) {
        if (authorRepository.existsByNameIgnoreCase(author.getName())) {
            throw new IllegalArgumentException("Taki autor już istnieje");
        }
        return authorRepository.save(author);
    }

    public Optional<Author> getById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public List<Author> validateAuthors(List<String> authorNames) {
        List<Author> result = new ArrayList<>();

        for (String rawName : authorNames) {
            String name = rawName.trim();

            Author author = authorRepository
                    .findByName(name)
                    .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono autora o nazwie: " + name));

            result.add(author);
        }

        return result;
    }
}
