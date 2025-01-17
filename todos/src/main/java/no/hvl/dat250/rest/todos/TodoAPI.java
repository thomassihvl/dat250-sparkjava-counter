package no.hvl.dat250.rest.todos;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import static spark.Spark.*;

import spark.Filter;
import spark.Request;
import spark.Response;

/**
 * Rest-Endpoint.
 */
public class TodoAPI {

    static ArrayList<Todo> todos;
    // Simplistic ID system
    static int idCounter;

    private static Todo getTodo(Long todoId) {
        Todo todo = todos.stream()
                .filter(t -> todoId.equals(t.getId()))
                .findAny()
                .orElse(null);
        return todo;
    };

    private static Boolean isNumeric(String num) {
        try {
            Long.parseLong(num);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    public static void main(String[] args) {

        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        todos = new ArrayList<Todo>();
        idCounter = 0;

        options("/*",
                (request, response) -> {

                    String accessControlRequestHeaders = request
                            .headers("Access-Control-Request-Headers");
                    if (accessControlRequestHeaders != null) {
                        response.header("Access-Control-Allow-Headers",
                                accessControlRequestHeaders);
                    }

                    String accessControlRequestMethod = request
                            .headers("Access-Control-Request-Method");
                    if (accessControlRequestMethod != null) {
                        response.header("Access-Control-Allow-Methods",
                                accessControlRequestMethod);
                    }

                    return "OK";
                });

        after((req, res) -> {
            res.type("application/json");
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            //res.header("Access-Control-Allow-Methods", "POST");
            //res.header("Access-Control-Allow-Methods", "PUT");
            //res.header("Access-Control-Allow-Methods", "DELETE");
        });


        get("/todos", (req, res) -> {
            Gson gson = new Gson();
            String todosJson = gson.toJson(todos);
            return todosJson;
        });
        get("/todos/:id", (req, res) -> {
            String recievedId = req.params(":id");
            if (!(isNumeric(recievedId))) {

                String errorMessage = "The id \""+recievedId+"\" is not a number!";
                return errorMessage;
            }

            Long id = Long.parseLong(recievedId);
            Todo todo = getTodo(id);
            if (todo != null) {
                Gson gson = new Gson();
                String todoJson = gson.toJson(todo);
                return todoJson;
            }else {
                return "Todo with the id  \""+id+"\" not found!";
            }
        });

        post("/todos", (req, res) -> {
            Gson gson = new Gson();

            String idRow = "id:"+idCounter;
            idCounter+=1;
            String todoJson = "{\r\n    id: "+idCounter+",\r"+req.body().substring(1);

            //System.out.println(todoJson);
            Todo newTodo = gson.fromJson(todoJson, Todo.class);
            todos.add(newTodo);

            Todo retrievedTodo = getTodo(newTodo.getId());

            return retrievedTodo.toJson();
        });

        put("/todos/:id", (req, res) -> {
            Gson gson = new Gson();

            // Extract id
            String recievedId = req.params(":id");

            // Check if id is numerical
            if (!(isNumeric(recievedId))) {

                String errorMessage = "The id \""+recievedId+"\" is not a number!";
                return errorMessage;
            }


            // Convert and try to find existing object
            Long id = Long.parseLong(recievedId);
            Todo todo = getTodo(id);

            // Remove old todo
            if (todo != null) {
                todos.remove(todo);
            }

            // add new todo
            String newTodoJson = "{id:"+id+",\n"+req.body().substring(1);
            Todo newTodo = gson.fromJson(newTodoJson, Todo.class);
            todos.add(newTodo);

            // Find and return new todo
            Todo retrievedTodo = getTodo(id);

            return retrievedTodo.toJson();
        });

        delete("/todos/:id", (req, res) -> {
            Gson gson = new Gson();

            // Extract id
            String recievedId = req.params(":id");

            // Check if id is numerical
            if (!(isNumeric(recievedId))) {

                String errorMessage = "The id \""+recievedId+"\" is not a number!";
                return errorMessage;
            }


            // Convert and try to find existing object
            Long id = Long.parseLong(recievedId);
            Todo todo = getTodo(id);

            // Remove old todo
            if (todo != null) {
                todos.remove(todo);
            }
            return todo.toJson();
        });
    }
}
