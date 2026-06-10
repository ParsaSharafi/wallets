package sharafi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sharafi.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query(value = "SELECT * FROM (SELECT signed_amount AS signedAmount, payment_gateway_reference AS info, " +
			"initiated, status FROM transfer WHERE wallet_id = ?1 UNION ALL " +
			"SELECT CASE WHEN t.payer_wallet_id = ?1 THEN -t.payer_amount ELSE t.payee_amount END AS signedAmount, " +
			"CASE WHEN t.payer_wallet_id = ?1 THEN w_payee.holder || ' --> ' || w_payee.tag ELSE " +
			"w_payer.holder || ' --> ' || w_payer.tag END AS info, t.initiated AS initiated, t.status AS status " +
			"FROM transaction t LEFT JOIN wallet w_payee ON t.payee_wallet_id = w_payee.id " +
			"LEFT JOIN wallet w_payer ON t.payer_wallet_id = w_payer.id WHERE ?1 IN " +
			"(t.payer_wallet_id, t.payee_wallet_id)) q ORDER BY initiated DESC;", nativeQuery = true)
	List<Object[]> getAllTransactions(long walletId);
}
