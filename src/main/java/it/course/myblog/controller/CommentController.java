package it.course.myblog.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Post;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.payload.response.CommentResponse;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.PostRepository;

@RestController

public class CommentController {
	
	@Autowired CommentRepository commentRepository;
	@Autowired PostRepository postRepository;
	
	@PutMapping("private/update-comment-visibility")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateCommentVisibility(@RequestParam Long id, HttpServletRequest request) {
		Optional<Comment> c = commentRepository.findById(id);
		if(!c.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Comment not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		c.get().setVisible(!c.get().getVisible());
		commentRepository.save(c.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Comment visibility successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("private/comment-counter")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> commentCounter(HttpServletRequest request) {
		List<Post> pl = new ArrayList<Post>();
		List<CommentResponse> crl = new ArrayList<CommentResponse>();
		pl = postRepository.findAll();
		if(pl.isEmpty()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Posts are not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		for(Post p : pl) {
			CommentResponse cr;
			Long nc = (long) p.getComments().size();
			cr = CommentResponse.createFromEntity(p,nc);
			crl.add(cr);
		}
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", crl, request.getRequestURI()), HttpStatus.OK);
	}
}
