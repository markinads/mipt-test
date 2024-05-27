package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Entity
@Table(name = "zoo_animal")
@AllArgsConstructor
public class ZooAnimal {

    @Getter
    private int zoo_id;
    @Getter
    private int animal_id;
    @Getter
    private Timestamp time_apperance;
    @Getter
    private int workman_id;

}