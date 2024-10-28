import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FitnessTrackerApp() {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE) }
    val editor = sharedPreferences.edit()

    var workoutType by remember { mutableStateOf("") }
    var workoutDuration by remember { mutableStateOf("") }
    var workoutGoal by remember { mutableStateOf("") }

    var totalDuration by remember { mutableStateOf(0) }
    val workouts = remember { mutableStateListOf<Workout>() }

    // Load data from SharedPreferences when the app starts
    LaunchedEffect(Unit) {
        // Load previous workouts
        val workoutSet = sharedPreferences.getStringSet("workouts", emptySet()) ?: emptySet()
        val savedWorkouts = workoutSet.map { workout ->
            val (type, duration) = workout.split(":")
            Workout(type, duration)
        }
        workouts.addAll(savedWorkouts)
        totalDuration = savedWorkouts.sumBy { it.duration.toInt() }

        // Load saved goal
        workoutGoal = sharedPreferences.getString("goal", "") ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Fitness Tracker", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Input fields for workout type and duration
        TextField(
            value = workoutType,
            onValueChange = { workoutType = it },
            label = { Text("Workout Type") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = workoutDuration,
            onValueChange = { workoutDuration = it },
            label = { Text("Duration (mins)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input field for fitness goal
        TextField(
            value = workoutGoal,
            onValueChange = { workoutGoal = it },
            label = { Text("Set Workout Goal (e.g., 30 mins daily)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add workout
        Button(
            onClick = {
                if (workoutType.isNotBlank() && workoutDuration.isNotBlank()) {
                    val workout = Workout(workoutType, workoutDuration)
                    workouts.add(workout)
                    totalDuration += workoutDuration.toInt()

                    // Save workouts and total duration to SharedPreferences
                    val workoutSet = workouts.map { "${it.type}:${it.duration}" }.toSet()
                    editor.putStringSet("workouts", workoutSet).apply()

                    // Save goal
                    editor.putString("goal", workoutGoal).apply()

                    // Reset input fields
                    workoutType = ""
                    workoutDuration = ""
                }
            }
        ) {
            Text("Add Workout")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the list of workouts
        LazyColumn(modifier = Modifier.fillMaxHeight(0.5f)) {
            items(workouts) { workout ->
                WorkoutItem(workout = workout)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display user's fitness goal
        Text("Your Goal: $workoutGoal", style = MaterialTheme.typography.bodyLarge)

        // Display total workout duration
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total Workout Duration: $totalDuration mins", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun WorkoutItem(workout: Workout) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Workout: ${workout.type}", style = MaterialTheme.typography.bodyLarge)
            Text("Duration: ${workout.duration} mins", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

data class Workout(val type: String, val duration: String)
