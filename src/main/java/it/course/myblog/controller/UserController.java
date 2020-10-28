package it.course.myblog.controller;

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.course.myblog.entity.User;

import it.course.myblog.payload.response.ApiResponseCustom;

import it.course.myblog.payload.response.UserMeResponse;
import it.course.myblog.payload.response.UserResponse;
import it.course.myblog.repository.UserRepository;
import it.course.myblog.repository.AuthorityRepository;
import it.course.myblog.repository.BanRepository;
import it.course.myblog.repository.DBFileRepository;
import it.course.myblog.repository.LanguageRepository;

import it.course.myblog.repository.TagRepository;

import it.course.myblog.security.JwtTokenUtil;
import it.course.myblog.security.JwtUser;
import it.course.myblog.service.DBFileService;
import it.course.myblog.service.UserService;
import javassist.expr.NewArray;
import lombok.extern.slf4j.Slf4j;
import it.course.myblog.config.ApiValidationExceptionHandler;
import it.course.myblog.entity.Authority;
import it.course.myblog.entity.AuthorityName;
import it.course.myblog.entity.Ban;
import it.course.myblog.entity.BanId;
import it.course.myblog.entity.DBFile;
import it.course.myblog.entity.Language;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.Tag;
import it.course.myblog.exception.AppException;
import it.course.myblog.payload.request.SignUpRequest;
import it.course.myblog.payload.request.UpdateMe;
import it.course.myblog.payload.request.ChangeAuthority;
import it.course.myblog.payload.request.SignInRequest;
import it.course.myblog.security.JwtAuthenticationResponse;

@Slf4j
@RestController
public class UserController {
	
	@Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
	PasswordEncoder passwordEncoder;
    
    @Autowired
	UserService userService;
    
    @Autowired
    DBFileRepository dbFileRepository;
    
    @Autowired
    DBFileService dbFileService;
    
    @Autowired
    TagRepository tagRepository;
        
    @Autowired
    LanguageRepository languageRepository;
    
    @Autowired
    BanRepository banRepository;
	
	
	//GET Users List - select * from user;
	@GetMapping("private/get-users")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getAllUsers(HttpServletRequest request){
		
		List<User> us = userRepository.findAllByEnabledTrue();
		
		if(us.isEmpty())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Users not found", request.getRequestURI()), HttpStatus.OK);
		
		List<UserResponse> urs = us.stream().map(UserResponse::createFromEntity).collect(Collectors.toList());
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", urs, request.getRequestURI())	, HttpStatus.OK);	
		
	}
	
	//GET Single Users - select * from user where id = ?;
	@GetMapping("private/get-user")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> getSingleUser(@RequestParam Long id, HttpServletRequest request){
		
		Optional<User> u = userRepository.findByIdAndEnabledTrue(id);
	
		if(u.isPresent()) {
			UserResponse ur = UserResponse.createFromEntity(u.get());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", ur, request.getRequestURI())	, HttpStatus.OK);
		}
			
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		
	}
	
	//GET ME - select * from user where id = ?;
	@GetMapping("private/get-me")
	public ResponseEntity<ApiResponseCustom> getMe(HttpServletRequest request){
		
		JwtUser jwtUser = userService.getAuthenticatedUser();
		
		Optional<User> u = userRepository.findByUsername(jwtUser.getUsername());
		
		UserMeResponse um = UserMeResponse.createFromEntity(u.get());
	
		if(u.isPresent()) {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", um, request.getRequestURI())	, HttpStatus.OK);
		}
			
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		
	}
	
	
	
	// UPDATE ME
	@PutMapping("private/update-me")
	@Transactional
	public  ResponseEntity<ApiResponseCustom> updateMe(
			@RequestParam @Size(min=4, max=15) String username,
			@RequestParam @Size(min=5, max=100) String password,
			@RequestParam MultipartFile file,
			HttpServletRequest request) {
		
		JwtUser jwtUser = userService.getAuthenticatedUser();
		Optional<User> u = userRepository.findByUsername(jwtUser.getUsername());
		
		if(u.isPresent()) {
			
			String msg = dbFileService.ctrlImageSizeAvatar(dbFileService.getBufferedImage(file));
			if(msg != null) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 403, "FORBIDDEN", msg, request.getRequestURI()), HttpStatus.FORBIDDEN);
			
			if(!password.equals(u.get().getPassword()))
				u.get().setPassword(passwordEncoder.encode(password));
			
			u.get().setUsername(username);
			
			DBFile dbfile = dbFileService.fromMultiToDBFile(file);
			if(!u.get().getDbFile().equals(dbfile)) {
				u.get().setDbFile(dbFileService.copyImageContent(u.get().getDbFile(),dbfile));
				
			}
			
			
			userRepository.save(u.get());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User susccesfully updated", request.getRequestURI()), HttpStatus.OK);			
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		}
		
	}
	
	
	// LOGICAL DELETE
	@PutMapping("private/disable-user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> disableUser(@PathVariable Long id, HttpServletRequest request)  {
		
		Optional<User> uo = userRepository.findById(id);
		Optional<Ban> bo = banRepository.findById(new BanId(uo.get()));
		
		if(uo.isPresent()) {
			
			if(bo.isPresent()) {
				bo.get().setBannedDirectly(true);
//				banRepository.save(bo.get());
			} else {
				Ban b = new Ban(new BanId(uo.get()),0,LocalDateTime.now().plus(1,ChronoUnit.CENTURIES),true);
//				banRepository.save(b);
			}
			
			uo.get().setEnabled(false);
//			userRepository.save(uo.get());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User succesfully disabled", request.getRequestURI()), HttpStatus.OK);	
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		}
	}
	
	@PutMapping("private/enable-user/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<ApiResponseCustom> enableUser(@PathVariable Long id, HttpServletRequest request)  {
		
		Optional<User> uo = userRepository.findById(id);
		Optional<Ban> bo = banRepository.findById(new BanId(uo.get()));
		
		if(uo.isPresent()) {
			if(bo.isPresent()) {
				if(bo.get().getAdvisories()==0) {
					banRepository.delete(bo.get());
					uo.get().setEnabled(true);
				}
				else {
					bo.get().setBannedDirectly(false);
				}
			}
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User succesfully enabled", request.getRequestURI()), HttpStatus.OK);	
		} else {
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		}
	}
	
	@PostMapping(value = "public/signin")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SignInRequest authenticationRequest, HttpServletResponse response, HttpServletRequest request) throws AuthenticationException, JsonProcessingException {

		//Enable user after 1 week post ban
		//Optional<User> u = userRepository.findByUsername(authenticationRequest.getUsername());
		User u = userRepository.findByUsername(authenticationRequest.getUsername()).get();
		Optional<Ban> bo = banRepository.findById(new BanId(u));
		if(bo.isPresent()) {
			//controllo banned directly
			if(bo.get().isBannedDirectly()) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "You are banned permanently", request.getRequestURI()), HttpStatus.OK);
			if(bo.get().getAdvisories()<6 && bo.get().getBannedUntil().isBefore(LocalDateTime.now())) {
				u.setEnabled(true);
				userRepository.save(u);
			} else {
				if(bo.get().getAdvisories()<6)
					return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
						Instant.now(), 200, "OK", "You are banned until "+bo.get().getBannedUntil(), request.getRequestURI()), HttpStatus.OK);
				if(bo.get().getAdvisories()>=6) return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
						Instant.now(), 200, "OK", "You are banned permanently", request.getRequestURI()), HttpStatus.OK);
			}
		}
		
        // Effettuo l'autenticazione
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Genero Token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        
        //Per aggiungere il token nell'header basta scommentare la riga sotto
        //response.setHeader(tokenHeader,token);
        
        // Ritorno il token
        return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, 
        		new JwtAuthenticationResponse(
        				userDetails.getUsername(),
        				userDetails.getAuthorities(), 
        				token), 
        		request.getRequestURI()), HttpStatus.OK );
		
    }
	
	@PostMapping("public/signup")
	@Transactional
	public ResponseEntity<ApiResponseCustom> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
		
		log.info("Call controller registerUser with SignUpRequest as parameter: {}, {}", signUpRequest.getEmail(), signUpRequest.getUsername());
		
		if(userRepository.existsByUsername(signUpRequest.getUsername())) {
			log.info("Username {} already in use", signUpRequest.getUsername());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 403, null, "Username already in use !", request.getRequestURI()), HttpStatus.BAD_REQUEST);	
		}
		
		if(userRepository.existsByEmail(signUpRequest.getEmail())) {
			log.info("Email {} already in use", signUpRequest.getEmail());
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom( Instant.now(), 403, null, "Email already in use !", request.getRequestURI()), HttpStatus.BAD_REQUEST);
		}
		
		User user = new User(signUpRequest.getEmail(), signUpRequest.getUsername(),  signUpRequest.getPassword());
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		
		Authority userAuthority = authorityRepository.findByName(AuthorityName.ROLE_READER)
        .orElseThrow(() -> new AppException("User Authority not set."));

		user.setAuthorities(Collections.singleton(userAuthority));
		
		log.info("User creation successfully completed", signUpRequest.getEmail());
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "User creation successfully completed", request.getRequestURI()), HttpStatus.OK );
		
	}
	
	@PutMapping("private/update-authority")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponseCustom> updateAuthority(@Valid @RequestBody ChangeAuthority changeAuthority, HttpServletRequest request) throws EnumConstantNotPresentException{
		
		Optional<User> u = userRepository.findByUsername(changeAuthority.getUsername());
		
		if(!u.isPresent())
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		
		Set<AuthorityName> authorityNames = changeAuthority.getAuthorityNames();
		
		if(authorityNames.isEmpty())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "No authorities have been selected", request.getRequestURI()), HttpStatus.OK);
		
		Set<Authority> authorities = authorityRepository.findByNameIn(authorityNames);
		
		u.get().setAuthorities(authorities);
		
		userRepository.save(u.get());		
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom(Instant.now(), 200, null, "Authorities have been updated", request.getRequestURI()), HttpStatus.OK );
		
	}
	
	
	
	
	@PutMapping("public/forgot-password")
	public ResponseEntity<ApiResponseCustom> forgotPassword(@RequestParam String usernameOrEmail, HttpServletRequest request){
		
		Optional<User> u = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
		
		if(!u.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "User not found", request.getRequestURI()), HttpStatus.OK);
		
		/*
		if(u.get().getIdentifierCode() != null)
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "You have already requested a change password", request.getRequestURI()), HttpStatus.OK);
		*/		
		
		String identifierCode = null;
		
		try {
			identifierCode = UserService.toHexString(UserService.getSHA(Instant.now().toString()));
			u.get().setIdentifierCode(identifierCode);
			userRepository.save(u.get());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Please, check your email in order to reset the password: http://localhost:8081/public/reset-password/"+identifierCode, request.getRequestURI()), HttpStatus.OK);
	}
	
	@PutMapping("public/reset-password/{identifierCode}")
	public ResponseEntity<ApiResponseCustom> resetPassword(@PathVariable String identifierCode, @RequestParam String newPassword, HttpServletRequest request){
		
		Optional<User> u = userRepository.findByIdentifierCode(identifierCode);
		
		if(!u.isPresent())
			return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
					Instant.now(), 200, "OK", "Identifier code not found", request.getRequestURI()), HttpStatus.OK);
		
		u.get().setPassword(passwordEncoder.encode(newPassword));
		u.get().setIdentifierCode(null);
		userRepository.save(u.get());
		
		return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
				Instant.now(), 200, "OK", "Password has been modified", request.getRequestURI()), HttpStatus.OK);
	}
	
	
	
	
	
	@PutMapping("private/update-avatar")
	@Transactional
	public ResponseEntity<ApiResponseCustom> updateAvatar(
			@RequestPart("avatarImage") MultipartFile avatarImage,
			HttpServletRequest request)throws Exception{
		
		
		DBFile file = null;
		if (!avatarImage.isEmpty()) {
			
			// Returns DBFile from request
			file = dbFileService.fromMultiToDBFile(avatarImage);
			
			// Transforms Multipart file in a buffered image in order to control height and width
			BufferedImage image = dbFileService.getBufferedImage(avatarImage);	
			
			if(dbFileService.ctrlImageSizeAvatar(image) != null) {
					return new ResponseEntity<ApiResponseCustom>(
						new ApiResponseCustom(Instant.now(), 401, null, dbFileService.ctrlImageSizeAvatar(image) , request.getRequestURI()),
						HttpStatus.FORBIDDEN);	
			}
			
			// salva nel database
			dbFileRepository.save(file);
		} else {
			return new ResponseEntity<ApiResponseCustom>(
					new ApiResponseCustom(Instant.now(), 401, null,"The image cannot be null", request.getRequestURI()),
					HttpStatus.FORBIDDEN);	
		}
		
		// cerca l'utente
		JwtUser jwtUser = userService.getAuthenticatedUser();
		Optional<User> u = userRepository.findByUsername(jwtUser.getUsername());
		
        //u.get().setDbFile(file);
        
		if(u.get().getDbFile()!= null) {
			Optional<DBFile> oldDBFile = dbFileRepository.findById(u.get().getDbFile().getId());

			if(oldDBFile.isPresent()) 
				file = dbFileService.copyImageContent(oldDBFile.get(), file);
		} 
		u.get().setDbFile(file);

		//save (update) user and avatar
		dbFileRepository.save(file);
		userRepository.save(u.get());
	
	
	return new ResponseEntity<ApiResponseCustom>(new ApiResponseCustom (
			Instant.now(), 200, "OK", "Avatar has been update", request.getRequestURI()), HttpStatus.OK);
}
		
		
}
