package com.kunal.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kunal.crm.service.UrlShortenerService;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/url")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody Map<String, String> requestBody) {
        String originalUrl = requestBody.get("originalUrl");
        String shortCode = urlShortenerService.shortenUrl(originalUrl);
        return ResponseEntity.ok(shortCode);
    }

    @GetMapping("/{shortCode}")
    public void redirectToOriginalUrl(HttpServletResponse response, @PathVariable("shortCode") String shortCode) throws IOException {
        Optional<String> originalUrlOptional = urlShortenerService.getOriginalUrl(shortCode);
        if (originalUrlOptional.isPresent()) {
            response.sendRedirect(originalUrlOptional.get());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "URL not found");
        }
    }

    @PostMapping("/{shortCode}/update")
    public ResponseEntity<Void> updateOriginalUrl(@PathVariable("shortCode") String shortCode, @RequestBody String newOriginalUrl) {
        if (urlShortenerService.updateOriginalUrl(shortCode, newOriginalUrl)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{shortCode}/updateexpiry")
    public ResponseEntity<Void> updateExpiry(@PathVariable("shortCode") String shortCode, @RequestParam int daysToAdd) {
        if (urlShortenerService.updateExpiry(shortCode, daysToAdd)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
