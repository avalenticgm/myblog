package it.course.myblog.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import it.course.myblog.security.JwtUser;


@Service
public class UserService {
	
	// Recover from Spring Security Context the User logged in
	public JwtUser getAuthenticatedUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return (JwtUser) authentication.getPrincipal();
	}
	
	
	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {	
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	} 
	
	
	public static String toHexString(byte[] hash) {
		
		BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));
		
		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}
		
		return hexString.toString().toUpperCase();
		
	}

}
