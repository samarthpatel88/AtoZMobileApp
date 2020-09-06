import com.atozkids.responsemodels.*
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiRepo {
    companion object {
        const val BASE_URL = "http://atozkidszone.co.in/api/"
        const val APP_CODE = "E4BC1D77-2B00-473D-B729-3FB98AD34172"
    }

    @GET("menu/$APP_CODE")
    fun getMenuContent(): Call<MenuContentResponseModel>

    @GET("category/$APP_CODE")
    fun getCategoryList(): Call<CategoryListResponseModel>

    @GET("item/$APP_CODE/{categoryid}")
    fun getCategoryItems(@Path("categoryid") categoryid: Int): Call<CategoryItemsResponseModel>

    @POST("device")
    fun insertUpdateDeviceDetails(@Body jsonObject: JsonObject):Call<ResponseBody>

    @GET("quiz/$APP_CODE/{categoryid}")
    fun getQuiz(@Path("categoryid") categoryid: Int): Call<QuizResponseModel>

    @GET("rhymes/$APP_CODE")
    fun getRhymes(): Call<RhymesResponseModel>
}