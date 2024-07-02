package com.kunal.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kunal.crm.entity.UrlMapping;
import com.kunal.crm.repository.UrlMappingRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository urlMappingRepository;

    public String shortenUrl(String originalUrl) {
    	 String shortCode = generateShortCode(originalUrl);

         // Save to database
         UrlMapping urlMapping = new UrlMapping();
         urlMapping.setOriginalUrl(originalUrl);
         urlMapping.setShortCode(shortCode);
         urlMapping.setCreatedAt(LocalDateTime.now());
         urlMapping.setExpiryAt(LocalDateTime.now().plusMonths(10));

         urlMappingRepository.save(urlMapping);

         return shortCode;
    }

    public Optional<String> getOriginalUrl(String shortCode) {
        Optional<UrlMapping> urlMappingOptional = urlMappingRepository.findByShortCode(shortCode);
        return urlMappingOptional.map(UrlMapping::getOriginalUrl);
    }

    private String generateShortCode(String originalUrl) {
    	try {
            // Create SHA-256 hash of the original URL
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));

            // Encode hash bytes to base64 (or any other encoding)
            String base64Hash = Base64.getUrlEncoder().encodeToString(hashBytes);

            
            return base64Hash.substring(0, 8); //take first 8 characters
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateOriginalUrl(String shortCode, String newOriginalUrl) {
        Optional<UrlMapping> urlMappingOptional = urlMappingRepository.findByShortCode(shortCode);
        if (urlMappingOptional.isPresent()) {
            UrlMapping urlMapping = urlMappingOptional.get();
            urlMapping.setOriginalUrl(newOriginalUrl);
            urlMappingRepository.save(urlMapping);
            return true;
        }
        return false;
    }

    public boolean updateExpiry(String shortCode, int daysToAdd) {
        Optional<UrlMapping> urlMappingOptional = urlMappingRepository.findByShortCode(shortCode);
        if (urlMappingOptional.isPresent()) {
            UrlMapping urlMapping = urlMappingOptional.get();
            urlMapping.setExpiryAt(urlMapping.getExpiryAt().plusDays(daysToAdd));
            urlMappingRepository.save(urlMapping);
            return true;
        }
        return false;
    }
    
}
