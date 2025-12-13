package com.example.ipl.controller;

import com.example.ipl.model.Matches;
import com.example.ipl.model.User;
import com.example.ipl.model.WinnerRequest;
import com.example.ipl.repositories.MatchesRepository;
import com.example.ipl.repositories.UserRepository;
import com.example.ipl.services.UserService;
import com.example.ipl.services.WinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MatchesRepository matchesRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private WinnerService winnerService;

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Add new match
    @PostMapping("/matches")
    public ResponseEntity<?> addMatch(@Valid @RequestBody Matches match) {
        try {
            Matches savedMatch = matchesRepository.save(match);
            return ResponseEntity.ok(savedMatch);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to add match"));
        }
    }

    // Get all matches
    @GetMapping("/matches")
    public List<Matches> getAllMatches() {
        return matchesRepository.findAll();
    }

    // Reset user password
    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            User user = userOpt.get();
            String newPassword = request.get("newPassword");
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setAccountLocked(false);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to reset password"));
        }
    }

    // Delete match
    @DeleteMapping("/matches/{matchId}")
    public ResponseEntity<?> deleteMatch(@PathVariable Long matchId) {
        try {
            matchesRepository.deleteById(matchId);
            return ResponseEntity.ok(Map.of("message", "Match deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete match"));
        }
    }
    
    // Admin add user
    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
            }
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to add user"));
        }
    }
    
    // Set match winner (Admin only)
    @PostMapping("/set-winner")
    @Transactional
    public ResponseEntity<?> setWinner(@RequestBody WinnerRequest winnerRequest) {
        try {
            // Validate match exists
            Optional<Matches> matchOpt = matchesRepository.findById(winnerRequest.getMatch_id());
            if (matchOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Match not found"));
            }
            
            Matches match = matchOpt.get();
            
            // Validate winner team is one of the match teams
            if (!winnerRequest.getWinnerTeam().equals(match.getTeam1()) &&
                !winnerRequest.getWinnerTeam().equals(match.getTeam2())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Winner must be one of the match teams"));
            }
            
            // Check if winner already set
            if (match.getWinner() != null && !match.getWinner().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Winner already set for this match"));
            }
            
            // Set winner and save winner records
            winnerService.save_winnerTeam(winnerRequest);
            winnerService.setWinnerFields(winnerRequest);
            
            return ResponseEntity.ok(Map.of("message", "Winner set successfully", "winner", winnerRequest.getWinnerTeam()));
        } catch (Exception e) {
            log.error("Error setting winner: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to set winner"));
        }
    }
}