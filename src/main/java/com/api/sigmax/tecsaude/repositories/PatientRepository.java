package com.api.sigmax.tecsaude.repositories;

import com.api.sigmax.tecsaude.domain.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    boolean existsByCpf(String cpf);

    boolean existsByRg(String rg);

    @Override
    boolean existsById(UUID uuid);

    @Query(value = "SELECT COUNT(id) FROM TB_PATIENT", nativeQuery = true)
    int registeredPatients();
}
