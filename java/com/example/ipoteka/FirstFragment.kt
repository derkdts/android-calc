package com.derk.calc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.derk.calc.databinding.FragmentFirstBinding
import java.io.Serializable
import java.util.Locale
import kotlin.math.pow

data class PaymentItem(
    val monthNumber: Int,
    val totalPayment: Double,
    val principalPayment: Double,
    val interestPayment: Double,
    val remainingBalance: Double
) : Serializable

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private var currentSchedule: List<PaymentItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCalculate.setOnClickListener {
            calculateMortgage()
        }

        binding.buttonViewSchedule.setOnClickListener {
            currentSchedule?.let { schedule ->
                val bundle = Bundle()
                bundle.putSerializable("schedule", ArrayList(schedule))
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
            }
        }
    }

    private fun calculateMortgage() {
        val propertyPrice = binding.editPropertyPrice.text.toString().toDoubleOrNull() ?: 0.0
        val downPayment = binding.editDownPayment.text.toString().toDoubleOrNull() ?: 0.0
        val annualInterestRate = binding.editInterestRate.text.toString().toDoubleOrNull() ?: 0.0
        val loanTermYears = binding.editLoanTerm.text.toString().toIntOrNull() ?: 0

        val principalAmount = propertyPrice - downPayment
        if (principalAmount <= 0) {
            binding.textResult.text = getString(R.string.error_principal)
            binding.buttonViewSchedule.visibility = View.GONE
            return
        }

        if (annualInterestRate <= 0) {
            binding.textResult.text = getString(R.string.error_rate)
            binding.buttonViewSchedule.visibility = View.GONE
            return
        }

        if (loanTermYears <= 0) {
            binding.textResult.text = getString(R.string.error_term)
            binding.buttonViewSchedule.visibility = View.GONE
            return
        }

        val isAnnuity = binding.radioAnnuity.isChecked
        val numberOfMonths = loanTermYears * 12
        val monthlyInterestRate = annualInterestRate / 12 / 100

        val schedule = mutableListOf<PaymentItem>()
        var remainingBalance = principalAmount

        if (isAnnuity) {
            val annuityRatio = (monthlyInterestRate * (1 + monthlyInterestRate).pow(numberOfMonths.toDouble())) /
                    ((1 + monthlyInterestRate).pow(numberOfMonths.toDouble()) - 1)
            val monthlyPayment = principalAmount * annuityRatio

            for (month in 1..numberOfMonths) {
                val interestPayment = remainingBalance * monthlyInterestRate
                val principalPayment = monthlyPayment - interestPayment
                remainingBalance -= principalPayment
                schedule.add(PaymentItem(month, monthlyPayment, principalPayment, interestPayment, if (remainingBalance < 0) 0.0 else remainingBalance))
            }
            binding.textResult.text = getString(R.string.result_format, String.format(Locale.getDefault(), "%,.2f", monthlyPayment))
        } else {
            val fixedPrincipalPayment = principalAmount / numberOfMonths
            var totalFirstPayment = 0.0
            var totalLastPayment = 0.0

            for (month in 1..numberOfMonths) {
                val interestPayment = remainingBalance * monthlyInterestRate
                val currentTotalPayment = fixedPrincipalPayment + interestPayment
                if (month == 1) totalFirstPayment = currentTotalPayment
                if (month == numberOfMonths) totalLastPayment = currentTotalPayment
                
                remainingBalance -= fixedPrincipalPayment
                schedule.add(PaymentItem(month, currentTotalPayment, fixedPrincipalPayment, interestPayment, if (remainingBalance < 0) 0.0 else remainingBalance))
            }
            binding.textResult.text = getString(R.string.result_format, 
                String.format(Locale.getDefault(), "%,.2f ... %,.2f", totalFirstPayment, totalLastPayment))
        }

        currentSchedule = schedule
        binding.buttonViewSchedule.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}