package la.hitomi.sm

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_chat.view.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*


class ChatFragment : Fragment() {

    private val mItems = ArrayList<ChatItem>()
    private var adapter: ChatAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)


        initRecyclerView(view)
        view.send.onClick {
            mItems.add(ChatItem("나",view.form.text.toString()))
            adapter!!.notifyDataSetChanged()
        }
        addDummy()
    return view
    }

    private fun initRecyclerView(view: View) { // RecyclerView 기본세팅
        // 변경될 가능성 o : false 로 , 없다면 true.
        view.recyclerView.setHasFixedSize(false)

        adapter = ChatAdapter(mItems)
        view.recyclerView.adapter = adapter
        view.recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    private fun addDummy(){
        mItems.add(ChatItem("우현진","내일 늦지 말고 꼭 나와!"))
        mItems.add(ChatItem("심효근","내일 늦을꺼 같은데 ㅠㅠ"))
        mItems.add(ChatItem("우현진","늦지마~"))
        adapter!!.notifyDataSetChanged()
    }

}