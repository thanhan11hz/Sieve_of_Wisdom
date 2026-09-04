package com.example.sieve_of_wisdom.ui.quiz

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.sieve_of_wisdom.R
import com.example.sieve_of_wisdom.databinding.FragmentQuizBinding
import com.example.sieve_of_wisdom.ui.viewmodel.QuizViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizViewModel by hiltNavGraphViewModels(R.id.quiz_graph)

    private var mediaPlayer: MediaPlayer? = null
    private var soundPool: SoundPool? = null
    private var soundRightId: Int = 0
    private var soundWrongId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeViewModel()
        initBackgroundMusic()
        initSoundEffects()

        val categoryId = requireArguments().getInt("category_id")
        viewModel.startQuiz(categoryId)
    }

    private fun initBackgroundMusic() {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.bgm_oquiz).apply {
            isLooping = true
            setVolume(0.3f, 0.3f)
        }
    }

    private fun initSoundEffects() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()

        soundRightId = soundPool?.load(requireContext(), R.raw.bgm_right, 1) ?: 0
        soundWrongId = soundPool?.load(requireContext(), R.raw.bgm_wrong, 1) ?: 0
    }

    private fun playAnswerSound(isCorrect: Boolean) {
        val soundId = if (isCorrect) soundRightId else soundWrongId
        if (soundId != 0) {
            soundPool?.play(soundId, 0.5f, 0.5f, 0, 0, 1.0f)
        }
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    private fun setupListeners() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitConfirmationDialog()
                }
            }
        )

        binding.btnBack.setOnClickListener { showExitConfirmationDialog() }
        binding.btnSubmit.setOnClickListener { submitCurrentAnswer() }
        binding.btnSkip.setOnClickListener {
            viewModel.skipQuestion()
            clearInput()
        }

        binding.edtAnswer.setOnEditorActionListener { _, actionId, event ->
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId in listOf(EditorInfo.IME_ACTION_SEND, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_GO) || isEnter) {
                submitCurrentAnswer()
                true
            } else false
        }

        binding.edtAnswer.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode in listOf(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                submitCurrentAnswer()
                true
            } else false
        }
    }

    private fun submitCurrentAnswer() {
        val session = viewModel.quizSessionState.value ?: return
        val currentIndex = session.currentQuestionIndex.toInt()
        val question = session.questions.getOrNull(currentIndex) ?: return

        val userAnswer = binding.edtAnswer.text.toString()
        viewModel.answerQuestion(userAnswer, question.answers)
        clearInput()
    }

    private fun clearInput() {
        binding.edtAnswer.setText("")
        binding.edtAnswer.requestFocus()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.quizSessionState.collect { session ->
                        session?.let { renderSession(it) }
                    }
                }
                launch {
                    viewModel.answerResultEvent.collect { isCorrect ->
                        playAnswerSound(isCorrect)
                    }
                }
            }
        }
    }

    private fun renderSession(session: com.example.sieve_of_wisdom.data.model.QuizSession) {
        val currentIndex = session.currentQuestionIndex.toInt()
        val question = session.questions.getOrNull(currentIndex) ?: return

        binding.tvQuestionNumber.text = "Câu ${currentIndex + 1} / ${session.questions.size}"
        binding.tvScore.text = "Điểm: ${session.score}"
        binding.tvQuestion.text = question.asking

        binding.edtAnswer.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtAnswer, InputMethodManager.SHOW_IMPLICIT)

        binding.tvTimer.text = "${session.timeLeft}s"
        binding.progressTimer.max = 60
        binding.progressTimer.progress = session.timeLeft

        if (session.isFinished) navigateToResult()
    }

    private fun navigateToResult() {
        findNavController().navigate(R.id.action_quizFragment_to_resultFragment)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Thoát lượt chơi?")
            .setMessage("Tiến trình hiện tại sẽ bị hủy và không ghi nhận điểm số.")
            .setPositiveButton("Thoát") { _, _ -> findNavController().popBackStack(R.id.homeFragment, false) }
            .setNegativeButton("Tiếp tục chơi", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        soundPool?.release()
        soundPool = null

        _binding = null
    }
}