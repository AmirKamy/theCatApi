data class Breed(
    val id: String,
    val name: String,
    val description: String?,
    val origin: String?,
    val life_span: String?,
    val reference_image_id: String?,
    val isFavorite: Boolean,
    val imageUrl: String? = null,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
)