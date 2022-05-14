package hu.bme.aut.android.ring_me_up_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.android.ring_me_up_app.data.Deal
import hu.bme.aut.android.ring_me_up_app.databinding.CardDealBinding

class DealsAdapter(private val context: Context) :
    ListAdapter<Deal, DealsAdapter.DealViewHolder>(itemCallback) {

    private var dealList: List<Deal> = emptyList()
    private var lastPosition = -1

    class DealViewHolder(binding: CardDealBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvAuthor: TextView = binding.tvAuthor
        val tvDebtor: TextView = binding.tvDebtor
        val tvDebtSum: TextView = binding.tvDebtSum
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DealViewHolder(CardDealBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val tmpDeal = dealList[position]
        holder.tvAuthor.text = tmpDeal.author
        holder.tvDebtor.text = tmpDeal.debtor
        holder.tvDebtSum.text = tmpDeal.debtSum

//        if (tmpDeal.imageUrl.isNullOrBlank()) {
//            holder.imgPost.visibility = View.GONE
//        } else {
//            Glide.with(context).load(tmpDeal.imageUrl).into(holder.imgPost)
//            holder.imgPost.visibility = View.VISIBLE
//        }

        setAnimation(holder.itemView, position)
    }

    fun addDeal(deal: Deal?) {
        deal ?: return

        dealList += (deal)
        submitList((dealList))
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    companion object {
        object itemCallback : DiffUtil.ItemCallback<Deal>() {
            override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean {
                return oldItem == newItem
            }
        }
    }
}