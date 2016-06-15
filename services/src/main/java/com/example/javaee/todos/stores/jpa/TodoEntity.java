package com.example.javaee.todos.stores.jpa;

import com.example.javaee.todos.Todo;

import javax.persistence.*;

@Entity
@Table(name = "todos")
public class TodoEntity {

    @Id
    @GeneratedValue
    @Column(name = "todo_id")
    private long id;

    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "todo_id", insertable = false, updatable = false)),
            @AttributeOverride(name = "title", column = @Column(name = "todo_title")),
            @AttributeOverride(name = "order", column = @Column(name = "todo_order")),
            @AttributeOverride(name = "completed", column = @Column(name = "todo_completed"))
    })
    private Todo todo;

    public TodoEntity() {
    }

    private TodoEntity(Todo t) {
        this.todo = t;
    }

    public static TodoEntity of(Todo t) {
        return new TodoEntity(t);
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Todo getResource() {
        return todo;
    }

    public void setResource(Todo t) {
        this.todo = todo;
    }
}
