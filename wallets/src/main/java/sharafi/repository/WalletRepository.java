package sharafi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sharafi.model.Holder;
import sharafi.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Optional<Wallet> findByHolderAndTag(Holder holder, String tag);

	boolean existsByHolderAndTag(Holder holder, String tag);

	List<Wallet> findAllByHolderOrderByCreatedDesc(Holder holder);
}
