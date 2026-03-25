package com.derk.calc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.derk.calc.databinding.FragmentBudgetBinding
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCalculateBudget.setOnClickListener {
            calculateBudget()
        }
    }

    private fun calculateBudget() {
        val income = binding.editIncome.text.toString().toDoubleOrNull() ?: 0.0
        val loanPayment = binding.editLoanPayment.text.toString().toDoubleOrNull() ?: 0.0
        
        val food = binding.editFood.text.toString().toDoubleOrNull() ?: 0.0
        val communal = binding.editCommunal.text.toString().toDoubleOrNull() ?: 0.0
        val transport = binding.editTransport.text.toString().toDoubleOrNull() ?: 0.0
        val other = binding.editOther.text.toString().toDoubleOrNull() ?: 0.0

        val totalExpenses = loanPayment + food + communal + transport + other
        val freeBalance = income - totalExpenses
        
        binding.textTotalExpenses.text = getString(
            R.string.total_expenses_result,
            String.format(Locale.getDefault(), "%,.2f", totalExpenses)
        )

        binding.textBudgetResult.text = getString(
            R.string.free_balance_result,
            String.format(Locale.getDefault(), "%,.2f", freeBalance)
        )
        
        if (freeBalance < 0) {
            binding.textBudgetResult.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        } else {
            binding.textBudgetResult.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}