package bsu.pischule.csab.imdb.data.service;

import bsu.pischule.csab.imdb.data.entity.SamplePerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, Integer> {

}