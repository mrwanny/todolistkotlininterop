package io.swagger.api;

import io.swagger.model.CompletedTask;
import io.swagger.model.Task;
import io.swagger.model.TodoList;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ListApiController implements ListApi {

    private static final Logger log = LoggerFactory.getLogger(ListApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ListApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> addTask(@ApiParam(value = "Unique identifier of the list to add the task for",required=true) @PathVariable("id") UUID id,@ApiParam(value = "task to add"  )  @Valid @RequestBody Task task) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<TodoList> getList(@ApiParam(value = "The unique identifier of the list",required=true) @PathVariable("id") UUID id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<TodoList>(objectMapper.readValue("{  \"name\" : \"Home\",  \"description\" : \"The list of things that need to be done at home\n\",  \"id\" : \"d290f1ee-6c54-4b01-90e6-d701748f0851\",  \"tasks\" : [ {    \"name\" : \"mow the yard\",    \"id\" : \"0e2ac84f-f723-4f24-878b-44e63e7ae580\",    \"completed\" : true  }, {    \"name\" : \"mow the yard\",    \"id\" : \"0e2ac84f-f723-4f24-878b-44e63e7ae580\",    \"completed\" : true  } ]}", TodoList.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<TodoList>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<TodoList>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> putTask(@ApiParam(value = "Unique identifier of the list to add the task for",required=true) @PathVariable("id") UUID id,@ApiParam(value = "Unique identifier task to complete",required=true) @PathVariable("taskId") UUID taskId,@ApiParam(value = "task to add"  )  @Valid @RequestBody CompletedTask task) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

}
