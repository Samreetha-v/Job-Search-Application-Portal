package com.job_sap.dto;

import com.job_sap.entity.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private UserDetails user; // We add this field to hold the safe user data

    // New constructor that takes both the token and the User entity
    public AuthResponse(String token, User userEntity) {
        this.setToken(token);
        
        // Map the entity to our safe inner class
        this.setUser(new UserDetails(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getEmail(), 
            userEntity.getRole().name()
        ));
    }

    public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserDetails getUser() {
		return user;
	}

	public void setUser(UserDetails user) {
		this.user = user;
	}

	// Inner class so we don't accidentally send the hashed password to the frontend
    @Data
    public static class UserDetails {
        private Long id;
        private String name;
        private String email;
        private String role;
        

        public UserDetails(Long id, String name, String email, String role) {
            this.setId(id);
            this.name = name;
            this.email = email;
            this.role = role;
        }
        
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getRole() {
			return role;
		}

		public void setRole(String role) {
			this.role = role;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
    }
}