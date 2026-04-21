package com.uqtr.decodeurtr.repository;

import com.uqtr.decodeurtr.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository  extends JpaRepository<Client, Long> {
        boolean existsByNomClientAndAdresse(String nomClient, String adresse);

}

