package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter @Setter
public class ModuleContenu {


    @ElementCollection
    @CollectionTable(
            name = "decodeur_chaines",
            joinColumns = @JoinColumn(name = "decodeur_id_decodeur")
    )
    @Column(name = "chaines")
    private List<String> chaines = new ArrayList<>();

    public boolean contientChaine(String chaine) {
        return chaines.contains(chaine);
    }

    public boolean ajouterChaine(String chaine) {
        if (contientChaine(chaine)) {
            return false;
        }
        chaines.add(chaine);
        return true;
    }

    public boolean supprimerChaine(String chaine) {
        if(!contientChaine(chaine)) {
            return false;
        }
        return chaines.remove(chaine);

    }
}
