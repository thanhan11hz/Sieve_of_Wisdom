package com.example.sieve_of_wisdom.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.sieve_of_wisdom.data.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncRepository: SyncRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("SYNC_DEBUG", "----------- WORKER START -----------")
        val result = syncRepository.syncAllData()
        Log.d(
            "SYNC_DEBUG",
            "SYNC RESULT = ${result.isSuccess}"
        )
        return if (result.isSuccess) {
            Log.d("SYNC_DEBUG", "----------- WORKER SUCCESS -----------")
            Result.success()
        } else {
            Log.e("SYNC_DEBUG", "----------- WORKER RETRY -----------")

            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "SyncDataWork"

        fun enqueueSync(context: Context) {
            Log.d(
                "SYNC_DEBUG",
                "Enqueue sync worker"
            )
            Log.d(
                "SYNC_DEBUG",
                "Enqueue sync worker"
            )
            
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            Log.d("SYNC_DEBUG","GET CONSTRAINTS")

            val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
               .setConstraints(constraints)
                .build()

            Log.d("SYNC_DEBUG","get request")

            val workManager = WorkManager.getInstance(context)

            Log.d("SYNC_DEBUG","New WorkRequest ID = ${syncRequest.id}")
        
            val existingWork =
                workManager
                    .getWorkInfosForUniqueWork(WORK_NAME)
                    .get()
            
            existingWork.forEach {
                Log.d("SYNC_DEBUG","Existing work: id=${it.id}, state=${it.state}")
            }
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.KEEP,
                syncRequest
            )

            Log.d("SYNC_DEBUG","enqueueUniqueWork DONE")

           // NOTE: enqueue đã ok, nhưng doWork chưa dc gọi?? worker chưa run hẻ??

        }

        fun cancelSync(context: Context) {
            Log.d(
                "SYNC_DEBUG",
                "Cancel sync worker"
            )
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}