package com.example.cardapp;

import com.example.cardapp.dto.RegisterRequest;
import com.example.cardapp.model.User;
import com.example.cardapp.util.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;

@Path("/register")
public class UserResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Username and password are required.").build();
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            List<User> existing = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", request.getUsername())
                    .getResultList();

            if (!existing.isEmpty()) {
                tx.rollback();
                return Response.status(Response.Status.CONFLICT)
                        .entity("User already exists.").build();
            }

            String hash = hashPassword(request.getPassword());
            User user = new User(request.getUsername(), hash);

            em.persist(user);
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return Response.serverError().entity("DB error: " + e.getMessage()).build();
        } finally {
            em.close();
        }

        return Response.status(Response.Status.CREATED).build();
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

    @GET
    @Path("/init")
    public String triggerInit() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            User u = new User("init_user", "hashed_pw");
            em.persist(u);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return "Ошибка: " + e.getMessage();
        } finally {
            em.close();
        }

        return "OK!";
    }
}
