package it.course.myblog.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Post;
import it.course.myblog.entity.Rating;
import it.course.myblog.entity.RatingUserPostCompositeKey;
import it.course.myblog.entity.User;
import it.course.myblog.payload.request.RatePostRequest;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.payload.response.AverageRateResponse;
import it.course.myblog.payload.response.EditorAverageRateResponse;
import it.course.myblog.repository.AuthorityRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.RatingRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.JwtUser;
import it.course.myblog.service.UserService;

@RestController

public class RatingController {
	
	@Autowired UserService userService;
	@Autowired UserRepository userRepository;
	@Autowired PostRepository postRepository;
	@Autowired RatingRepository ratingRepository;
	@Autowired AuthorityRepository authorityRepository;
	
	@PostMapping("private/rate-post")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> ratePost(@RequestBody RatePostRequest ratePostRequest, HttpServletRequest request) {
		JwtUser jwtUser = userService.getAuthenticatedUser();
		User user = userRepository.findByUsername(jwtUser.getUsername()).get();
		Optional<Post> p = postRepository.findById(ratePostRequest.getPostId());
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		if(!p.get().getVisible()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "You can't rate this post: post not visible", request.getRequestURI()), HttpStatus.OK);
		RatingUserPostCompositeKey ck = new RatingUserPostCompositeKey(user,p.get());
		Rating r = new Rating(ck, ratePostRequest.getRating());
		String msg = ratingRepository.existsById(ck) ? "Rate updated" : "Post successfully rated";
		ratingRepository.save(r);
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", msg, request.getRequestURI()), HttpStatus.OK);
	}
	
	@DeleteMapping("private/delete-rate")
	@PreAuthorize("hasRole('READER')")
	public ResponseEntity<ApiResponseCustom> deleteRate(@RequestBody RatePostRequest ratePostRequest, HttpServletRequest request) {
		JwtUser jwtUser = userService.getAuthenticatedUser();
		User user = userRepository.findByUsername(jwtUser.getUsername()).get();
		Optional<Post> p = postRepository.findById(ratePostRequest.getPostId());
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Post not found", request.getRequestURI()), HttpStatus.OK);
		if(!p.get().getVisible()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Post not visible", request.getRequestURI()), HttpStatus.OK);
		RatingUserPostCompositeKey ck = new RatingUserPostCompositeKey(user,p.get());
		Optional<Rating> ro = ratingRepository.findById(ck);
		if(!ro.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Rate not present", request.getRequestURI()), HttpStatus.OK);
		ratingRepository.delete(ro.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Post successfully unrated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/average-rate/{postId}")
	public ResponseEntity<ApiResponseCustom> averageRate(@PathVariable Long postId, HttpServletRequest request) {
		Optional<Post> p = postRepository.findById(postId);
		if(!p.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		if(!p.get().getVisible())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Post not visible", request.getRequestURI()), HttpStatus.OK);
		Double avg = ratingRepository.findByRatingUserPostCompositeKeyPostId(p.get()).stream()
				.mapToInt(x -> x.getRating()).average().getAsDouble();
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", avg, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("private/all-posts-average-rate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> allAverageRate(HttpServletRequest request) {
		List<Post> pl = new ArrayList<>();
		List<AverageRateResponse> arl = new ArrayList<>();
		pl = postRepository.findByVisibleTrue();
		if(pl.isEmpty())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		for(Post p : pl) {
			Double avg = ratingRepository.findByRatingUserPostCompositeKeyPostId(p).stream()
					.mapToInt(x -> x.getRating()).average().getAsDouble();
			AverageRateResponse ar = AverageRateResponse.createFromEntity(p,avg);
			arl.add(ar);
		}
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", arl, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("private/editors-average-rate")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> editorsAverageRate(HttpServletRequest request) {
		List<User> el = new ArrayList<>();
		List<EditorAverageRateResponse> erl = new ArrayList<>();
		el = userRepository.findAll().stream()
				.filter(e -> e.getAuthorities().contains(authorityRepository.findById((long) 2).get()))
				.collect(Collectors.toList());

		if(el.isEmpty())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Editors are not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		for(User e : el) {
			List<Post> pl = new ArrayList<>();
			pl = postRepository.findByVisibleTrue().stream()
					.filter(p -> p.getAuthor().getId() == e.getId())
					.collect(Collectors.toList());
			//Non riesco a farlo tutto con gli stream
			Double avg = 0.0;
			int count = 0;
			for(Post p : pl) {
				avg += ratingRepository.findByRatingUserPostCompositeKeyPostId(p).stream()
					.mapToInt(x -> x.getRating()).sum();
				count += ratingRepository.findByRatingUserPostCompositeKeyPostId(p).stream()
					.mapToInt(x -> x.getRating()).count();
			}
			avg /= count;
			EditorAverageRateResponse er = EditorAverageRateResponse.createFromEntity(e,avg);
			erl.add(er);
		}
		erl.sort(Comparator.comparing(EditorAverageRateResponse::getAverage,Comparator.reverseOrder()));
		//ordinamento non funziona
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", erl, request.getRequestURI()), HttpStatus.OK);
	}
}
