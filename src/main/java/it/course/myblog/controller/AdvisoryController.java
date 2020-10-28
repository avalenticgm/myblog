package it.course.myblog.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Advisory;
import it.course.myblog.entity.AdvisoryId;
import it.course.myblog.entity.AdvisoryReason;
import it.course.myblog.entity.Ban;
import it.course.myblog.entity.BanId;
import it.course.myblog.entity.Comment;
import it.course.myblog.entity.User;
import it.course.myblog.payload.request.AdvisoryRequest;
import it.course.myblog.payload.response.AdvisoryResponse;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.AdvisoryRepository;
import it.course.myblog.repository.BanRepository;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.security.JwtUser;
import it.course.myblog.service.UserService;

@RestController
public class AdvisoryController {
	@Autowired AdvisoryRepository advisoryRepository;
	@Autowired UserRepository userRepository;
	@Autowired UserService userService;
	@Autowired CommentRepository commentRepository;
	@Autowired BanRepository banRepository;
	
	@PostMapping("private/advisory")
	public ResponseEntity<ApiResponseCustom> advisory(@Valid @RequestBody AdvisoryRequest advisoryRequest,HttpServletRequest request) {
		Optional<Comment> comment = commentRepository.findByIdAndVisibleTrue(advisoryRequest.getCommentId());
		if(!comment.isPresent()) 
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 404, "OK", "Comment not present", request.getRequestURI()), HttpStatus.NOT_FOUND);
		JwtUser jwtUser = userService.getAuthenticatedUser();
		User reporter =  userRepository.findByUsername(jwtUser.getUsername()).get();
		if(reporter.equals(comment.get().getCommentAuthor()))
			 return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
						Instant.now(), 200, "OK", "You cannot report your comment", request.getRequestURI()), HttpStatus.OK);
		AdvisoryId id = new AdvisoryId(reporter,comment.get());
		if(advisoryRepository.existsById(id))
			 return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
						Instant.now(), 200, "OK", "Comment already reported by you", request.getRequestURI()), HttpStatus.OK);
		Advisory adv = new Advisory(id,AdvisoryReason.valueOf(advisoryRequest.getReason()));
		advisoryRepository.save(adv);
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Comment successfully reported", request.getRequestURI()), HttpStatus.OK);
	}
	
	//get advisory con response(commentid, comment, reporterid, reporter, authorid, author, reason, status, dataupdate, titolopost)
	@GetMapping("private/get-advisories")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getAdvisories(HttpServletRequest request) {
		List<Advisory> al = advisoryRepository.findAll();
		List<AdvisoryResponse> arl = new ArrayList<>();
		for(Advisory a : al) arl.add(AdvisoryResponse.createFromEntity(a));
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", arl, request.getRequestURI()), HttpStatus.OK);
	}
	
	//put cambia stato advisory con param(advisoryid, boolean visible del commento)
	@PutMapping("private/update-advisory")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> updateAdvisory(@RequestParam Long userId,
			@RequestParam Long commentId, @RequestParam Boolean visible, HttpServletRequest request) {
		User u = userRepository.findById(userId).get();
		Comment c = commentRepository.findById(commentId).get();
		Advisory a = advisoryRepository.findById(new AdvisoryId(u,c)).get();
		a.setStatus(true);
		c.setVisible(visible);
		if(!visible) {
			Optional<Ban> bo = banRepository.findById(new BanId(c.getCommentAuthor()));
			if(!bo.isPresent()) {
				Ban b = new Ban(new BanId(c.getCommentAuthor()),1,LocalDateTime.now());
				banRepository.save(b);
			}
			else {
				bo.get().setAdvisories(bo.get().getAdvisories()+1);
				if(bo.get().getAdvisories()==3) {
					bo.get().setBannedUntil(LocalDateTime.now().plus(1,ChronoUnit.WEEKS));
					c.getCommentAuthor().setEnabled(false);
				}
				if(bo.get().getAdvisories()==6) {
					bo.get().setBannedUntil(LocalDateTime.now().plus(1,ChronoUnit.CENTURIES));
					c.getCommentAuthor().setEnabled(false);
				}
				banRepository.save(bo.get());
			}
		}
//		if(!visible) u.setAdvisories(u.getAdvisories()+1);
//		if(u.getAdvisories()==3 || u.getAdvisories()==6) u.setEnabled(false);
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Advisory successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
}
