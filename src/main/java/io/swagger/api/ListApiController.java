package io.swagger.api;

import io.swagger.model.CompletedTask;
import io.swagger.model.Task;
import io.swagger.model.TodoList;

import java.util.NoSuchElementException;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import io.swagger.service.TodoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ListApiController implements ListApi {

    private static final Logger log = LoggerFactory.getLogger(ListApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private  TodoService todoService;

    @org.springframework.beans.factory.annotation.Autowired
    public ListApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> addTask(@ApiParam(value = "Unique identifier of the list to add the task for",required=true) @PathVariable("id") UUID id,@ApiParam(value = "task to add"  )  @Valid @RequestBody Task task) {
        String accept = request.getHeader("Accept");
        if (todoService.existsTaskInTodoList(task.getId(),id)){
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }
        try{
            todoService.addTask(id,task);
        } catch (NoSuchElementException e){
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    public ResponseEntity<TodoList> getList(@ApiParam(value = "The unique identifier of the list",required=true) @PathVariable("id") UUID id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TodoList>(todoService.getTodoList(id),HttpStatus.OK);
            } catch (NoSuchElementException e){
                log.debug("oops", e);
                return new ResponseEntity<TodoList>(HttpStatus.NOT_FOUND);
            }

        }

        return new ResponseEntity<TodoList>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> putTask(@ApiParam(value = "Unique identifier of the list to add the task for",required=true) @PathVariable("id") UUID id,@ApiParam(value = "Unique identifier task to complete",required=true) @PathVariable("taskId") UUID taskId,@ApiParam(value = "task to add"  )  @Valid @RequestBody CompletedTask task) {
        String accept = request.getHeader("Accept");
        try{
            todoService.updateTaskCompletion(id,taskId,task.isCompleted());
        } catch (NoSuchElementException e){
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
