package uab.cs422.projectinlook.ui

import android.content.Context
import android.util.AttributeSet
import androidx.preference.SwitchPreferenceCompat
import uab.cs422.projectinlook.R

// COURTESY OF https://stackoverflow.com/a/74956115
class MaterialSwitchPreference(context: Context, attributeSet: AttributeSet): SwitchPreferenceCompat(context, attributeSet) {
    init {
        widgetLayoutResource = R.layout.preference_widget_material_switch
    }
}