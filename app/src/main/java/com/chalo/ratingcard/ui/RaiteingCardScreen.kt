package com.chalo.ratingcard.ui


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Locale

// Barra de estrellas: sin estado propio (state hoisting).
// Si onRatingChange es null, es solo lectura.
@Composable
fun RatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    maxRating: Int = 5,
    starSize: androidx.compose.ui.unit.Dp = 24.dp,
    onRatingChange: ((Int) -> Unit)? = null,
) {
    val activo = MaterialTheme.colorScheme.primary
    val inactivo = MaterialTheme.colorScheme.outlineVariant

    // Solo lectura: un único nodo accesible para todo el grupo
    val semanticaLectura = if (onRatingChange == null) {
        Modifier.clearAndSetSemantics {
            contentDescription = "Calificación: $rating de $maxRating estrellas"
        }
    } else Modifier

    Row(modifier = modifier.then(semanticaLectura)) {
        for (i in 1..maxRating) {
            val seleccionada = i <= rating
            val icono: @Composable () -> Unit = {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null, // la descripción va en el contenedor/botón
                    tint = if (seleccionada) activo else inactivo,
                    modifier = Modifier.size(starSize),
                )
            }
            if (onRatingChange != null) {
                // IconButton garantiza un área táctil mínima de 48dp
                IconButton(
                    onClick = { onRatingChange(i) },
                    modifier = Modifier.semantics {
                        role = Role.RadioButton
                        selected = seleccionada
                        contentDescription = "$i de $maxRating estrellas"
                    },
                    content = icono,
                )
            } else {
                icono()
            }
        }
    }
}

// Tarjeta sin estado: recibe datos y expone eventos hacia arriba.
@Composable
fun RatingCard(
    title: String,
    averageRating: Float,
    ratingCount: Int,
    userRating: Int,
    onUserRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(Locale.getDefault(), "%.1f", averageRating),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    RatingBar(rating = averageRating.toInt().coerceIn(0, 5), starSize = 16.dp)
                    Text(
                        text = "$ratingCount reseñas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Tu calificación",
                style = MaterialTheme.typography.labelLarge,
            )
            RatingBar(rating = userRating, onRatingChange = onUserRatingChange)
        }
    }
}

// Uso: el estado vive en el llamador (en una app real, en el ViewModel).
@Composable
fun RatingCardScreen(modifier: Modifier = Modifier) {
    var userRating by rememberSaveable { mutableIntStateOf(0) }

    RatingCard(
        title = "Curso de Álgebra Lineal",
        averageRating = 4.3f,
        ratingCount = 128,
        userRating = userRating,
        onUserRatingChange = { userRating = it },
        modifier = modifier.padding(16.dp),
    )
}

@Preview(showBackground = true)
@Composable
private fun RatingCardPreview() {
    MaterialTheme { RatingCardScreen() }
}