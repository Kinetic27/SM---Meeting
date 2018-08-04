package la.hitomi.sm

import android.content.Intent
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

/**
 * Created by Kinetic on 2018-08-05.
 */

class LoginActivity: BaseActivity(){

    override var viewId: Int = R.layout.activity_login
    override var toolbarId: Int? = null

    override fun onCreate() {
        login_btn.setOnClickListener {
            if(id_tv.text.toString() == "test1234" && pw_tv.text.toString() == "test1234") {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
            }else{
                toast("미등록 회원입니다. 회원가입을 해주세요")
            }
        }
        signup_go.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }
    }

}