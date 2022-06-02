
import android.app.Application
import androidx.room.Room
import com.example.sotoontest.api.list.PlacesApi
import com.example.sotoontest.data.list.PlacesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(PlacesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePlacesApi(retrofit: Retrofit): PlacesApi =
        retrofit.create(PlacesApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): PlacesDatabase =
        Room.databaseBuilder(app, PlacesDatabase::class.java, "places_database")
            .fallbackToDestructiveMigration()
            .build()
}