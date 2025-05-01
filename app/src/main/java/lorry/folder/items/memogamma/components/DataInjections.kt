package lorry.folder.items.memogamma.components

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import lorry.folder.items.memogamma.StylusStateStore
import lorry.folder.items.memogamma.bubble.BubbleViewModel
import lorry.folder.items.memogamma.bubble.StylusStoreProvider
import lorry.folder.items.memogamma.bubble.StylusStoreProviderImpl
import lorry.folder.items.memogamma.bubble.stylusStateDataStore
import javax.inject.Singleton

@Module
@InstallIn(dagger.hilt.components.SingletonComponent::class)
abstract class DataInjections {

    @Binds
    @Singleton
    abstract fun bindStylusStoreProvider(
        impl: StylusStoreProviderImpl
    ): StylusStoreProvider
    
//    @dagger.Binds
//    abstract fun bindFileAccessDataSource(
//        fileAccessDataSource: FileAccessDS
//    ): IFileAccessDS

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

    @Provides
    @Singleton
    fun provideStylusStateDataStore(
        @ApplicationContext context: Context
    ): DataStore<StylusStateStore> =
        context.stylusStateDataStore // extension déjà définie
    
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
//    @dagger.Provides
//    @javax.inject.Singleton
//    fun provideDSIUserPreferences(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): DSI_UserPreferences {
//        return DS_UserPreferences(context)
//    }
}

class VideoShortcutsBubbleViewModelFactory(
    private val context: Context,
    private val provider: StylusStoreProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BubbleViewModel::class.java)) {
            return BubbleViewModel(context, provider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
