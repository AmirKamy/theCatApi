data class Weight(
    val imperial: String,
    val metric: String
)

data class Image(
    val id: String,
    val width: Int,
    val height: Int,
    val url: String
)

data class Breed(
    val id: String,
    val name: String,
    val origin: String,
    val description: String,
    val lifeSpan: String,
    val referenceImageId: String,
    val isFavorite: Boolean = false
)