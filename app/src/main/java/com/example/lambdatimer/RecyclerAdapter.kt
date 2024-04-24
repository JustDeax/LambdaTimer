package com.example.lambdatimer

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lambdatimer.databinding.SavedItemBinding
import com.example.lambdatimer.databinding.TimerInfoBinding

class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    private var timersList = emptyList<SavedTimersEntity>()

    class MyViewHolder(val b: SavedItemBinding): RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b = SavedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }
    override fun getItemCount() = timersList.size
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val thisItem = timersList[position]

        with(holder) {
            with(timersList[position]) {
                b.apply {
                    workB.text = thisItem.workM.toString()
                    workB.append(String.format(":%02d", thisItem.workS))
                    breakB.text = thisItem.breakM.toString()
                    breakB.append(String.format(":%02d", thisItem.breakS))
                    title.text = thisItem.tag

                    if (thisItem.bbM == 0 && thisItem.bbS == 0) bbBoolean.setImageResource(R.drawable.ic_bb_off)
                    else bbBoolean.setImageResource(R.drawable.ic_bb_on)

                    more.setOnClickListener {
                        val b = TimerInfoBinding.inflate(LayoutInflater.from(holder.itemView.context))
                        val s = AlertDialog.Builder(holder.itemView.context)
                        fun setText(string: String, minute: Int, second: Int): String {
                            return "$string - " + String.format("%d:%02d", minute, second)
                        }
                        s.setView(b.root)
                        s.setTitle(R.string.enter_tag)
                        s.setPositiveButton(R.string.edit) { _, _ ->
                            //TODO Open edit menu
                        }
                        s.setNeutralButton(R.string.del) {_, _ ->
                            //TODO Delete row in db
                        }
                        s.setNegativeButton(R.string.ok) {_, _ -> }
                        b.apply {
                            titleI.text = thisItem.tag
                            workI.text = setText(itemView.context.getString(R.string.work_time), thisItem.workM, thisItem.workS)
                            breakI.text = setText(itemView.context.getString(R.string.break_time), thisItem.breakM, thisItem.breakS)
                            if (thisItem.breakM == 0 && thisItem.breakS == 0) {
                                bbI.visibility = View.GONE
                                afterI.visibility = View.GONE
                            } else {
                                bbI.visibility = View.VISIBLE
                                afterI.visibility = View.VISIBLE
                                bbI.text = setText(itemView.context.getString(R.string.bb_time), thisItem.bbM, thisItem.bbS)
                                afterI.text = itemView.context.getString(R.string.after_txt) + " - ${thisItem.afterV}"
                            }
                        }
                        s.show()
                    }
                    card.setOnClickListener {
                        //TODO
                    }
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(timers: List<SavedTimersEntity>) {
        this.timersList = timers
        notifyDataSetChanged()
    }
}