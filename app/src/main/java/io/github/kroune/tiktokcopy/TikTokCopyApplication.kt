package io.github.kroune.tiktokcopy

import android.app.Application
import io.github.kroune.tiktokcopy.data.api.ChallengeApiService
import io.github.kroune.tiktokcopy.data.api.ExpenseApiService
import io.github.kroune.tiktokcopy.data.local.ExpenseDatabase
import io.github.kroune.tiktokcopy.data.preferences.FirstLaunchManager
import io.github.kroune.tiktokcopy.data.repository.ChallengeRepository
import io.github.kroune.tiktokcopy.data.repository.ExpenseRepository
import org.koin.core.context.startKoin
import org.koin.dsl.module

class TikTokCopyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(module {
                single { ExpenseDatabase.getDatabase(this@TikTokCopyApplication) }
                single { get<ExpenseDatabase>().expenseDao() }
                single { get<ExpenseDatabase>().challengeDao() }
                single { ExpenseApiService() }
                single { ChallengeApiService() }
                single { ExpenseRepository(get(), get()) }
                single { ChallengeRepository(get(), get()) }
                single { FirstLaunchManager(this@TikTokCopyApplication) }
            })
        }
    }
}

