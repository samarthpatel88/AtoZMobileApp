import androidx.annotation.NonNull
import com.atozkids.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiManager {

    companion object {

        var addToken:Boolean = true

        fun getInstance(addToken: Boolean): ApiRepo {
            this.addToken = addToken
            val retrofit = ApiManager.retrofitInstance
            return retrofit.create(ApiRepo::class.java)
        }

        val instance: ApiRepo
            get() {
                val retrofit = ApiManager.retrofitInstance
                return retrofit.create(ApiRepo::class.java)
            }

        val retrofitInstance: Retrofit
            get() {
                val httpClient = OkHttpClient.Builder()
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    httpClient.addInterceptor(logging)
                }

                    httpClient.connectTimeout(60, TimeUnit.SECONDS)
                    httpClient.writeTimeout(60, TimeUnit.SECONDS)
                    httpClient.readTimeout(60, TimeUnit.SECONDS)

                val gson = GsonBuilder()
                    .setLenient()
                    .create()

                httpClient.hostnameVerifier { hostname, session ->
                    true
                }

                return Retrofit.Builder()
                    .baseUrl(ApiRepo.BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }

        @NonNull
        private fun addAccessToken(accessToken: String): Interceptor {

            return Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }
        }



    }


}