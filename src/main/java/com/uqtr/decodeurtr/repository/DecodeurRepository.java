package com.uqtr.decodeurtr.repository;

import com.uqtr.decodeurtr.entity.Decodeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecodeurRepository extends JpaRepository<Decodeur,Long> {
    List<Decodeur> findByClientIsNotNull();
    List<Decodeur> findByClientIsNull();

    List<Decodeur> findByClientId(Long idClient);
}
