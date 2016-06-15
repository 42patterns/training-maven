package com.example.javaee.todos.stores;

import com.example.javaee.todos.Store;
import com.example.javaee.todos.Todo;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class InMemoryStoreTest {

    private Store store = new InMemoryStore();
    private final Todo t = Todo.aTodo()
            .withTitle("Test")
            .isCompleted(false).build();


    @Before
    public void clear_store_before_test() {
        store = new InMemoryStore();
    }

    @Test
    public void should_create_todo_record() {
        //when
        Todo storedTodo = store.create(t);

        //then
        List<Todo> all = store.getAll();
        assertThat(all.size(), is(equalTo(1)));
        assertThat(all.get(0), is(storedTodo));
        assertThat(storedTodo.getTitle(), equalTo(t.getTitle()));
        assertThat(storedTodo.getOrder(), equalTo(t.getOrder()));
        assertThat(storedTodo.isCompleted(), equalTo(t.isCompleted()));
    }

    @Test
    public void should_remove_existing_record() {
        //given
        Todo storedTodo = store.create(t);

        //when
        boolean remove = store.remove(storedTodo.getId());

        //then
        assertThat(remove, is(true));
    }

    @Test
    public void should_return_false_for_removing_not_existing_record() {
        //when
        boolean remove = store.remove(-1);

        //then
        assertThat(remove, is(false));
    }

    @Test
    public void should_create_new_record_on_update() {
        //given
        Todo storedTodo = store.create(t);

        Todo newTodo = Todo.aTodo()
                .withId(storedTodo.getId())
                .withTitle("New title")
                .withOrder(storedTodo.getOrder())
                .isCompleted(storedTodo.isCompleted())
                .build();

        Optional<Todo> maybeSaved = store.save(storedTodo.getId(), newTodo);

        assertThat(maybeSaved.isPresent(), equalTo(true));
        assertThat(maybeSaved.get().getId(), not(equalTo(storedTodo.getId())));
        assertThat(maybeSaved.get().getTitle(), not(equalTo(storedTodo.getTitle())));
        assertThat(maybeSaved.get().isCompleted(), equalTo(storedTodo.isCompleted()));
        assertThat(maybeSaved.get().getOrder(), equalTo(storedTodo.getOrder()));
    }

    @Test
    public void should_not_update_not_existing_record() {
        Optional<Todo> maybeSaved = store.save(1, t);

        assertThat(maybeSaved.isPresent(), equalTo(false));
    }

}