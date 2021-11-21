package bsu.pischule.csab.imdb.data.repository;

import bsu.pischule.csab.imdb.data.entity.Name;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NameRepository extends JpaRepository<Name, Integer> {

}