package com.example.ipl.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    // Only one of these will be set per request
    @Size(min = 3, max = 50, message = "New username must be between 3 and 50 characters")
    private String newUsername;

    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getNewUsername() { return newUsername; }
    public void setNewUsername(String newUsername) { this.newUsername = newUsername; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
