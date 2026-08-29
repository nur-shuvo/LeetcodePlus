package com.byteutility.dev.leetcode.plus

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import io.devconsole.DevConsole
import io.devconsole.api.BrowserBinding
import io.devconsole.api.BrowserConfig
import io.devconsole.api.DevConsoleConfig
import io.devconsole.api.OpenTriggers
import io.devconsole.api.ScreenshotPolicy
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initializeTextMate()
        initializeMobileAds()
        initializeDevConsole()
    }

    /**
     * Shake the device to open the in-app inspector. This returns immediately without starting
     * anything in release builds, where the no-op artifact is what gets compiled in. The browser
     * dashboard is never auto-started; open it from the inspector's More screen.
     */
    private fun initializeDevConsole() {
        DevConsole.initialize(
            this,
            DevConsoleConfig
                .default()
                .withScreenshotPolicy(ScreenshotPolicy(enabled = true))
                .withBrowserConfig(BrowserConfig(binding = BrowserBinding.LAN))
                .withOpenTriggers(OpenTriggers(shakeToOpen = true)),
        )
    }

    private fun initializeMobileAds() {
        MobileAds.initialize(this) { initializationStatus ->
            Log.i("AdMob", "Mobile Ads initialized: $initializationStatus")
        }
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
