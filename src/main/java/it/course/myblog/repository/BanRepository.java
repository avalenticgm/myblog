package it.course.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Advisory;
import it.course.myblog.entity.AdvisoryId;
import it.course.myblog.entity.Ban;
import it.course.myblog.entity.BanId;
import it.course.myblog.entity.User;

@Repository
public interface BanRepository extends JpaRepository<Ban,BanId> {
	
}
