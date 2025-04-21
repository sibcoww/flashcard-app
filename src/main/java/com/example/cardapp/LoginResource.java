package com.example.cardapp;

import com.example.cardapp.dto.RegisterRequest;
import com.example.cardapp.model.User;
import com.example.cardapp.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Path("/login")
public class LoginResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password are required.").build();
        }

        EntityManager em = JPAUtil.getEntityManager();
        User user;
        try {
            user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", request.getUsername())
                    .getSingleResult();
        } catch (NoResultException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("User not found.").build();
        } finally {
            em.close();
        }

        String hash = hashPassword(request.getPassword());
        if (!user.getPasswordHash().equals(hash)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid password.").build();
        }

        return Response.ok("Login successful").build();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }
}
