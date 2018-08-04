package la.hitomi.sm

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_people.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PeopleFragment : Fragment() {
    private val items = java.util.ArrayList<Data>()
    private var recyclerView: RecyclerView? = null
    private var adapter: RecyclerAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        view.fab.setOnClickListener {
            items += Data("글 제목", "작성자", "1분 전", "질문있습니다!")
            recyclerView!!.adapter.notifyDataSetChanged()
        }
        recyclerView = view!!.findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        recyclerView!!.adapter = RecyclerAdapter(items)
        adapter = recyclerView!!.adapter as RecyclerAdapter?

        Client.retrofitService.getsnsList().enqueue(object : Callback<ArrayList<PeopleRepo>> {
            override fun onResponse(call: Call<ArrayList<PeopleRepo>>?, response: Response<ArrayList<PeopleRepo>>?) {
                val repo = response!!.body()

                when (response.code()) {
                    200 -> {
                        repo!!.indices.forEach {
                            items += Data(repo[it].id!!, repo[it].writer!!, repo[it].date!!, repo[it].content!!)
                            recyclerView!!.adapter.notifyDataSetChanged()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<PeopleRepo>>?, t: Throwable?) {
                Log.v("C2cTest", "fail!!")
            }
        })
        return view
    }
    inner class RecyclerAdapter(private val dataList: ArrayList<Data>) : RecyclerView.Adapter<RecyclerAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(parent)

        override fun onBindViewHolder(holder: Holder, position: Int) {

            with(holder.itemView) {
                //val data = dataList[position]

                //writeTv.text = data.writer
               // dateTv.text = data.content
                //contentTv.text = data.date

            }
        }
        override fun getItemCount(): Int = dataList.size

        inner class Holder(parent: ViewGroup)
            : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_people, parent, false))
    }
}