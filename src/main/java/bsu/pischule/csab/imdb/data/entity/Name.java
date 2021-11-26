package bsu.pischule.csab.imdb.data.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE name SET deleted = true WHERE id=?")
@Entity
public class Name extends AbstractEntity {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfDeath;
    @DecimalMin("0.00") @DecimalMax("2.72")
    private Double height;
    @Min(0) @Max(100)
    private Integer numberOfChildren;
    private Boolean deleted = Boolean.FALSE;

    public String getFullName() {
        return Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
