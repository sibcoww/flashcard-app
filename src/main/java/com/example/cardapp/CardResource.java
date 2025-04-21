package com.example.cardapp;

import com.example.cardapp.model.Card;
import com.example.cardapp.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/cards")
public class CardResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> getAllCards() {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Card> query = em.createQuery("SELECT c FROM Card c", Card.class);
        List<Card> cards = query.getResultList();
        em.close();
        return cards;
    }
    @DELETE
    @Path("/{id}")
    public Response deleteCard(@PathParam("id") Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Card card = em.find(Card.class, id);
            if (card == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Карточка не найдена").build();
            }
            em.remove(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
            return Response.serverError().entity("Ошибка при удалении: " + e.getMessage()).build();
        } finally {
            em.close();
        }

        return Response.ok("Карточка удалена").build();
    }

    @GET
    @Path("/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> getCardsByCategory(@PathParam("category") String category) {
        EntityManager em = JPAUtil.getEntityManager();
        TypedQuery<Card> query = em.createQuery("SELECT c FROM Card c WHERE c.category = :category", Card.class);
        query.setParameter("category", category);
        List<Card> cards = query.getResultList();
        em.close();
        return cards;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCard(Card card) {
        // Проверка на пустоту и null
        if (card.getQuestion() == null || card.getQuestion().isBlank() ||
                card.getAnswer() == null || card.getAnswer().isBlank() ||
                card.getCategory() == null || card.getCategory().isBlank() ||
                card.getCreatedByUsername() == null || card.getCreatedByUsername().isBlank()) {

            System.out.println("⛔ Ошибка: одно из полей пустое или null");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Все поля обязательны к заполнению").build();
        }

        // Вывод полей карточки
        System.out.println("🎯 Card DTO перед сохранением:");
        System.out.println("→ Вопрос: " + card.getQuestion());
        System.out.println("→ Ответ: " + card.getAnswer());
        System.out.println("→ Категория: " + card.getCategory());
        System.out.println("→ Пользователь: " + card.getCreatedByUsername());

        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(card);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("🔥 Ошибка при сохранении карточки:");
            e.printStackTrace(); // Подробный стек трейс в консоль
            em.getTransaction().rollback();
            return Response.serverError()
                    .entity("Ошибка БД: " + e.getClass().getSimpleName() + " - " + e.getMessage())
                    .build();
        } finally {
            em.close();
        }

        System.out.println("✅ Карточка успешно сохранена в БД!");

        return Response.status(Response.Status.CREATED).build();
    }
    @GET
    @Path("/categories")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllCategories() {
        EntityManager em = JPAUtil.getEntityManager();
        List<String> categories = em.createQuery(
                "SELECT DISTINCT c.category FROM Card c", String.class
        ).getResultList();
        em.close();
        return categories;
    }

}
