package it.course.myblog.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Authority;
import it.course.myblog.entity.AuthorityName;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    
	Optional<Authority> findByName(AuthorityName name);
	
	Set<Authority> findByNameIn(Set<AuthorityName> authorityNames);

}
