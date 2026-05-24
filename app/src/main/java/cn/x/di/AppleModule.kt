package cn.x.di


import androidx.room.Room
import cn.x.data.MusicDatabase
import cn.x.data.dao.FolderDao
import cn.x.data.dao.SongDao
import cn.x.data.dao.SongListDao
import cn.x.service.PlayerController
import cn.x.service.PlayerControllerImpl
import cn.x.ui.screen.FolderScreenVM
import cn.x.ui.screen.HomeScreenVM
import cn.x.ui.screen.LocalSongScreenVM
import cn.x.ui.screen.ScanScreenVM
import cn.x.ui.screen.SongListScreenVM
import cn.x.ui.screen.sub_screen.AddSelectSongScreenVM
import cn.x.ui.screen.sub_screen.PlayerScreenVM
import cn.x.ui.screen.sub_screen.SearchScreenVM
import cn.x.ui.screen.sub_screen.SongListSortScreenVM
import cn.x.ui.screen.sub_screen.SongsScreenVM
import cn.x.util.MediaStoreScanUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.scope.get
import org.koin.dsl.module


val appModule = module {
//    single<CoroutineScope> {
//        // 使用 Koin 内置的应用级作用域
//        koinApplicationScope()
//    }

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    }

    single<PlayerController> { PlayerControllerImpl(player = get(), get(),get()) }

}

// 工具模块
val utilModule: Module = module {
    single { MediaStoreScanUtil(get()) }
}

// 数据库模块
val databaseModule: Module = module {

    // Room 数据库实例
    single {
        Room.databaseBuilder(
            androidApplication(),
            MusicDatabase::class.java,
            "x_music_database"
        ).fallbackToDestructiveMigration(false)
            .build()
    }

    // DAO
    single { get<MusicDatabase>().FolderDao() }
    single { get<MusicDatabase>().PlayListDao() }
    single { get<MusicDatabase>().SongDao() }
    single { get<MusicDatabase>().SongListDao() }
}

// ViewModel 模块 (使用 Koin Compose 4.x 语法)
val viewModelModule: Module = module {

    viewModel<FolderScreenVM> {
        FolderScreenVM(
            folderDao = get<FolderDao>(),
            playerController = get<PlayerController>()
        )
    }

    viewModel<HomeScreenVM> {
        HomeScreenVM(playerController = get<PlayerController>())
    }

    viewModel<LocalSongScreenVM> {
        LocalSongScreenVM(
            songDao = get<SongDao>(),
            songListDao = get<SongListDao>(),
            playerController = get<PlayerController>()
        )
    }

    viewModel<ScanScreenVM> {
        ScanScreenVM(
            mediaStoreScanUtil = get<MediaStoreScanUtil>(),
            songDao = get<SongDao>(),
            folderDao = get<FolderDao>(),
        )
    }

    viewModel<SongListScreenVM> {
        SongListScreenVM(
            songListDao= get<SongListDao>()
        )
    }
    viewModel<AddSelectSongScreenVM> {
        AddSelectSongScreenVM(
            songDao = get<SongDao>(),
            songListDao= get<SongListDao>(),
            savedStateHandle = get()
        )
    }
    viewModel<FolderScreenVM> {
        FolderScreenVM(
            folderDao = get<FolderDao>(),
            playerController = get()
        )
    }

    viewModel<PlayerScreenVM> {
        PlayerScreenVM(
            playerController = get()
        )
    }
    viewModel<SearchScreenVM> {
        SearchScreenVM(
            songDao = get<SongDao>(),
        )
    }
    viewModel<SongListSortScreenVM> {
        SongListSortScreenVM(
            songListDao= get<SongListDao>(),
        )
    }
    viewModel<SongsScreenVM> {
        SongsScreenVM(
            songListDao= get<SongListDao>(),
            playerController = get(),
            savedStateHandle = get()
        )
    }


}