package com.dtse.demo.ml

import android.app.Application
import com.huawei.hms.mlsdk.common.MLApplication

class MLScanDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MLApplication.getInstance().apiKey =
            "CgB6e3x97WledIRvTenn+a5z8Hk6VIvOXtvMxfSDL/gcxDo3R/HfbrMaHzms9nG3aya1R+DqbCjQGGV7auM9gfES"
    }
}