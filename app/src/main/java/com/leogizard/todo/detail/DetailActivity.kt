package com.leogizard.todo.detail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.leogizard.todo.detail.ui.theme.TODOLeoGizardTheme
import com.leogizard.todo.list.Task
import java.util.UUID

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TODOLeoGizardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val task = intent.getSerializableExtra("task") as Task?
                    val onValidate = { t: Task->
                        intent.putExtra("task",t)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                    Detail(onValidate,task)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(onValidate: (Task) -> Unit,initialTask: Task?, modifier: Modifier = Modifier) {
    var newTask = Task(UUID.randomUUID().toString(),"Title")
    var task by remember {mutableStateOf(initialTask ?: newTask)}
    Column (
        modifier = Modifier.padding(Dp(16f)),
        verticalArrangement = Arrangement.spacedBy(Dp(16f))
    )
    {
        Text(
            text = "Task detail",
            modifier = modifier,
            style = MaterialTheme.typography.headlineLarge
        )
        OutlinedTextField(
            value = task.title,
            onValueChange = { t:String -> task = task.copy(title = t)},
            modifier = modifier,
            label = {Text("Title")},
            textStyle = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = task.description,
            onValueChange = { d:String -> task = task.copy(description = d)},
            modifier = modifier,
            label = {Text("Description")},

            )

        Button(
            onClick = {
                onValidate(task)}
        ) {
            Text("Validate")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailPreview() {
    TODOLeoGizardTheme {
        Detail({},Task(UUID.randomUUID().toString(),"Title","description"))
    }
}