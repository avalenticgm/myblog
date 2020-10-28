package it.course.myblog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Post;
import it.course.myblog.payload.response.PostResponse;
import it.course.myblog.payload.response.PostResponseWithoutComments;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	Optional<Post> findById(Long id);
	Boolean existsByTitle(String title);
	List<Post> findAll();
	List<Post> findByVisibleTrue();
	Optional<Post> findByIdAndVisibleTrue(Long id);
	@Query(value="SELECT * FROM post WHERE id=:id AND is_visible=1",nativeQuery=true)
	Optional<Post> findByIdAndVisibleTrueNativeQuery(@Param("id") Long id);
	@Query(value="SELECT new it.course.myblog.payload.response.PostResponseWithoutComments("
			+ "p.id, p.title, p.overview, p.content, p.updatedAt, p.author.username, p.language.description, p.dbfile.fileName) "
			+ "FROM Post p "
			+ "WHERE p.visible=true AND p.id=:id")
	PostResponseWithoutComments findByIdAndVisibleTrueJpql(@Param("id") Long id);
	@Query(value="SELECT * FROM findallandvisibletrueview",nativeQuery=true)
	List<Post> findAllAndVisibleTrueView();
}