package io.swagger.api;

import io.swagger.model.TodoList;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class ListsApiController implements ListsApi {

    private static final Logger log = LoggerFactory.getLogger(ListsApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @Autowired
    private TodoService todoService;

    @org.springframework.beans.factory.annotation.Autowired
    public ListsApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> addList(@ApiParam(value = "ToDo list to add"  )  @Valid @RequestBody TodoList todoList) {
        String accept = request.getHeader("Accept");
        if (todoService.existsTodoList(todoList.getId())){
            return new ResponseEntity<Void>(HttpStatus.CONFLICT);
        }else {
            todoService.upsertTodoList(todoList);
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
    }

    public ResponseEntity<List<TodoList>> searchLists(@ApiParam(value = "pass an optional search string for looking up a list") @Valid @RequestParam(value = "searchString", required = false) String searchString,@Min(0)@ApiParam(value = "number of records to skip for pagination") @Valid @RequestParam(value = "skip", required = false, defaultValue = "0") Integer skip,@Min(0) @Max(50) @ApiParam(value = "maximum number of records to return") @Valid @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                if (searchString != null) {
                    List<TodoList> result = todoService.list(searchString,skip,limit);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }else {
                    List<TodoList> result = todoService.list(skip,limit);
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            } catch (Exception e) {
                return new ResponseEntity<List<TodoList>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<TodoList>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
