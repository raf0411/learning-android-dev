package org.example

import java.util.UUID
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

data class Task(
    val id: UUID,
    val title: String?,
    val deadline: LocalDateTime?, // e.g, 2 May 2025 15:05
    val priority: Priority?
)

enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

fun addTask(title: String, deadline: LocalDateTime?, priority: String, tasks: ArrayList<Task>){
    val convertedPriority = when (priority){
        "HIGH" -> Priority.HIGH
        "MEDIUM" -> Priority.MEDIUM
        "LOW" -> Priority.LOW
        else -> null
    }

    val newId = UUID.randomUUID()

    val newTask = Task(
        id = newId,
        title = title,
        deadline = deadline,
        priority = convertedPriority,
    )

    tasks.add(newTask)
}

fun removeTask(id: UUID, tasks: ArrayList<Task>){
    tasks.removeIf { it -> it.id == id }
}

fun showAllTask(tasks: ArrayList<Task>){

    if (tasks.isEmpty()){
        println("===================================")
        println("======== Your Tasks (0) ===========")
        println("===================================")
        println("You currently have no tasks!")
        print("Press enter to continue.")
        readln()
        return
    }

    val idWidth = 5
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

fun displayAddTaskMenu(tasks: ArrayList<Task>){
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
            addTask(title, formatDate(deadline), priority, tasks)
            println("Task added successfully!")
        }

        job.join()
    }

    print("Press enter to continue.")
    readln()
}

fun displayRemoveTaskMenu(tasks: ArrayList<Task>){

    if (tasks.isEmpty()){
        println("===================================")
        println("========== Remove Task ============")
        println("===================================")
        println("You currently have no tasks!")
        print("Press enter to continue.")
        readln()
        return
    }

    lateinit var id: UUID

    showAllTask(tasks)
    println("===================================")
    println("========== Remove Task ============")
    println("===================================")

    do {
        print("Enter task id to delete [Case Sensitive]: ")
        id = UUID.fromString(readln())

        val hasId = tasks.any{ item -> item.id == id}
    } while (hasId != true)

    runBlocking {
        println("Removing task, please wait!")
        delay(5000L)

        val job = launch {
            removeTask(id, tasks)
            println("Task added successfully!")
        }

        job.join()
    }

    print("Press enter to continue.")
    readln()
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

fun main(){
    val tasks: ArrayList<Task> = ArrayList<Task>()

    // Main Program Loop
    var opt = -1

    do {
        displayMainMenu()
        opt = readln().toInt()

        when (opt){
            1 -> { displayAddTaskMenu(tasks) }
            2 -> { displayRemoveTaskMenu(tasks) }
            3 -> { showAllTask(tasks) }
        }

    } while (opt != 0)
}
