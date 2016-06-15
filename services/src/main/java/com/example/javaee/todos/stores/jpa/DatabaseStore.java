package com.example.javaee.todos.stores.jpa;

import com.example.javaee.todos.Store;
import com.example.javaee.todos.Todo;

import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Alternative
@Stateless
public class DatabaseStore implements Store {

    @PersistenceContext
    EntityManager em;

    @Override
    public Todo create(Todo data) {
        TodoEntity entity = TodoEntity.of(data);
        em.persist(entity);
        em.flush();
        em.refresh(entity);
        return entity.getResource();
    }

    @Override
    public Optional<Todo> get(long id) {
        TodoEntity entity = em.find(TodoEntity.class, id);
        return Optional.ofNullable(entity.getResource());
    }

    @Override
    public List<Todo> getAll() {
        return em.createQuery("select t from TodoEntity t", TodoEntity.class).getResultList()
                .stream().map(e -> e.getResource())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Todo> save(long id, Todo data) {
        Optional<Todo> todoOptional = get(id);
        if (todoOptional.isPresent()) {
            TodoEntity entity = TodoEntity.of(data);
            Todo t = em.merge(entity).getResource();
            return Optional.of(t);
        }

        return Optional.empty();
    }

    @Override
    public boolean remove(long id) {
        Optional<Todo> todoOptional = get(id);
        if (todoOptional.isPresent()) {
            em.remove(TodoEntity.of(todoOptional.get()));
            return true;
        }

        return false;
    }
}
