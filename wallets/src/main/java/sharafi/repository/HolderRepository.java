package sharafi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sharafi.model.Holder;

@Repository
public interface HolderRepository extends JpaRepository<Holder, String> {

}
