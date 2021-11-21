package bsu.pischule.csab.imdb.data.service;

import bsu.pischule.csab.imdb.data.entity.Film;
import bsu.pischule.csab.imdb.data.repository.FilmRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
@AllArgsConstructor
public class FilmService extends CrudService<Film, Integer> {
    private final FilmRepository repository;

    @Override
    protected FilmRepository getRepository() {
        return repository;
    }

}
