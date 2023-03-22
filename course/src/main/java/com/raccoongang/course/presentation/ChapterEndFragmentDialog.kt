package com.raccoongang.course.presentation

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.raccoongang.core.ui.NewEdxButton
import com.raccoongang.core.ui.theme.NewEdxTheme
import com.raccoongang.core.ui.theme.appColors
import com.raccoongang.core.ui.theme.appShapes
import com.raccoongang.core.ui.theme.appTypography
import com.raccoongang.course.R
import com.raccoongang.course.presentation.section.CourseSectionFragment

class ChapterEndFragmentDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            NewEdxTheme {
                ChapterEndDialogScreen(
                    sectionName = requireArguments().getString(ARG_SECTION_NAME) ?: "",
                    onButtonClick = {
                        dismiss()
                        requireActivity().supportFragmentManager.popBackStack(
                            CourseSectionFragment::class.java.simpleName,
                            0
                        )
                    }
                )
            }
        }
    }

    companion object {
        private const val ARG_SECTION_NAME = "sectionName"
        fun newInstance(
            sectionName: String
        ): ChapterEndFragmentDialog {
            val dialog = ChapterEndFragmentDialog()
            dialog.arguments = bundleOf(
                ARG_SECTION_NAME to sectionName
            )
            dialog.isCancelable = false
            return dialog
        }
    }
}

@Composable
private fun ChapterEndDialogScreen(
    sectionName: String,
    onButtonClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.appShapes.courseImageShape),
        backgroundColor = MaterialTheme.appColors.background,
        shape = MaterialTheme.appShapes.courseImageShape
    ) {
        Column(
            Modifier
                .padding(horizontal = 40.dp)
                .padding(top = 48.dp, bottom = 38.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .width(76.dp)
                    .height(72.dp),
                painter = painterResource(id = R.drawable.course_id_diamond),
                contentDescription = null,
                tint = MaterialTheme.appColors.onBackground
            )
            Spacer(Modifier.height(36.dp))
            Text(
                text = stringResource(id = R.string.course_good_work),
                color = MaterialTheme.appColors.textPrimary,
                style = MaterialTheme.appTypography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.course_section_finished, sectionName),
                color = MaterialTheme.appColors.textSecondary,
                style = MaterialTheme.appTypography.titleSmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(42.dp))
            NewEdxButton(
                text = stringResource(id = R.string.course_back_to_outline),
                onClick = onButtonClick
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ChapterEndDialogScreenPreview() {
    NewEdxTheme {
        ChapterEndDialogScreen(sectionName = "Section", onButtonClick = {})
    }
}