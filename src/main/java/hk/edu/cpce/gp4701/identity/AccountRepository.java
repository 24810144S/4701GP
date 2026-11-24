package hk.edu.cpce.gp4701.identity;

import java.util.Optional;
import hk.edu.cpce.gp4701.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
