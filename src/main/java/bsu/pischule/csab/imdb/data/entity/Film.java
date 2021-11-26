package bsu.pischule.csab.imdb.data.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@SQLDelete(sql = "UPDATE film SET deleted = true WHERE id=?")
@Entity
public class Film extends AbstractEntity {
    @NotEmpty
    private String name;
    @ManyToOne
    private Name director;
    @Min(0)
    private Integer duration;
    @Column(columnDefinition = "text")
    private String description;
    @Min(0) @Max(10)
    private Double rating;
    private LocalDate releaseDate;
    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Name> actors;
    private Boolean deleted = Boolean.FALSE;
}
