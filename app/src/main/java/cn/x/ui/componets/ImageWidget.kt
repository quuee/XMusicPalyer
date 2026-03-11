package cn.x.ui.componets

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cn.x.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun ImageWidget(
    cover: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {

    val finalModifier = modifier.size(56.dp)

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(cover)
            .crossfade(true)
            .memoryCacheKey(cover.hashCode().toString())
            .diskCacheKey(cover.hashCode().toString())
            .size(Size.ORIGINAL)
            .build(),
        contentDescription = "网络图片",
        modifier = finalModifier,
        contentScale = contentScale,
        placeholder = painterResource(R.drawable.music_logo),
        error = painterResource(R.drawable.music_logo)
    )

}