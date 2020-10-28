package it.course.myblog.controller;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.course.myblog.entity.Tag;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.TagRepository;

@RestController

public class TagController {
	@Autowired TagRepository tagRepository;
	
	@PostMapping("private/create-tag")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> createTag(@RequestParam String tagName,HttpServletRequest request) {
		if(!tagName.isEmpty()) {
			Optional<Tag> t=tagRepository.findByTagName(tagName);
			if(!t.isPresent()) {
				Tag newTag = new Tag(tagName.toUpperCase());
				tagRepository.save(newTag);
			} else {
				return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Tag already present", request.getRequestURI()), HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 400, "Bad request", "No tag defined in request", request.getRequestURI()), HttpStatus.BAD_REQUEST);
		}
	return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
			Instant.now(), 200, "OK", "Tag successfully added", request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("private/update-tag")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateTag(@RequestParam String oldTagName,@RequestParam String newTagName,HttpServletRequest request) {
		if(!newTagName.isEmpty()) {
			Optional<Tag> t=tagRepository.findByTagName(oldTagName);
			t.get().setTagName(newTagName.toUpperCase());
			tagRepository.save(t.get());
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 400, "Bad request", "No tag defined in request", request.getRequestURI()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Tag successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("private/get-all-tags")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getTags(HttpServletRequest request) {
		List<Tag> tags = tagRepository.findAll();
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", tags, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/get-all-visible-tags")
	public ResponseEntity<ApiResponseCustom> getVisibleTags(HttpServletRequest request) {
		List<Tag> tags = tagRepository.findByVisibleTrue();
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", tags, request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("private/update-visibility-tag")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateVisibilityTag(@RequestParam String tagName,HttpServletRequest request) {
		Optional<Tag> t=tagRepository.findByTagName(tagName);
		t.get().setVisible(!t.get().getVisible());
		tagRepository.save(t.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Tag visibility successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
}