package com.example.referencebook

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.referencebook.ui.theme.ReferenceBookTheme

class MainActivity : ComponentActivity() {
    private val vm: MVVM by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReferenceBookTheme {
                var showNews by remember { mutableStateOf(true) }
                if (showNews) {
                    NewsScreen(vm = vm) {
                        showNews = false
                    }
                } else {
                    OpenGLScreen()
                }
            }
        }
    }
}

@Composable
fun NewsScreen(vm: MVVM, onSwitchOpenGL: () -> Unit) {
    val news by vm.currentNews.collectAsState()
    Column(Modifier.fillMaxSize()) {
        Button(
            onClick = onSwitchOpenGL,
            modifier = Modifier.padding(3.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White),

            ) {
            Text("Discover space", fontSize = 20.sp)
        }
        Row(Modifier.weight(1f)) {
            NewsCard(news[0], onLike = { vm.getLikes(0) }, Modifier.weight(1f))
            NewsCard(news[1], onLike = { vm.getLikes(1) }, Modifier.weight(1f))
        }
        Row(Modifier.weight(1f)) {
            NewsCard(news[2], onLike = { vm.getLikes(2) }, Modifier.weight(1f))
            NewsCard(news[3], onLike = { vm.getLikes(3) }, Modifier.weight(1f))
        }
    }
}

@Composable
fun NewsCard(news: Data, onLike: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = modifier.weight(0.6f).fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = news.imageUrl),
                contentDescription = null,
                modifier = modifier.fillMaxSize()
            )
        }
        Box(
            modifier = modifier.weight(0.3f).fillMaxWidth(),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(text = news.title,
                fontSize = 15.sp)
        }
        Row(
            modifier = modifier.weight(0.1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = "Likes: ${news.likes}",
                fontSize = 20.sp)
            Button(onClick = onLike,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray,
                    contentColor = Color.White),
                    modifier = Modifier.width(100.dp).height(35.dp))
            {
                Text(text = "Like",
                    fontSize = 20.sp)
            }
        }
    }
}
@Composable
fun OpenGLScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context -> GLSurfaceView(context).apply {
            setEGLContextClientVersion(1)
            setRenderer(Render(context))
        }
        }
    )
}
