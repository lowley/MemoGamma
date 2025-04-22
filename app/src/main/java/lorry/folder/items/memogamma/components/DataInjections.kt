package lorry.folder.items.memogamma.components

import dagger.Module
import dagger.hilt.InstallIn

@Module
@InstallIn(dagger.hilt.components.SingletonComponent::class)
abstract class DataInjections {

//    @dagger.Binds
//    abstract fun bindFileAccessRepository(
//        fileAccessRepository: FileAccessRP
//    ): IFileAccessRP
//
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