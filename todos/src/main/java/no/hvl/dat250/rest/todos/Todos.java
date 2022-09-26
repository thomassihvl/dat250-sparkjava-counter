package no.hvl.dat250.rest.todos;

import java.util.ArrayList;
import com.google.gson.Gson;

public class Todos {

    private ArrayList<Todo> todos;

    public Todos() {
        this.todos = new ArrayList<Todo>();
    }

    public Todos(Todo todo) {
        this.todos.add(todo);
    }

    public ArrayList<Todo> getTodos() {
        return todos;
    }






    public void setTodos(ArrayList<Todo> todos) {
        this.todos = todos;
    }
}
