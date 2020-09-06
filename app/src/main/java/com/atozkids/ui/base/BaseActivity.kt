
import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import android.net.ConnectivityManager
import com.atozkids.R
import com.atozkids.ui.base.BaseView
import com.atozkids.utils.Utility


abstract class BaseActivity : AppCompatActivity(), BaseView {

    var storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState!!.putInt("dummy_value", 0)

    }

    override fun showLoading() {
        Utility.showLoading(this@BaseActivity)
    }

    override fun hideLoading() {
        Utility.closeLoading()
    }

    override fun showErrorMsg(msg: Int) {
        Utility.showErrorToast(this, getString(msg))
    }

    override fun showErrorMsg(msg: String) {
        Utility.showErrorToast(this, msg)
    }

    override fun showSuccessMsg(msg: Int) {
        Utility.showSuccessToast(this, getString(msg))
    }

    override fun showSuccessMsg(msg: String) {
        Utility.showSuccessToast(this, msg)
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }


    private val APP_FOLDER_PATH: String = "/AtoZKids/"

    fun isNetworkConnectedMessage(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val connected =  activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (connected.not()){
            showErrorMsg(getString(R.string.str_no_internet_message))
        }
        return connected
    }
}