package bsu.pischule.csab.imdb.data.service;

import bsu.pischule.csab.imdb.data.entity.Name;
import bsu.pischule.csab.imdb.data.repository.NameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
@AllArgsConstructor
public class NameService extends CrudService<Name, Integer> {
    private final NameRepository repository;

    @Override
    protected NameRepository getRepository() {
        return repository;
    }

}
