package org.example

import java.io.IOException
import java.util.UUID
import kotlinx.coroutines.*

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

val tasks: ArrayList<Task> = emptyList<Task>() as ArrayList<Task>

fun clearConsole() {
    try {
        if (System.getProperty("os.name").contains("Windows")) {
            ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
        } else {
            print("\u001b[H\u001b[2J")
            System.out.flush()
        }
    } catch (e: IOException) {
        println("Error clearing console: ${e.message}")
    } catch (e: InterruptedException) {
        println("Error clearing console: ${e.message}")
    }
}

fun displayMainMenu(){
    println("===================================")
    println("=== Lazy Student Task Automator ===")
    println("===================================")
    println("============ Main Menu ============")
    println("Press one of the numbers below!")
    println("1. Add a task")
    println("2. Remove a task")
    println("3. View all task")
    println("0. Exit Program")
    println("===================================")
    print(">> ")
}

fun addTask(title: String, deadline: String, priority: String){
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

fun removeTask(id: UUID){
    tasks.removeIf { it -> it.id == id }
}

fun showAllTask(){
    for (task in tasks){
        
    }
}

fun displayAddTaskMenu(){
    lateinit var title: String
    lateinit var deadline: String
    lateinit var priority: String

    println("===================================")
    println("=== Lazy Student Task Automator ===")
    println("===================================")
    println("============ Add Task ============")
    println("===================================")

    do {
        print("Enter task title [e.g, 'Try not to be a failure.' | min. 5 characters]: ")
        title = readln()
    } while (title.length < 5)

    do {
        print("Enter the deadline [e.g, 2 May 2025 15:05:00]: ")
        deadline = readln()

        // TODO How to compare formatted date with string ?
    } while (deadline.length < 5)

    do {
        print("Enter the priority level [HIGH | MEDIUM | LOW]: ")
        priority = readln()
    } while (
        priority.equals("HIGH", ignoreCase = true) ||
        priority.equals("MEDIUM", ignoreCase = true) ||
        priority.equals("LOW", ignoreCase = true)
    )

    runBlocking {
        launch {
            println("Adding task, please wait!")
            addTask(title, deadline, priority)
            delay(5000L)
        }
        println("Task added successfully!")
        delay(2000L)
    }
}

fun displayRemoveTaskMenu(){
    lateinit var id: UUID

    println("===================================")
    println("=== Lazy Student Task Automator ===")
    println("===================================")
    println("============ Remove Task ============")
    println("===================================")

    // TODO Display All Task in a table (with id)

    do {
        print("Enter task title [e.g, 'Try not to be a failure.' | min. 5 characters]: ")
        id = UUID.fromString(readln())

        // TODO Check if ID exists or not in table
    } while (id == null)

    runBlocking {
        launch {
            println("Removing task, please wait!")
            removeTask(id)
            delay(5000L)
        }
        println("Task added successfully!")
        delay(2000L)
    }
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
            3 -> { /* TODO Show All Task */}
        }

        clearConsole()

    } while (opt != 0)
}

