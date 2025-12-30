package com.byteutility.dev.leetcode.plus

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.eclipse.tm4e.core.registry.IThemeSource
import javax.inject.Inject

@Suppress("TooGenericExceptionCaught")
@HiltAndroidApp
class LeetCodePlusApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        initializeTextMate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun initializeTextMate() {
        try {
            // 1. Register file provider for TextMate to access assets
            FileProviderRegistry.getInstance().addFileProvider(
                AssetsFileResolver(applicationContext.assets)
            )

            // 2. Load and set theme
            val themeRegistry = ThemeRegistry.getInstance()
            val themeName = "darcula"
            val themeAssetsPath = "textmate/$themeName.json"

            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry.getInstance().tryGetInputStream(themeAssetsPath),
                        themeAssetsPath,
                        null
                    ),
                    themeName
                )
            )
            themeRegistry.setTheme(themeName)

            // 3. Load language grammars
            GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")

            Log.i("TextMate", "TextMate initialization completed successfully")
        } catch (e: Exception) {
            Log.e("TextMate", "Failed to initialize TextMate", e)
        }
    }

    companion object {
        val appCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}
