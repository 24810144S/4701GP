package hk.edu.cpce.gp4701.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hk.edu.cpce.gp4701.model.Account;
import hk.edu.cpce.gp4701.model.AdmissionApplication;

public interface AdmissionApplicationRepository extends JpaRepository<AdmissionApplication, Long> {
    List<AdmissionApplication> findByMemberOrderBySubmittedAtDesc(Account member);

    @Query("SELECT a FROM AdmissionApplication a " +
           "JOIN FETCH a.member " +
           "JOIN FETCH a.programmeLevel pl " +
           "JOIN FETCH pl.programme " +
           "ORDER BY a.submittedAt DESC")
    List<AdmissionApplication> findAllWithDetailsOrderBySubmittedAtDesc();

    @Query("SELECT COUNT(a) FROM AdmissionApplication a " +
           "WHERE a.programmeLevel.id = :levelId " +
           "AND UPPER(a.decision) = 'SUCCESS' " +
           "AND (a.offerResponse IS NULL OR UPPER(a.offerResponse) <> 'DECLINED')")
    long countOccupiedSeats(@Param("levelId") Long programmeLevelId);
}