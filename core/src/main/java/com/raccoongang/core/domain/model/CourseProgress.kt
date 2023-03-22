package com.raccoongang.core.domain.model

data class CourseProgress(
    val sections: List<Section>,
    val progress: Int
) {

    data class Section(
        val displayName: String,
        val subsections: List<Subsection>,
    )

    data class Subsection(
        val earned: Float,
        val total: Float,
        val percentageString: String,
        val displayName: String,
        val score: List<Score>,
        val showGrades: Boolean,
        val graded: Boolean,
        val gradeType: String,
    )

    data class Score(
        val earned: Float,
        val possible: Float,
    )
}