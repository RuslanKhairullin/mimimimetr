package ru.ruslankhayrullin.mimimimetr.model.persistence;

import lombok.*;

import javax.persistence.*;

/**
 * Entity with information about cats rate
 */
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CatRateInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Getter
    @Setter
    @Column
    private String nameCat;


    @Column(columnDefinition = "integer default 0")
    private Integer points;
}
