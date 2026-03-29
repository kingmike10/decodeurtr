package com.uqtr.decodeurtr.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "decodeur")
public class Decodeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_decodeur")
    private Long id;

    @Column(name = "adresse_ip", nullable = false, unique = true, length = 50)
    private String adresseIp;

    @Enumerated(EnumType.STRING)
    @Column(name = "etat", nullable = false, length = 20)
    private EtatDecodeur etat;

    @Column(name = "last_restart")
    private LocalDateTime lastRestart;

    @Column(name = "last_reinit")
    private LocalDateTime lastReinit;

    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    @Embedded
    private ModuleContenu moduleContenu = new ModuleContenu();


    public boolean ajouterChaine(String chaine) {
        return moduleContenu.ajouterChaine(chaine);
    }

        public boolean supprimerChaine(String chaine) {
            return moduleContenu.supprimerChaine(chaine);
        }


}
