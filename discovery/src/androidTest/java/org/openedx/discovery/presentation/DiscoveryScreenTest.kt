package org.openedx.discovery.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.openedx.core.domain.model.Course
import org.openedx.core.domain.model.Media
import org.openedx.core.ui.WindowSize
import org.openedx.core.ui.WindowType
import org.junit.Rule
import org.junit.Test
import java.util.Date

class DiscoveryScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    //region mockCourse
    private val course = Course(
        id = "id",
        blocksUrl = "blocksUrl",
        courseId = "courseId",
        effort = "effort",
        enrollmentStart = Date(),
        enrollmentEnd = null,
        hidden = false,
        invitationOnly = false,
        media = Media(),
        mobileAvailable = true,
        name = "Test course",
        number = "number",
        org = "EdX",
        pacing = "pacing",
        shortDescription = "shortDescription",
        start = "start",
        end = "end",
        startDisplay = "startDisplay",
        startType = "startType",
        overview = "",
        isEnrolled = false
    )
    //endregion

    @Test
    fun discoveryScreenLoading() {
        composeTestRule.setContent {
            DiscoveryScreen(
                windowSize = WindowSize(WindowType.Compact, WindowType.Compact),
                state = DiscoveryUIState.Loading,
                uiMessage = null,
                canLoadMore = false,
                refreshing = false,
                hasInternetConnection = true,
                onSearchClick = {},
                onSwipeRefresh = {},
                paginationCallback = {},
                onItemClick = {},
                onReloadClick = {}
            )
        }

        with(composeTestRule) {
            onNode(
                hasProgressBarRangeInfo(
                    ProgressBarRangeInfo(
                        current = 0f,
                        range = 0f..0f,
                        steps = 0
                    )
                )
            )
        }

    }

    @Test
    fun discoveryScreenLoaded() {
        composeTestRule.setContent {
            DiscoveryScreen(
                windowSize = WindowSize(WindowType.Compact, WindowType.Compact),
                state = DiscoveryUIState.Courses(listOf(course)),
                uiMessage = null,
                canLoadMore = false,
                refreshing = false,
                hasInternetConnection = true,
                onSearchClick = {},
                onSwipeRefresh = {},
                paginationCallback = {},
                onItemClick = {},
                onReloadClick = {}
            )
        }

        with(composeTestRule) {
            onNode(hasScrollAction()).onChildren().assertAny(hasText(course.name))
        }
    }

    @Test
    fun discoveryScreenPaginationAvailable() {
        composeTestRule.setContent {
            DiscoveryScreen(
                windowSize = WindowSize(WindowType.Compact, WindowType.Compact),
                state = DiscoveryUIState.Courses(listOf(course)),
                uiMessage = null,
                canLoadMore = true,
                refreshing = false,
                hasInternetConnection = true,
                onSearchClick = {},
                onSwipeRefresh = {},
                paginationCallback = {},
                onItemClick = {},
                onReloadClick = {}
            )
        }

        with(composeTestRule) {
            onNode(hasScrollAction()).onChildren().assertAny(hasText(course.name))
            onNode(
                hasScrollAction().and(
                    hasAnyChild(
                        hasProgressBarRangeInfo(
                            ProgressBarRangeInfo(
                                current = 0f,
                                range = 0f..0f,
                                steps = 0
                            )
                        )
                    )
                )
            )
        }

    }
}