package com.example.referencebook

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.referencebook.ui.theme.ReferenceBookTheme

class MainActivity : ComponentActivity() {
    private val vm: MVVM by viewModels()
    private val planetvm: MVVMPlanet by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReferenceBookTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.News) }

                when (currentScreen) {
                    Screen.News -> NewsScreen(vm = vm) { currentScreen = it }
                    Screen.OpenGL -> OpenGLScreen(onScreenChange = { currentScreen = it })
                    Screen.MoonInfo -> MoonView(onScreenChange = { currentScreen = it })
                    Screen.PlanetInfo -> {
                        val planet = planetvm.planets[0]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo1 -> {
                        val planet = planetvm.planets[1]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo2 -> {
                        val planet = planetvm.planets[2]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo3 -> {
                        val planet = planetvm.planets[3]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo4 -> {
                        val planet = planetvm.planets[4]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo5 -> {
                        val planet = planetvm.planets[5]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo6 -> {
                        val planet = planetvm.planets[6]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo7 -> {
                        val planet = planetvm.planets[7]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo8 -> {
                        val planet = planetvm.planets[8]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                    Screen.PlanetInfo9 -> {
                        val planet = planetvm.planets[9]
                        PlanetView (planet = planet , onScreenChange = { currentScreen = it })
                    }
                }
            }
        }

    }
}

enum class Screen {
    News,
    OpenGL,
    MoonInfo,
    PlanetInfo,
    PlanetInfo1,
    PlanetInfo2,
    PlanetInfo3,
    PlanetInfo4,
    PlanetInfo5,
    PlanetInfo6,
    PlanetInfo7,
    PlanetInfo8,
    PlanetInfo9
}

@Composable
fun NewsScreen(vm: MVVM, onScreenChange: (Screen) -> Unit) {
    val news by vm.currentNews.collectAsState()
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.weight(1f)) {
            NewsCard(news[0], onLike = { vm.getLikes(0) }, Modifier.weight(1f))
            NewsCard(news[1], onLike = { vm.getLikes(1) }, Modifier.weight(1f))
        }
        Row(Modifier.weight(1f)) {
            NewsCard(news[2], onLike = { vm.getLikes(2) }, Modifier.weight(1f))
            NewsCard(news[3], onLike = { vm.getLikes(3) }, Modifier.weight(1f))
        }
        Button(
            onClick = {onScreenChange(Screen.OpenGL)},
            modifier = Modifier.padding(3.dp).fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Gray),

            ) {
            Text("Go to space >", fontSize = 20.sp)

        }
    }
}

@Composable
fun NewsCard(news: Data, onLike: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(8.dp).background(Color.LightGray),
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
            contentAlignment = Alignment.Center
        ) {
            Text(text = news.title,
                fontSize = 15.sp)
        }
        Row(
            modifier = modifier.weight(0.1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(text = " \uD83D\uDC4D: ${news.likes}",
                fontSize = 20.sp)
            Button(onClick = onLike,
                colors = ButtonDefaults.buttonColors(
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
fun OpenGLScreen(onScreenChange: (Screen) -> Unit) {
    var currentPlanetIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(1)
                    setRenderer(Render(context) { currentPlanetIndex })
                }
            },
            update = {
                it.requestRender()
            }
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { moveLeft(currentPlanetIndex) { newIndex -> currentPlanetIndex = newIndex } },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                modifier = Modifier.width(80.dp)) {
                Text("<", fontSize = 20.sp)
            }
            Button(onClick = { when (currentPlanetIndex){
                0 -> {onScreenChange(Screen.PlanetInfo)}
                1 -> {onScreenChange(Screen.PlanetInfo1)}
                2 -> {onScreenChange(Screen.PlanetInfo2)}
                3 -> {onScreenChange(Screen.PlanetInfo3)}
                4 -> {onScreenChange(Screen.MoonInfo)}
                5 -> {onScreenChange(Screen.PlanetInfo4)}
                6 -> {onScreenChange(Screen.PlanetInfo5)}
                7 -> {onScreenChange(Screen.PlanetInfo6)}
                8 -> {onScreenChange(Screen.PlanetInfo7)}
                9 -> {onScreenChange(Screen.PlanetInfo8)}
                10 -> {onScreenChange(Screen.PlanetInfo9)}
                else -> {}
            } },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                modifier = Modifier.width(150.dp)) {
                Text("Информация",fontSize = 15.sp)
            }
            Button(onClick = { moveRight(currentPlanetIndex) { newIndex -> currentPlanetIndex = newIndex } },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                modifier = Modifier.width(80.dp)) {
                Text(">", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun MoonView(onScreenChange: (Screen) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                GLSurfaceView(context).apply {
                    setEGLContextClientVersion(2)
                    setRenderer(MoonRender(context))
                    setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x80000000))
                .padding(16.dp),

            ) {
            Text(
                text = "Информация о Луне",
                color = Color.White,
                fontSize = 24.sp
            )
            Text(
                text = "Луна — единственный естественный спутник Земли. Она является единственным внеземным астрономическим объектом, на который ступала нога человека.",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {onScreenChange(Screen.OpenGL)},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                modifier = Modifier.width(145.dp)) {
                Text("Назад", fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun PlanetView(planet: DataPlanet, onScreenChange: (Screen) -> Unit) {
    Column(
        modifier =Modifier.fillMaxSize().background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(0.6f).fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = planet.imageUrl),
                contentDescription = planet.name,
                modifier = Modifier.fillMaxSize()
            )
        }
        Box(
            modifier = Modifier.weight(0.3f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = planet.description,
                color = Color.White,
                fontSize = 18.sp)
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {onScreenChange(Screen.OpenGL)},
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White),
                modifier = Modifier.width(145.dp)) {
                Text("Назад", fontSize = 15.sp)
            }
        }
    }
}

fun moveRight(currentIndex: Int, updateIndex: (Int) -> Unit) {
    val planetsCount = 11
    updateIndex((currentIndex + 1) % planetsCount)
}

fun moveLeft(currentIndex: Int, updateIndex: (Int) -> Unit) {
    val planetsCount = 11
    updateIndex(if (currentIndex - 1 < 0) planetsCount - 1 else currentIndex - 1)
}