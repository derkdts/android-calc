package com.derk.calc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.derk.calc.databinding.FragmentSecondBinding
import java.util.Locale

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val schedule = arguments?.getSerializable("schedule") as? ArrayList<PaymentItem>
        schedule?.let {
            binding.recyclerSchedule.adapter = ScheduleAdapter(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class ScheduleAdapter(private val items: List<PaymentItem>) :
        RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textMonth: TextView = view.findViewById(R.id.text_month)
            val textTotalPayment: TextView = view.findViewById(R.id.text_total_payment)
            val textPrincipal: TextView = view.findViewById(R.id.text_principal)
            val textInterest: TextView = view.findViewById(R.id.text_interest)
            val textBalance: TextView = view.findViewById(R.id.text_balance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_payment, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            
            holder.textMonth.text = "Месяц ${item.monthNumber}"
            holder.textTotalPayment.text = String.format(Locale.getDefault(), "%,.2f ₸", item.totalPayment)
            holder.textPrincipal.text = String.format(Locale.getDefault(), "%,.2f", item.principalPayment)
            holder.textInterest.text = String.format(Locale.getDefault(), "%,.2f", item.interestPayment)
            holder.textBalance.text = String.format(Locale.getDefault(), "%,.2f", item.remainingBalance)
        }

        override fun getItemCount() = items.size
    }
}