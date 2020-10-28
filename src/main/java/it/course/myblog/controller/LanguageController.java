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

import it.course.myblog.entity.Language;
import it.course.myblog.payload.response.ApiResponseCustom;
import it.course.myblog.repository.LanguageRepository;

@RestController

public class LanguageController { 
	@Autowired LanguageRepository langRepository;
	
	@PostMapping("private/create-language")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> createLanguage(@RequestParam String langCode,String langDesc,HttpServletRequest request) {
		if(!langCode.isEmpty()) {
			Optional<Language> l = langRepository.findById(langCode);
			if(!l.isPresent()) {
				Language newLang = new Language(langCode.toUpperCase(),langDesc);
				langRepository.save(newLang);
			} else {
				return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Language already present", request.getRequestURI()), HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 400, "Bad request", "No language defined in request", request.getRequestURI()), HttpStatus.BAD_REQUEST);
		}
	return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
			Instant.now(), 200, "OK", "Language successfully added", request.getRequestURI()), HttpStatus.OK);
	}	  
	
	@PutMapping("private/update-language")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateLanguage(@RequestParam String oldLangCode,@RequestParam String newLangCode,String langDesc,HttpServletRequest request) {
		if(!newLangCode.isEmpty()) {
			Optional<Language> l = langRepository.findById(oldLangCode);
			l.get().setCode(newLangCode.toUpperCase());
			langRepository.save(l.get());
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 400, "Bad request", "No language defined in request", request.getRequestURI()), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Language successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("private/get-all-languages")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getLanguages(HttpServletRequest request) {
		List<Language> langs = langRepository.findAll();
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", langs, request.getRequestURI()), HttpStatus.OK);
	}
	
	@GetMapping("public/get-all-visible-languages")
	public ResponseEntity<ApiResponseCustom> getVisibleTags(HttpServletRequest request) {
		List<Language> langs = langRepository.findByVisibleTrue();
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", langs, request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("private/update-visibility-language")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateVisibilityTag(@RequestParam String langCode,HttpServletRequest request) {
		Optional<Language> l = langRepository.findById(langCode);
		l.get().setVisible(!l.get().getVisible());
		langRepository.save(l.get());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Language visibility successfully updated", request.getRequestURI()), HttpStatus.OK);
	}
	
}
