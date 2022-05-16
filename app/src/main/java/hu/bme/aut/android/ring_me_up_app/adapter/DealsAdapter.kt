package hu.bme.aut.android.ring_me_up_app.adapter

import android.content.Context
import android.graphics.Color
import android.text.Html
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
import com.google.firebase.auth.FirebaseAuth
import hu.bme.aut.android.ring_me_up_app.data.Deal
import hu.bme.aut.android.ring_me_up_app.databinding.CardDealBinding
import org.w3c.dom.Text

class DealsAdapter(private val context: Context) :
    ListAdapter<Deal, DealsAdapter.DealViewHolder>(itemCallback) {

    private var dealList: List<Deal> = emptyList()
    private var lastPosition = -1
    private val userName = FirebaseAuth.getInstance().currentUser?.displayName

    class DealViewHolder(binding: CardDealBinding) : RecyclerView.ViewHolder(binding.root) {
        //        val tvAuthor: TextView = binding.tvAuthor
//        val tvDebtor: TextView = binding.tvDebtor
//        val tvDebtSum: TextView = binding.tvDebtSum
        val tvAdded: TextView = binding.tvAdded
        val tvGetBack: TextView = binding.tvGetBack
        val tvDate: TextView = binding.tvDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DealViewHolder(CardDealBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: DealViewHolder, position: Int) {
        val tmpDeal = dealList[position]
//        holder.tvAuthor.text = tmpDeal.author
//        holder.tvDebtor.text = tmpDeal.debtor
//        holder.tvDebtSum.text = tmpDeal.debtSum
        /**
         * checks whether user is the debtor or debtee and constructs the holder according to that
         */
        if (tmpDeal.author == userName) {
            holder.tvAdded.text = Html.fromHtml("<b>You</b> added <b>${tmpDeal.item}</b>")
            holder.tvGetBack.setTextColor(Color.parseColor("#a8ff7d"))
            holder.tvGetBack.text = Html.fromHtml("<b>You</b> get back ${tmpDeal.debtSum} from ${tmpDeal.debtor}")
            holder.tvDate.text = tmpDeal.date
        } else if (tmpDeal.debtor == userName) {
            holder.tvAdded.text = Html.fromHtml("<b>${tmpDeal.author}</b> added <b>${tmpDeal.item}</b>")
            holder.tvGetBack.setTextColor(Color.parseColor("#ff7d7d"))
            holder.tvGetBack.text = Html.fromHtml("<b>You</b> owe ${tmpDeal.debtSum}")
            holder.tvDate.text = tmpDeal.date
        }

        setAnimation(holder.itemView, position)
    }

    fun addDeal(deal: Deal?) {
        deal ?: return
        if (deal.author == userName || deal.debtor == userName) {
            dealList += (deal)
            submitList((dealList))
        } else {
            return
        }
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