package bsu.pischule.csab.imdb.data.service;

import bsu.pischule.csab.imdb.data.entity.SampleBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class SampleBookService extends CrudService<SampleBook, Integer> {

    private SampleBookRepository repository;

    public SampleBookService(@Autowired SampleBookRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SampleBookRepository getRepository() {
        return repository;
    }

}
