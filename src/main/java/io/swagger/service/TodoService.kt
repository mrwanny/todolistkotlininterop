package io.swagger.service

import io.swagger.model.Task
import io.swagger.model.TodoList
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*
import org.apache.tinkerpop.gremlin.structure.Graph
import org.apache.tinkerpop.gremlin.structure.T
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph

private val logger = KotlinLogging.logger {}

@Service
class TodoService {
    companion object {
        val graph: Graph = TinkerGraph.open()
    }

    fun existsTodoList(id: UUID): Boolean{
        return graph.traversal().V().hasLabel("TodoList").has("id", id).hasNext()
    }

    fun existsTaskInTodoList(taskId: UUID, id: UUID): Boolean{
        return graph.traversal().V().hasLabel("TodoList").has("id", id).outE("tasks").otherV().has("id",taskId).hasNext()
    }


    fun getTodoList(id: UUID): TodoList? {

        var todoList = TodoList()
        val vtodoList = graph.traversal().V().hasLabel("TodoList").has("id", id).next()
        if (vtodoList == null) {
            throw Exception("Cannot Find TodoList")
        } else {
            todoList.id = vtodoList.property<UUID>("id").orElseThrow { Exception("Cannot Find TodoList") }
            todoList.name = vtodoList.property<String>("name").orElseThrow { Exception("Cannot Find TodoList") }
            todoList.description = vtodoList.property<String>("description").orElseThrow { Exception("Cannot Find TodoList") }
            for (vtask in graph.traversal().V().hasLabel("TodoList").has("id", id).outE("tasks").otherV()) {
                var task = Task()
                task.id = vtask.property<UUID>("id").orElseThrow { Exception("Cannot Find TodoList") }
                task.name = vtask.property<String>("name").orElseThrow { Exception("Cannot Find TodoList") }
                task.isCompleted = vtask.property<Boolean>("completed").orElseThrow { Exception("Cannot Find TodoList") }
                todoList.addTasksItem(task)

            }
        }
        return todoList
    }

    fun upsertTodoList(todoList: TodoList) {
        val vtodoList = graph.addVertex(T.label, "TodoList", "id", todoList.id, "name", todoList.name, "description", todoList.description, "todolist",todoList)
        for (task in todoList.tasks){
            val vtask = graph.addVertex(T.label, "task","id", task.id, "name", task.name, "completed", task.isCompleted )
            vtodoList.addEdge("tasks",vtask)
        }

    }

    fun list(skip: Long, limit: Long): List<TodoList?>{
        var list = mutableListOf<TodoList?>()
        for (vtodoList in graph.traversal().V().hasLabel("TodoList").skip(skip).limit(limit)){
            list.add(getTodoList(vtodoList.property<UUID>("id").orElseThrow { Exception("Cannot Find TodoList") }))
        }
        return list
    }

    fun list(searchString: String, skip: Long, limit: Long): List<TodoList?>{
        var list = mutableListOf<TodoList?>()
        for (vtodoList in graph.traversal().V().hasLabel("TodoList").has("name",searchString).skip(skip).limit(limit)){
            list.add(getTodoList(vtodoList.property<UUID>("id").orElseThrow { Exception("Cannot Find TodoList") }))
        }
        return list
    }

    fun addTask(id: UUID, task: Task) {
        val vtodoList = graph.traversal().V().hasLabel("TodoList").has("id", id).next()
        val vtask = graph.addVertex(T.label, "task","id", task.id, "name", task.name, "completed", task.isCompleted )
        vtodoList.addEdge("tasks",vtask)
    }

    fun updateTaskCompletion(id: UUID, taskId: UUID, completed: Boolean) {
        val vtask = graph.traversal().V().hasLabel("TodoList").has("id", id).outE("tasks").otherV().has("id",taskId).next()
        vtask.property("completed", completed)
    }

}