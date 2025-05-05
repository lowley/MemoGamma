package lorry.folder.items.memogamma.components

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.hilt.InstallIn
import lorry.folder.items.memogamma.__data.userPreferences.IUserPreferences
import lorry.folder.items.memogamma.__data.userPreferences.UserPreferences
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.ui.ScreenInteraction

@Module
@InstallIn(dagger.hilt.components.SingletonComponent::class)
abstract class DataInjections {

    @dagger.Binds
    abstract fun bindScreenInteraction(
        screenInteraction: ScreenInteraction
    ): ScreenInteraction

    //
//    @Binds
//    abstract fun bindFfmpegDataSource(
//        ffmpegDataSource: DS_ffmpeg
//    ): DSI_ffmpeg
//
//    @dagger.Binds
//    abstract fun bindIntentWrapperDataSource(
//        intentWrapperDataSource: IntentWrapperDS
//    ): IIntentWrapperDS
//
//    @dagger.Binds
//    abstract fun bindFTPDataSource(
//        ftpDataSource: FtpDS
//    ): IFtpDS
//
//    @dagger.Binds
//    abstract fun bindFfmpegDataSource(
//        ffmpegDataSource: FfmpegDS
//    ): IFfmpegDS
}

@dagger.Module
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
class DataInjections2 {
//    @dagger.Provides
//    @javax.inject.Singleton
//    fun bindIntent(): Intent {
//        return Intent()
//    }
//
//    @dagger.Provides
//    @javax.inject.Singleton
//    fun provideContext(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): Context {
//        return context
//    }
//
//    @dagger.Provides
//    @javax.inject.Singleton
//    fun provideApplicationScope(): CoroutineScope {
//        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
//    }
//
//    @dagger.Provides
//    @javax.inject.Singleton
//    fun provideShortcutUseCase(
//        ftpDataSource: IFtpDS,
//        fileRepo: IFileAccessRP,
//        userPreferences: DSI_UserPreferences,
//        scope: CoroutineScope,
//        context: Context
//    ): ShortcutUseCase {
//        return ShortcutUseCase(
//            ftpDataSource = ftpDataSource,
//            fileRepo = fileRepo,
//            userPreferences = userPreferences,
//            scope = scope,
//            context = context
//        )
//    }
//
    @dagger.Provides
    @javax.inject.Singleton
    fun provideDSIUserPreferences(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): IUserPreferences {
        return UserPreferences(context)
    }
}

class VideoShortcutsBubbleViewModelFactory(
    private val context: Context,
    private val userPreferences: IUserPreferences,
    private val screenInteraction: ScreenInteraction
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BubbleViewModel::class.java)) {
            return BubbleViewModel(context, userPreferences, screenInteraction) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
