//import androidx.recyclerview.widget.RecyclerView
//
//
//class TopicCardAdapter(
//    private val items: List<Package>
//) : RecyclerView.Adapter<TopicCardAdapter.ViewHolder>() {
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
//
//    }
//}