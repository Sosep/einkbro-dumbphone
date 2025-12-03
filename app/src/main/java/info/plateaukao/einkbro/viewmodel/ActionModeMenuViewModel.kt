package info.plateaukao.einkbro.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.view.ActionMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import info.plateaukao.einkbro.R
import info.plateaukao.einkbro.preference.ConfigManager
import info.plateaukao.einkbro.preference.GptActionType
import info.plateaukao.einkbro.preference.HighlightStyle
import info.plateaukao.einkbro.unit.ShareUtil
import info.plateaukao.einkbro.view.data.MenuInfo
import info.plateaukao.einkbro.view.data.toMenuInfo
import info.plateaukao.einkbro.view.dialog.compose.HighlightStyleDialogFragment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ActionModeMenuViewModel : ViewModel(), KoinComponent {
    private val configManager: ConfigManager by inject()

    private var actionMode: ActionMode? = null
    private val _clickedPoint = MutableStateFlow(Point(100, 100))
    val clickedPoint: StateFlow<Point> = _clickedPoint.asStateFlow()

    private val _selectedText = MutableStateFlow("")
    val selectedText: StateFlow<String> = _selectedText.asStateFlow()

    private val _actionModeMenuState =
        MutableStateFlow(ActionModeMenuState.Idle as ActionModeMenuState)
    val actionModeMenuState: StateFlow<ActionModeMenuState> = _actionModeMenuState.asStateFlow()

    val showIcons: Boolean
        get() = configManager.showActionMenuIcons

    var menuInfos: MutableState<List<MenuInfo>> = mutableStateOf(emptyList())

    private val _shouldShow = MutableStateFlow(false)
    val shouldShow: StateFlow<Boolean> = _shouldShow.asStateFlow()

    fun isInActionMode(): Boolean = actionMode != null

    fun updateActionMode(actionMode: ActionMode?) {
        this.actionMode = actionMode
        if (actionMode == null) {
            finish()
        }
    }

    fun updateMenuInfos(
        context: Context,
        translationViewModel: TranslationViewModel,
    ) {
        menuInfos.value = getAllProcessTextMenuInfos(
            context,
            context.packageManager,
            translationViewModel,
        )
    }

    fun finish() {
        actionMode?.finish()
        actionMode = null
        _shouldShow.value = false
        _actionModeMenuState.value = ActionModeMenuState.Idle
    }

    fun updateSelectedText(text: String) {
        _selectedText.value = text
    }

    fun updateClickedPoint(point: Point) {
        _clickedPoint.value = point
    }

    fun hide() {
        _shouldShow.value = false
    }

    fun show() {
        _shouldShow.value = true
    }

    private fun getAllProcessTextMenuInfos(
        context: Context,
        packageManager: PackageManager,
        translationViewModel: TranslationViewModel,
    ): List<MenuInfo> {
        val intent = Intent(Intent.ACTION_PROCESS_TEXT).apply {
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val menuInfos : MutableList<MenuInfo> = ArrayList()

        menuInfos.add(
            0,
            MenuInfo(
                context.getString(R.string.read_from_here),
                imageVector = Icons.Outlined.RecordVoiceOver,
                action = { _actionModeMenuState.value = ActionModeMenuState.ReadFromHere }
            )
        )
        menuInfos.add(
            0,
            MenuInfo(
                context.getString(R.string.google_translate),
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_translate_google),
                action = { _actionModeMenuState.value = ActionModeMenuState.GoogleTranslate }
            )
        )
        if (configManager.imageApiKey.isNotBlank()) {
            menuInfos.add(
                0,
                MenuInfo(
                    context.getString(R.string.deepl_translate),
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_translate),
                    action = { _actionModeMenuState.value = ActionModeMenuState.DeeplTranslate }
                )
            )
        }

        menuInfos.add(
            0,
            MenuInfo(
                context.getString(R.string.select_paragraph),
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_paragraph),
                closeMenu = false,
                action = {
                    _actionModeMenuState.value = ActionModeMenuState.SelectParagraph
                }
            )
        )
        menuInfos.add(
            0,
            MenuInfo(
                context.getString(R.string.select_sentence),
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_reselect),
                closeMenu = false,
                action = {
                    _actionModeMenuState.value = ActionModeMenuState.SelectSentence
                }
            )
        )
        menuInfos.add(
            0,
            MenuInfo(
                context.getString(android.R.string.copy),
                drawable = ContextCompat.getDrawable(context, R.drawable.ic_copy),
                action = {
                    val processedText = selectedText.value.replace("\\n", "\n")
                    ShareUtil.copyToClipboard(context, processedText)
                    finish()
                }
            )
        )

        return menuInfos
    }
}

sealed class ActionModeMenuState {
    data object Idle : ActionModeMenuState()
    class Gpt(val gptActionIndex: Int) : ActionModeMenuState()
    data object GoogleTranslate : ActionModeMenuState()
    data object DeeplTranslate : ActionModeMenuState()
    data object Papago : ActionModeMenuState()
    data object Naver : ActionModeMenuState()
    data object ReadFromHere: ActionModeMenuState()
    class SplitSearch(val stringFormat: String) : ActionModeMenuState()
    class Tts(val text: String) : ActionModeMenuState()
    class HighlightText(val highlightStyle: HighlightStyle) : ActionModeMenuState()
    data object SelectSentence : ActionModeMenuState()
    data object SelectParagraph : ActionModeMenuState()
}