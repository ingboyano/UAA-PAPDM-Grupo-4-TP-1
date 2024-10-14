package com.example.movieregistryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class Movie(
    val title: String,
    val director: String,
    val year: Int,
    val genre: String,
    val duration: Int,
    val posterUrl: String
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    surface = Color(0xFF1C1C1E),
    onSurface = Color(0xFFE0E0E0),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    surface = Color(0xFFFFFFFF),
    onSurface = Color.Black,
    background = Color(0xFFF5F5F5),
    onBackground = Color.Black
)


@Composable
fun MovieRegistryAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieRegistryAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieRegistryApp()
                }
            }
        }
    }
}

@Composable
fun MovieRegistryApp() {
    var movies by remember { mutableStateOf(listOf<Movie>()) }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Ingresar Película", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Listar Películas", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                Text("Eliminar Película", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> MovieInputForm { newMovie ->
                movies = movies + newMovie
            }
            1 -> MovieList(movies = movies, onDelete = {}, showDeleteButton = false)
            2 -> MovieList(movies = movies, onDelete = { movieToRemove ->
                movies = movies.filter { it != movieToRemove }
            }, showDeleteButton = true)
        }
    }
}

@Composable
fun MovieInputForm(onAddMovie: (Movie) -> Unit) {
    var title by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }

    Column {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        )
        TextField(value = director, onValueChange = { director = it }, label = { Text("Director") }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        )
        TextField(value = year, onValueChange = { year = it }, label = { Text("Año de Lanzamiento") }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        )
        TextField(value = genre, onValueChange = { genre = it }, label = { Text("Género") }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        )
        TextField(value = duration, onValueChange = { duration = it }, label = { Text("Duración (min)") }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        )
        TextField(value = posterUrl, onValueChange = { posterUrl = it }, label = { Text("URL del Poster") }, modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank() && director.isNotBlank()) {
                    onAddMovie(Movie(title, director, year.toIntOrNull() ?: 0, genre, duration.toIntOrNull() ?: 0, posterUrl))
                    title = ""; director = ""; year = ""; genre = ""; duration = ""; posterUrl = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text("Agregar Película", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>, onDelete: (Movie) -> Unit, showDeleteButton: Boolean) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(movies) { movie ->
            MovieListItem(movie = movie, onDelete = onDelete, showDeleteButton = showDeleteButton)
        }
    }
}

@Composable
fun MovieListItem(movie: Movie, onDelete: (Movie) -> Unit, showDeleteButton: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = movie.posterUrl,
                contentDescription = "Poster",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.width(16.dp))


            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )


                Text(text = "Director: ${movie.director}")
                Text(text = "Año: ${movie.year}")
                Text(text = "Género: ${movie.genre}")
                Text(text = "Duración: ${movie.duration} min")


                if (showDeleteButton) {
                    Button(
                        onClick = { onDelete(movie) },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMovieRegistryApp() {
    MovieRegistryApp()
}
