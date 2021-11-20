package bsu.pischule.csab.imdb.data.service;

import bsu.pischule.csab.imdb.data.entity.SampleBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleBookRepository extends JpaRepository<SampleBook, Integer> {

}