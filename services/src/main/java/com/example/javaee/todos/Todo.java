package com.example.javaee.todos;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.beans.ConstructorProperties;

@Embeddable
@Access(AccessType.FIELD)
public class Todo {

    @NotNull
    private long id;

    @NotNull
    @Size(min = 1)
    private String title;
    private long order;
    private boolean completed;

    public Todo() {

    }

    public Todo(long id, String title, long order, boolean completed) {
        this.id = id;
        this.title = title;
        this.order = order;
        this.completed = completed;
    }

    public static TodoBuilder aTodo() {
        return new TodoBuilder();
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getOrder() {
        return order;
    }

    public boolean isCompleted() {
        return completed;
    }

    public static class TodoBuilder {
        private long id;
        private String title;
        private long order;
        private boolean completed;

        private TodoBuilder() {

        }

        public TodoBuilder withId(long id) {
            this.id = id;
            return this;
        }

        public TodoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public TodoBuilder withOrder(long order) {
            this.order = order;
            return this;
        }

        public TodoBuilder isCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public Todo build() {
            return new Todo(id, title, order, completed);
        }
    }
}