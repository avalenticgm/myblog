package it.course.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Advisory;
import it.course.myblog.entity.AdvisoryId;

@Repository
public interface AdvisoryRepository extends JpaRepository<Advisory,AdvisoryId> {
	
}
