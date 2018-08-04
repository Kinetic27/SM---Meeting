package la.hitomi.sm

import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_prelist.*


class PreListActivity : BaseActivity() {

    override var viewId: Int = R.layout.activity_prelist
    override var toolbarId: Int? = R.id.toolbar

    private val mItems = ArrayList<MusicItem>()
    private var adapter: MusicAdapter? = null
    override fun onCreate() {

    }
    private fun initRecyclerView() { // RecyclerView 기본세팅
        // 변경될 가능성 o : false 로 , 없다면 true.
        recyclerView.setHasFixedSize(false)

        adapter = MusicAdapter(mItems, this, this, false)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}