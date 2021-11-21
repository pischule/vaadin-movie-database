package bsu.pischule.csab.imdb.data.repository;

import bsu.pischule.csab.imdb.data.entity.Film;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilmRepository extends JpaRepository<Film, Integer> {

}