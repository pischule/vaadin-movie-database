package bsu.pischule.csab.imdb.data.generator;

import bsu.pischule.csab.imdb.data.entity.Film;
import bsu.pischule.csab.imdb.data.entity.Name;
import bsu.pischule.csab.imdb.data.repository.FilmRepository;
import bsu.pischule.csab.imdb.data.repository.NameRepository;
import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;

import java.time.LocalDateTime;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

@SpringComponent
public class DataGenerator {

    public CommandLineRunner loadData(NameRepository nameRepository,
                                      FilmRepository filmRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (nameRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Sample Person entities...");
            ExampleDataGenerator<Name> samplePersonRepositoryGenerator = new ExampleDataGenerator<>(
                    Name.class, LocalDateTime.of(2021, 11, 20, 0, 0, 0));
            samplePersonRepositoryGenerator.setData(Name::setId, DataType.ID);
            samplePersonRepositoryGenerator.setData(Name::setFirstName, DataType.FIRST_NAME);
            samplePersonRepositoryGenerator.setData(Name::setLastName, DataType.LAST_NAME);
            samplePersonRepositoryGenerator.setData(Name::setDateOfBirth, DataType.DATE_OF_BIRTH);
            samplePersonRepositoryGenerator.setData(Name::setHeight, new DataType<>() {
                @Override
                public Double getValue(Random random, int i, LocalDateTime localDateTime) {
                    return random.nextDouble() * (1.8 - 1.2) + 1.2;
                }
            });
            samplePersonRepositoryGenerator.setData(Name::setNumberOfChildren, DataType.NUMBER_UP_TO_10);
            nameRepository.saveAll(samplePersonRepositoryGenerator.create(100, seed));

            logger.info("... generating 100 Sample Book entities...");
            ExampleDataGenerator<Film> sampleBookRepositoryGenerator = new ExampleDataGenerator<>(
                    Film.class, LocalDateTime.of(2021, 11, 20, 0, 0, 0));
            sampleBookRepositoryGenerator.setData(Film::setId, DataType.ID);
            sampleBookRepositoryGenerator.setData(Film::setName, DataType.BOOK_TITLE);
            sampleBookRepositoryGenerator.setData(Film::setRating, new DataType<>() {
                @Override
                public Double getValue(Random random, int i, LocalDateTime localDateTime) {
                    return random.nextInt(100) / 10.0;
                }
            });
            sampleBookRepositoryGenerator.setData(Film::setDuration, DataType.NUMBER_UP_TO_100);
            sampleBookRepositoryGenerator.setData(Film::setDescription, new DataType<>() {
                @Override
                public String getValue(Random random, int i, LocalDateTime localDateTime) {
                    return """
                            Jack and his family move into an isolated hotel with a violent past.
                             Living in isolation, Jack begins to lose his sanity, which affects his family members.
                            """;
                }
            });
            sampleBookRepositoryGenerator.setData(Film::setReleaseDate, DataType.DATE_LAST_10_YEARS);
            filmRepository.saveAll(sampleBookRepositoryGenerator.create(100, seed));

            logger.info("Generated demo data");
        };
    }

}