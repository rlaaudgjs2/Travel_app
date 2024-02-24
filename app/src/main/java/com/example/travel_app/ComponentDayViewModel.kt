package com.example.travel_app

import android.net.Uri
import androidx.lifecycle.ViewModel

class ComponentDayViewModel : ViewModel() {
    var selectedTitle: String? = null
    var selectedUri: Uri? = null
    var selectedContent: String? = null
    var selectedIndex: Int? = null

}