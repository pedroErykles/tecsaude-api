package com.api.sigmax.tecsaude.services;

import com.api.sigmax.tecsaude.domain.model.Patient;
import com.api.sigmax.tecsaude.repositories.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll(){
        return patientRepository.findAll();
    }

    public Optional<Patient> findById(UUID id){
        return patientRepository.findById(id);
    }

    public boolean existsById(UUID id){
        return patientRepository.existsById(id);
    }

    public boolean existsByRg(String rg){ return patientRepository.existsByRg(rg); }

    public boolean existsByCpf(String cpf){
        return patientRepository.existsByCpf(cpf);
    }

    @Transactional
    public Patient save(Patient patient){
        return patientRepository.save(patient);
    }

    @Transactional
    public Patient update(Patient patient){
        return patientRepository.save(patient);
    }

    @Transactional
    public void delete(UUID id){
        patientRepository.deleteById(id);
    }

}
