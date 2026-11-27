package hk.edu.cpce.gp4701.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hk.edu.cpce.gp4701.model.Account;
import hk.edu.cpce.gp4701.model.AdmissionApplication;

public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    List<AdmissionApplication> findByMemberOrderBySubmittedAtDesc(Account member);
}