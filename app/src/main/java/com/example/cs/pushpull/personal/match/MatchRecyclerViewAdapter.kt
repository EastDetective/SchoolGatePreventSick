package com.example.cs.pushpull.personal.match

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.cs.pushpull.R

class MatchRecyclerViewAdapter(private val listStr: List<String>) :
    RecyclerView.Adapter<MatchRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mConstrinLayout: ConstraintLayout = itemView.findViewById(R.id.constrainLayout)
        var expend: Boolean = false

        var mTxtAct: TextView = itemView.findViewById(R.id.textView_Act)
        var mTxtName: TextView = itemView.findViewById(R.id.textView_ClassName)
        var mTxtDate: TextView = itemView.findViewById(R.id.textView_ApplyTime)
        var mTxtDescribe: TextView = itemView.findViewById(R.id.textView_describe)
        var mBtnDetail: Button = itemView.findViewById(R.id.button_showDetail)
        var mImgSuccess: ImageView = itemView.findViewById(R.id.imageView_success)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v : View = LayoutInflater.from(parent.context).inflate(R.layout.item_match_record, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mTxtAct.text = "受腐生"
        when (position % 3) {
            0 -> holder.mTxtName.text = "微積分"
            1 -> holder.mTxtName.text = "離散數學"
            else -> holder.mTxtName.text = "死聲終極OTK爆牌薩"
        }
        holder.mTxtDate.text = "2020/01/01"
        holder.mTxtDescribe.text = listStr[position] + "文字文字文字文字文字\n文字文字文字文字文字\n文字文字文字文字文字\n文字文字文字文字文字\n文字文字文字文字文字"
        holder.mBtnDetail.setOnClickListener {
            Toast.makeText(it.context, "Item $position is clicked.", Toast.LENGTH_SHORT).show()

            if(!holder.expend){
                holder.mConstrinLayout.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            }
            holder.expend = !holder.expend
        }
        holder.mImgSuccess.setImageResource(android.R.drawable.star_on)
    }

    override fun getItemCount() = listStr.count()

}