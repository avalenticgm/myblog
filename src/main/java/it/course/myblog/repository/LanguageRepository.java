package it.course.myblog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Language;

@Repository
public interface LanguageRepository extends JpaRepository<Language, String>{
	Optional<Language> findById(String langCode);
	List<Language> findByVisibleTrue();
}