package org.example

import java.util.UUID
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileLock
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.time.Duration
import javax.imageio.ImageIO

private val scheduler = Executors.newScheduledThreadPool(1)
val DEADLINE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.ENGLISH)

@Serializable
data class Task(
    val id: String,
    val title: String,
    val deadline: String,
    val priority: Priority?
)

@Serializable
enum class Priority {
    HIGH,
    MEDIUM,
    LOW
}

class TaskRepository(filePath: String) {
    private val json = Json { prettyPrint = true }
    val file = File(filePath)

    init {
        if (!file.exists()){
            file.createNewFile()
            file.writeText("[]")
        }
    }

    private fun <T> withFileLock(block: (MutableList<Task>) -> T): T {
        RandomAccessFile(file, "rw").use { randomAccessFile ->
            randomAccessFile.channel.use { channel ->
                var lock: FileLock? = null
                try {
                    lock = channel.lock()

                    val buffer = ByteBuffer.allocate(channel.size().toInt())
                    channel.read(buffer)
                    buffer.flip()
                    val fileContent = StandardCharsets.UTF_8.decode(buffer).toString()

                    val tasks = if (fileContent.isBlank()) {
                        mutableListOf()
                    } else {
                        Json.decodeFromString<MutableList<Task>>(fileContent)
                    }

                    val result = block(tasks)

                    val listSerializer = ListSerializer(Task.serializer())
                    val jsonString = json.encodeToString(listSerializer, tasks)
                    val jsonBytes = jsonString.toByteArray(StandardCharsets.UTF_8)

                    channel.truncate(0)
                    channel.position(0)
                    channel.write(ByteBuffer.wrap(jsonBytes))

                    return result
                } finally {
                    lock?.release()
                }
            }
        }
    }

    fun addTask(title: String, deadline: LocalDateTime?, priority: String): Task{
        val convertedPriority = when (priority.uppercase()){
            "HIGH" -> Priority.HIGH
            "MEDIUM" -> Priority.MEDIUM
            "LOW" -> Priority.LOW
            else -> null
        }

        val newId = UUID.randomUUID().toString()
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale.ENGLISH)
        val newDeadline = deadline?.format(outputFormatter)

        val task = Task(
            id = newId,
            title = title,
            deadline = newDeadline.toString(),
            priority = convertedPriority,
        )

        scheduleNotificationForTask(task)

        return withFileLock { tasks ->
            tasks.add(task)
            task
        }
    }

    fun getAllTasks(): List<Task>{
        return withFileLock { tasks ->
            tasks.toList()
        }
    }

    fun getTaskById(id: String): Task? {
        return withFileLock { tasks ->
            tasks.find { it.id == id }
        }
    }

    fun removeTask(id: String){
        return withFileLock { tasks ->
            tasks.removeIf { it.id == id }
        }
    }
}

fun displayAllTasks(taskRepository: TaskRepository){
    val tasks = taskRepository.getAllTasks()

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
    val deadlineWidth = 25
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
        val row = "| ${(task.id).toString().padEndFixed(idWidth)} | ${task.title.padEndFixed(titleWidth)} | ${(task.deadline.toString()
            .padEndFixed(deadlineWidth))} | ${(task.priority).toString().padEndFixed(priorityWidth)} |"
        println(row)
    }

    println(separator)

    print("Press enter to continue.")
    readln()
}

fun displayAddTaskMenu(taskRepository: TaskRepository){
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
        print("Enter the deadline [e.g, 2 May 2025 15:00]: ")
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
            taskRepository.addTask(title, formatDate(deadline), priority)
            println("Task added successfully!")
        }

        job.join()
    }

    print("Press enter to continue.")
    readln()
}

fun displayRemoveTaskMenu(taskRepository: TaskRepository) {
    if (taskRepository.getAllTasks().isEmpty()) {
        println("===================================")
        println("========== Remove Task ============")
        println("===================================")
        println("You currently have no tasks!")
        print("Press enter to continue.")
        readln()
        return
    }

    displayAllTasks(taskRepository)
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
            val idToRemove = UUID.fromString(userInputId).toString()
            taskRepository.removeTask(idToRemove)
            taskSuccessfullyRemoved = true

        } catch (_: IllegalArgumentException) {
            println("Invalid UUID string entered: '$userInputId'.")

        }
    } while (!taskSuccessfullyRemoved)

    runBlocking {
        println("Removing task, please wait!")
        delay(5000L)

        val job = launch {
            println("Task removed successfully!")
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

fun parseDeadline(deadlineString: String): LocalDateTime {
    return LocalDateTime.parse(deadlineString, DEADLINE_FORMATTER)
}

fun scheduleAllTaskNotifications(taskRepository: TaskRepository) {
    taskRepository.getAllTasks().forEach { task ->
        scheduleNotificationForTask(task)
    }
}

fun showNotification(task: Task) {
    if (SystemTray.isSupported()) {
        val image: Image? = try {
            val resourceUrl = object {}.javaClass.getResource("/task_notif_icon.png")
            if (resourceUrl == null) {
                println("Error: icon.png not found in resources folder!")
                null
            } else {
                ImageIO.read(resourceUrl)
            }
        } catch (e: IOException) {
            println("Error reading icon file.")
            e.printStackTrace()
            null
        }

        if (image == null) return

        val tray = SystemTray.getSystemTray()
        val trayIcon = TrayIcon(image, "Task Notification")
        trayIcon.isImageAutoSize = true

        try {
            tray.add(trayIcon)
            trayIcon.displayMessage(
                "Your Task Now: ${task.title}",
                "Deadline: ${task.deadline}",
                TrayIcon.MessageType.INFO
            )
            Thread.sleep(5000)
            tray.remove(trayIcon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else {
        println("System tray not supported")
    }
}

fun scheduleNotificationForTask(task: Task) {
    val now = LocalDateTime.now()
    val deadline = parseDeadline(task.deadline)

    if (deadline.isAfter(now)) {
        val delay = Duration.between(now, deadline).toMillis()
        println("Scheduling '${task.title}' in ${Duration.ofMillis(delay).toMinutes()} minutes.")

        scheduler.schedule({
            showNotification(task)
        }, delay, TimeUnit.MILLISECONDS)
    }
}

fun formatDate(date: String): LocalDateTime? {
    return try {
        val inputPattern = "d MMMM yyyy HH:mm"
        val inputFormatter = DateTimeFormatter.ofPattern(inputPattern, Locale.ENGLISH)

        val parsedDate = LocalDateTime.parse(date, inputFormatter)
        parsedDate

    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        println("Invalid date format. Please use the format 'd MMMM yyyy HH:mm:ss' (e.g., 2 May 2025 15:00).")
        null

    } catch (e: Exception){
        e.printStackTrace()
        null
    }
}

fun main(){
    // Main Program Loop
    val taskRepository = TaskRepository(filePath = "tasks.json")

    println("Scheduling notifications for existing tasks...")
    scheduleAllTaskNotifications(taskRepository)
    println("Scheduling complete.")

    var opt = -1

    do {
        displayMainMenu()
        opt = readln().toIntOrNull() ?: -1

        when (opt){
            1 -> { displayAddTaskMenu(taskRepository) }
            2 -> { displayRemoveTaskMenu(taskRepository) }
            3 -> { displayAllTasks(taskRepository) }
            0 -> {
                println("Shutting down background scheduler...")
                scheduler.shutdown()
                sayGoodbye()
            }
            else -> { println("=== Invalid Option ===") }
        }

    } while (opt != 0)
}
