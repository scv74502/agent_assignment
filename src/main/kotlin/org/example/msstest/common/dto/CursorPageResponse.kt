package org.example.msstest.common.dto

data class CursorPageResponse<T>(
    val items: List<T>,
    val nextCursor: Long?,
    val hasNext: Boolean,
    val size: Int,
) {
    companion object {
        fun <T> of(
            items: List<T>,
            pageSize: Int,
            cursorExtractor: (T) -> Long,
        ): CursorPageResponse<T> {
            val hasNext = items.size > pageSize
            val content = if (hasNext) items.dropLast(1) else items
            val nextCursor = if (hasNext) cursorExtractor(content.last()) else null
            return CursorPageResponse(
                items = content,
                nextCursor = nextCursor,
                hasNext = hasNext,
                size = content.size,
            )
        }
    }
}
