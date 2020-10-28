package it.course.myblog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);	
	
	Optional<User> findByUsernameOrEmail(String username, String email);
	
	Boolean existsByUsernameOrEmail(String username, String email);
	
	List<User> findAllByEnabledTrue();
	
	Optional<User> findByIdAndEnabledTrue(Long id);
	
	Optional<User> findByUsername(String username);
	Optional<User> findByUsernameAndEnabledTrue(String username);
    
    Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	Optional<User> findByIdentifierCode(String identifierCode);

	
}
