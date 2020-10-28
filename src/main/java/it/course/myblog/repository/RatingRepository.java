package it.course.myblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Rating;
import it.course.myblog.entity.RatingUserPostCompositeKey;

@Repository
public interface RatingRepository extends JpaRepository<Rating, RatingUserPostCompositeKey> {
	List<Rating> findByRatingUserPostCompositeKeyPostId(Post p);
}
