package it.course.myblog.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.DBFile;
import it.course.myblog.entity.Language;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.Tag;
import it.course.myblog.entity.User;
import it.course.myblog.payload.request.CommentRequest;
import it.course.myblog.payload.request.PostRequest;
import it.course.myblog.payload.request.PostTagRequest;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.payload.response.PostResponse;
import it.course.myblog.payload.response.PostResponseWithoutComments;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.LanguageRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.TagRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.JwtUser;
import it.course.myblog.service.DBFileService;
import it.course.myblog.service.UserService;

@RestController
public class PostController {
	
	@Autowired PostRepository postRepository;
	@Autowired UserService userService;
	@Autowired UserRepository userRepository;
	@Autowired LanguageRepository languageRepository;
	@Autowired TagRepository tagRepository;
	@Autowired DBFileService dbFileService;
	
	@PostMapping("private/create-post")
	@Transactional
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> createPost(@RequestParam String title, @RequestParam String overview,
			@RequestParam String content,@RequestParam String language,@RequestParam MultipartFile file,HttpServletRequest request) {
		
		if(postRepository.existsByTitle(title))
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Post Title already present", request.getRequestURI()), HttpStatus.OK);
		
		Optional<Language> l = languageRepository.findById(language);
		if(!l.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Language not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		String overv = overview.isEmpty() ? null : overview;
		
		JwtUser jwtUser = userService.getAuthenticatedUser();
		User author =  userRepository.findByUsername(jwtUser.getUsername()).get();
		
		String msg = dbFileService.ctrlImageSize(dbFileService.getBufferedImage(file));
		if(msg != null) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 403, "FORBIDDEN", msg, request.getRequestURI()), HttpStatus.FORBIDDEN);
		String msgkb = dbFileService.ctrlImageLength(file);
		if(msgkb != null) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 403, "FORBIDDEN", msgkb, request.getRequestURI()), HttpStatus.FORBIDDEN);
				
		Post p = new Post(
				title,
				overv,
				content,
				author,
				l.get(),
				dbFileService.fromMultiToDBFile(file)
				);
		
		postRepository.save(p);
		
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Post successfully created", request.getRequestURI()), HttpStatus.OK);
		
		
	}
	
	@PutMapping("private/update-post/{id}")
	@PreAuthorize("hasRole('EDITOR')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> updatePost(@RequestParam String title, @RequestParam String overview,
			@RequestParam String content,@RequestParam String language,@RequestParam MultipartFile file,
			@PathVariable Long id, HttpServletRequest request) {
		
		Optional<Post> p = postRepository.findById(id);
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not found", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		Optional<Language> l = languageRepository.findById(language);
		if(!l.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Language not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		
		JwtUser jwtUser = userService.getAuthenticatedUser();
		User author =  userRepository.findByUsername(jwtUser.getUsername()).get();
		
		// può aggiornare il post solo lo user che lo ha scritto
		if(!p.get().getAuthor().equals(author)) 
		 return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Permission denied", request.getRequestURI()), HttpStatus.OK);
		
		String msg = dbFileService.ctrlImageSize(dbFileService.getBufferedImage(file));
		if(msg != null) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 403, "FORBIDDEN", msg, request.getRequestURI()), HttpStatus.FORBIDDEN);
		String msgkb = dbFileService.ctrlImageLength(file);
		if(msgkb != null) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 403, "FORBIDDEN", msgkb, request.getRequestURI()), HttpStatus.FORBIDDEN);		
		
		p.get().setTitle(title);
		p.get().setOverview(overview);
		p.get().setContent(content);
		p.get().setLanguage(l.get());
		p.get().setApproved(false);
		p.get().setVisible(false);
		
		DBFile dbfile = dbFileService.fromMultiToDBFile(file);
		if(!p.get().getDbfile().equals(dbfile)) p.get().setDbfile(dbFileService.copyImageContent(p.get().getDbfile(),dbfile));
		
		postRepository.save(p.get());
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Post successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/get-post/{id}")
	public ResponseEntity<ApiResponseCustom> getPost(@PathVariable Long id, HttpServletRequest request) {
		Optional<Post> p = postRepository.findByIdAndVisibleTrue(id);
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		List<Comment> comments = p.get().getComments();
		List<String> cs = new ArrayList<String>();
		cs=comments.stream().map(s -> s.getComment()).collect(Collectors.toList());
		PostResponse pr = PostResponse.createFromEntity(p.get(), cs);
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", pr, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/get-post-nq/{id}")
	public ResponseEntity<ApiResponseCustom> getPostNativeQuery(@PathVariable Long id, HttpServletRequest request) {
		Optional<Post> p = postRepository.findByIdAndVisibleTrueNativeQuery(id);
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		List<Comment> comments = p.get().getComments();
		List<String> cs = new ArrayList<String>();
		cs=comments.stream().map(s -> s.getComment()).collect(Collectors.toList());
//		if(!comments.isEmpty()) {
//			cs=comments.stream().map(s -> s.getComment()).collect(Collectors.toList());
//		}
		PostResponse pr = PostResponse.createFromEntity(p.get(), cs);
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", pr, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/get-post-jpql/{id}")
	public ResponseEntity<ApiResponseCustom> getPostJpql(@PathVariable Long id, HttpServletRequest request) {
		Optional<Post> p = postRepository.findById(id);
		List<String> cs = p.get().getComments().stream().map(x -> x.getComment()).collect(Collectors.toList());
		PostResponseWithoutComments prwc = postRepository.findByIdAndVisibleTrueJpql(id);
		PostResponse pr = PostResponse.mergePostComments(prwc, cs);
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", pr, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/get-post-view")
	public ResponseEntity<ApiResponseCustom> getPost(HttpServletRequest request) {
		List<Post> ps = postRepository.findAllAndVisibleTrueView();
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", ps, request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("private/update-visibilty-post/{id}")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> updateVisibilityPost(@PathVariable Long id, HttpServletRequest request){
		Optional<Post> p = postRepository.findById(id);
		p.get().setVisible(!p.get().getVisible());
		postRepository.save(p.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Post visibility successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("private/update-approvation-post/{id}")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> updateApprovationPost(@PathVariable Long id, HttpServletRequest request){
		Optional<Post> p = postRepository.findById(id);
		p.get().setApproved(!p.get().getApproved());
		postRepository.save(p.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Post approvation successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@PostMapping("private/create-post-tags")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> createPostTags(@Valid @RequestBody PostTagRequest postTagRequest,HttpServletRequest request) {
		Optional<Post> p = postRepository.findById(postTagRequest.getPostId());
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		Set<Tag> tags = tagRepository.findByTagNameIn(postTagRequest.getTagNames());
		if(tags.isEmpty()) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 404, "OK", "Tags not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		p.get().setTags(tags);
		postRepository.save(p.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Post-tags associations successfully created", request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("private/delete-post-tags")
	@PreAuthorize("hasRole('EDITOR')")
	public ResponseEntity<ApiResponseCustom> deletePostTags(@RequestParam Long id, HttpServletRequest request) {
		Optional<Post> p = postRepository.findById(id);
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		p.get().setTags(null);
		postRepository.save(p.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Post-tags associations successfully deleted", request.getRequestURI()), HttpStatus.OK);
	}
	
	@PostMapping("private/create-comment")
	@PreAuthorize("hasRole('READER')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> createComment(@Valid @RequestBody CommentRequest commentRequest,HttpServletRequest request) {
		Optional<Post> p = postRepository.findById(commentRequest.getPostId());
		if(!p.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Post not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		JwtUser jwtUser = userService.getAuthenticatedUser();
		User commentAuthor = userRepository.findByUsername(jwtUser.getUsername()).get();
		List<Comment> comments = p.get().getComments();
		comments.add(new Comment(commentRequest.getComment(),commentAuthor));
		p.get().setComments(comments);
		postRepository.save(p.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Comment successfully created", request.getRequestURI()), HttpStatus.OK);
	}
}
