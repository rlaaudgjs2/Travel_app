package com.example.travel_app

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class KakaoApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, "65e1571c0b9d501c7fa536ce6a697638")
    }
}