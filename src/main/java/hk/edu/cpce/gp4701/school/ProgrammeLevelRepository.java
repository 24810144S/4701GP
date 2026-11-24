package hk.edu.cpce.gp4701.school;

import java.util.Optional;
import hk.edu.cpce.gp4701.model.ProgrammeLevel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgrammeLevelRepository extends JpaRepository<ProgrammeLevel, Long> {
    @EntityGraph(attributePaths = "programme")
    Optional<ProgrammeLevel> findFirstByOrderByIdAsc();
}
