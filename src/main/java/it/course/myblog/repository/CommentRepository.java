package it.course.myblog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findById(Long id);
	Optional<Comment> findByIdAndVisibleTrue(Long id);
}