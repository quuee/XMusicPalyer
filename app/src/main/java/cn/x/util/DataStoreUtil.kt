package cn.x.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. 创建 DataStore 实例 (扩展属性)
// "app_settings" 是文件名，会自动生成 app_settings.preferences_pb
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

object DataStoreUtil {

    // 定义 Key，建议统一管理
    object Keys {
        val PLAY_MODE = intPreferencesKey("play_mode_key")
        // 可以添加其他 key，例如:
        // val VOLUME = intPreferencesKey("volume_key")
    }

    /**
     * 读取 Int 值的 Flow
     * @param key 偏好设置的键
     * @param defaultValue 默认值
     */
    fun getIntFlow(context: Context, key: Preferences.Key<Int>, defaultValue: Int): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    /**
     * 保存 Int 值
     */
    suspend fun saveInt(context: Context, key: Preferences.Key<Int>, value: Int) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    /**
     * 清除特定 key
     */
    suspend fun removeKey(context: Context, key: Preferences.Key<Int>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    /**
     * 清除所有数据
     */
    suspend fun clearAll(context: Context) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}