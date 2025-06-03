package org.example

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import kotlinx.coroutines.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

// 1. Create a Task Repository
// 2. Create a get data from json file (check if not exists create one)
// 3. Put all CRUD stuff to Repository
// 4. All argument for CRUD stuff must take from getTasks() -> which is from json file

data class Task(
    val id: UUID,
    val title: String?,
    val deadline: String?, // e.g, 2 May 2025 15:05
    val priority: Priority?
)

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

class TaskRepository {
    val tasks: ArrayList<Task> = ArrayList()

    fun addTask(task: Task){

    }

    fun removeTask(task: Task){

    }

    fun getTasks(){
        // Create JSON file

    }
}

fun addTask(title: String, deadline: LocalDateTime?, priority: String){
    val convertedPriority = when (priority.uppercase()){
        "HIGH" -> Priority.HIGH
        "MEDIUM" -> Priority.MEDIUM
        "LOW" -> Priority.LOW
        else -> null
    }

    val newId = UUID.randomUUID()

    val newTask = Task(
        id = newId,
        title = title,
        deadline = deadline.toString(),
        priority = convertedPriority,
    )

    persistTask(newTask)
}

fun removeTask(id: UUID){
    val taskIndex = getTaskArrayList().indexOfFirst { it.id == id }

    if (taskIndex != -1) {
        getTaskArrayList().removeAt(taskIndex)
    } else {
        println("Error: Task with ID '$id' not found in the list.")
    }
}

fun showAllTask(){
    val tasks = getTaskArrayList()

    if (tasks.isEmpty()){
        println("===================================")
        println("======== Your Tasks (0) ===========")
        println("===================================")
        println("You currently have no tasks!")
        print("Press enter to continue.")
        readln()
        return
    }

    val idWidth = 50
    val titleWidth = 25
    val deadlineWidth = 15
    val priorityWidth = 10

    fun String.padEndFixed(length: Int, padChar: Char = ' '): String {
        return this.padEnd(length, padChar).take(length)
    }

    fun Int.padEndFixed(length: Int, padChar: Char = ' '): String {
        return this.toString().padEnd(length, padChar).take(length)
    }

    val header = "| ${"ID".padEndFixed(idWidth)} | ${"Title".padEndFixed(titleWidth)} | ${"Deadline".padEndFixed(deadlineWidth)} | ${"Priority".padEndFixed(priorityWidth)} |"
    val separator = "-".repeat(header.length)

    println("===================================")
    println("======== Your Tasks (${tasks.size}) ===========")
    println("===================================")

    println(separator)
    println(header)
    println(separator)

    for (task in tasks) {
        val row = "| ${(task.id).toString().padEndFixed(idWidth)} | ${task.title?.padEndFixed(titleWidth)} | ${(task.deadline?.toString()
            ?.padEndFixed(deadlineWidth))} | ${(task.priority).toString().padEndFixed(priorityWidth)} |"
        println(row)
    }

    println(separator)

    print("Press enter to continue.")
    readln()
}

fun displayAddTaskMenu(){
    lateinit var title: String
    lateinit var deadline: String
    lateinit var priority: String

    println("===================================")
    println("============ Add Task =============")
    println("===================================")

    do {
        print("Enter task title [e.g, 'Try not to be a failure.' | min. 5 characters]: ")
        title = readln()

    } while (title.length < 5)

    do {
        print("Enter the deadline [e.g, 2 May 2025 15:05:00]: ")
        deadline = readlnOrNull().toString()
        val isDeadline = formatDate(deadline) != null

    } while (isDeadline == false)

    do {
        print("Enter the priority level [HIGH | MEDIUM | LOW]: ")
        priority = readln()

        var isPriority = false

        if(priority.equals("HIGH", ignoreCase = true) || priority.equals("MEDIUM", ignoreCase = true) || priority.equals("LOW", ignoreCase = true)){
            isPriority = true
        }

    } while (isPriority == false)

    runBlocking {
        println("Adding task, please wait!")
        delay(5000L)

        val job = launch {
            addTask(title, formatDate(deadline), priority)
            println("Task added successfully!")
        }

        job.join()
    }

    print("Press enter to continue.")
    readln()
}

fun displayRemoveTaskMenu() {
    if (getTaskArrayList().isEmpty()) {
        println("===================================")
        println("========== Remove Task ============")
        println("===================================")
        println("You currently have no tasks!")
        print("Press enter to continue.")
        readln()
        return
    }

    showAllTask()
    println("===================================")
    println("========== Remove Task ============")
    println("===================================")

    var taskSuccessfullyRemoved = false

    do {
        print("Enter task id to delete [Case Sensitive] (or type 'exit' to cancel): ")
        val userInputId = readlnOrNull()

        if (userInputId == null || userInputId.equals("exit", ignoreCase = true)) {
            println("Task removal cancelled or no input received.")
            break
        }

        try {
            val idToRemove = UUID.fromString(userInputId)
            removeTask(idToRemove)
            taskSuccessfullyRemoved = true

        } catch (_: IllegalArgumentException) {
            println("Invalid UUID string entered: '$userInputId'.")

        }
    } while (!taskSuccessfullyRemoved)

    runBlocking {
        println("Removing task, please wait!")
        delay(5000L)

        val job = launch {
            println("Task added successfully!")
        }

        job.join()
    }

    if (taskSuccessfullyRemoved) {
        print("Press enter to continue.")
        readln()
    }
}

fun displayMainMenu(){
    println("===================================")
    println("=== Lazy Student Task Automator ===")
    println("===================================")
    println("============ Main Menu ============")
    println("===================================")
    println("1. Add a task")
    println("2. Remove a task")
    println("3. View all task")
    println("0. Exit Program")
    println("===================================")
    print(">> ")
}

fun sayGoodbye(){
    println("====================================")
    println("==== GOODBYE! WE'LL MISS YOU 😔 ====")
    println("====================================")
}

fun formatDate(date: String): LocalDateTime? {
    return try {
        val inputPattern = "d MMMM yyyy HH:mm:ss"
        val inputFormatter = DateTimeFormatter.ofPattern(inputPattern, Locale.ENGLISH)

        val parsedDate = LocalDateTime.parse(date, inputFormatter)
        parsedDate

    } catch (e: DateTimeParseException) {
        println("Invalid date format. Please use the format 'd MMMM yyyy HH:mm:ss' (e.g., 2 May 2025 15:05:00).")
        null

    } catch (e: Exception){
        e.printStackTrace()
        null

    }
}

fun persistTask(task: Task){
    val file = File("tasks.json")
    var tasks: ArrayList<Task>? = ArrayList<Task>()
    val gson = Gson()

    if (file.exists() && file.length() > 0) {
        try {
            FileReader(file).use { reader ->
                val listType = object : TypeToken<ArrayList<Task>>() {}.type

                val existingTasks: ArrayList<Task>? = gson.fromJson(reader, listType)
                if (existingTasks != null) {
                    tasks = existingTasks
                }
            }
        } catch (e: Exception) {
            println("Error reading or parsing tasks.json, will start with a new list. Error: ${e.message}")
            tasks = ArrayList()
        }
    }

    tasks?.add(task)

    try {
        FileWriter(file).use { writer ->
            gson.toJson(tasks, writer)
        }
    } catch (e: IOException) {
        println("Error writing updated task list to tasks.json: ${e.message}")
    }
}

fun getTaskArrayList(): ArrayList<Task>{
    val file = File("tasks.json")
    val gson = Gson()
    var taskList: ArrayList<Task> = ArrayList()

    if (file.exists() && file.length() > 0) {
        try {
            FileReader(file).use { reader ->
                val listType = object : TypeToken<ArrayList<Task>>() {}.type
                taskList = gson.fromJson(reader, listType)
            }
        } catch (e: IOException) {
            println("Error reading tasks.json: ${e.message}")
        } catch (e: com.google.gson.JsonSyntaxException) {
            println("Error parsing JSON from tasks.json: ${e.message}")
        }
    } else {
        println("tasks.json does not exist or is empty.")
    }

    return taskList
}

fun main(){
    // Main Program Loop
    var opt = -1

    do {
        displayMainMenu()
        opt = readln().toInt()

        when (opt){
            1 -> { displayAddTaskMenu() }
            2 -> { displayRemoveTaskMenu() }
            3 -> { showAllTask() }
            0 -> { sayGoodbye() }
            else -> { println("=== ERROR ===") }
        }

    } while (opt != 0)
}
