//package com.example.sieve_of_wisdom.ui.quiz
//
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.sieve_of_wisdom.databinding.ItemQuestionDetailBinding
//
//class QuestionDetailAdapter(
//    private val items: List<QuestionDetailItem>
//) : RecyclerView.Adapter<QuestionDetailAdapter.ViewHolder>() {
//
//    inner class ViewHolder(val binding: ItemQuestionDetailBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemQuestionDetailBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false
//        )
//        return ViewHolder(binding)
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = items[position]
//        with(holder.binding) {
//            tvQuestionNumber.text = "Câu ${item.questionNumber}"
//            tvQuestion.text = item.questionText
//            tvCorrectAnswer.text = "Đáp án: ${item.correctAnswer}"
//            tvUserAnswer.text = "Bạn trả lời: ${item.userAnswer ?: "Bỏ qua"}"
//
//            // Green (#4CAF50) for correct, Red (#E53935) for incorrect/skipped
//            val backgroundColor = if (item.isCorrect) {
//                Color.parseColor("#4CAF50")
//            } else {
//                Color.parseColor("#E53935")
//            }
//            questionItem.setBackgroundColor(backgroundColor)
//        }
//    }
//}

package com.example.sieve_of_wisdom.ui.quiz

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sieve_of_wisdom.data.model.QuestionResult
import com.example.sieve_of_wisdom.databinding.ItemQuestionDetailBinding
import com.example.sieve_of_wisdom.R
class QuestionDetailAdapter(
    private var results: List<QuestionResult>
) : RecyclerView.Adapter<QuestionDetailAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemQuestionDetailBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemQuestionDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val result = results[position]

        with(holder.binding) {
            tvQuestionNumber.setText("Câu ${position + 1}")
            tvQuestion.setText(result.asking)
            tvCorrectAnswer.setText("Đáp án: ${result.correctAnswer}")
            tvUserAnswer.setText(  "Bạn trả lời: ${result.userAnswer ?: "Bỏ qua"}")


            val backgroundColor = if (result.isCorrect) {
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.question_correct
                )
            } else {
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.question_wrong
                )
            }

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(backgroundColor)
                setStroke(
                    5,
                    Color.BLACK
                )
            }

            questionItemContent.background = drawable
        }
    }

    fun updateData(results: List<QuestionResult>) {
        this.results = results
        notifyDataSetChanged()
    }
}