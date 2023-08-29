package com.phoenix.phoenixplayer2.db.tv

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.media.tv.TvContract
import android.os.RemoteException
import android.util.Log
import com.phoenix.phoenixplayer2.api.ConnectManager
import com.phoenix.phoenixplayer2.api.TimeUtils
import com.phoenix.phoenixplayer2.model.Channel
import com.phoenix.phoenixplayer2.model.Portal
import com.phoenix.phoenixplayer2.model.Program
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ProgramSyncService: JobService() {

    private val mJobScope = CoroutineScope(Dispatchers.IO)
    private lateinit var mRepository: TvRepository
    lateinit var context: Context


    override fun onStartJob(params: JobParameters?): Boolean {
        context = applicationContext
        mJobScope.launch {
            updateTask(params!!)
            jobFinished(params, false)
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        mJobScope.coroutineContext.cancel()
        return true
    }

    private fun updateTask(params: JobParameters){
        val serverParameters =
            params.extras.getStringArray(Portal.PORTAL_JOB_INTENT_TAG)
        val serverUrl = serverParameters!![0]
        val macAddress = serverParameters[1]
        val token = serverParameters[2]
        val timeZone = serverParameters[3]
        val connectManager = ConnectManager(serverUrl, macAddress, token)
        mRepository = TvRepository(context = context)
        val allChannels = mRepository.getChannelsMap()
        contentResolver.delete(TvContract.Programs.CONTENT_URI, null, null)
        allChannels.forEach {
            val list = it.value
            list.forEach {channel->
                val programs = connectManager.getAllProgramsForChannel(channel = channel, 0)
                if (programs != null){
                    if (programs.isNotEmpty()){
                        updatePrograms(channel, programs = programs, zoneId = timeZone)
                    }
                }
            }
        }
    }


    private fun updatePrograms(channel: Channel, programs: List<Program>, zoneId: String){
        val oldPrograms = TvRepository.getPrograms(contentResolver,
            channelUri = channel.getUri())!!
        val newPrograms = programs
        val ops = ArrayList<ContentProviderOperation>()

        for (program in oldPrograms) {
            if (program.startTimeMillis!! < newPrograms[newPrograms.size-1].endTimeMillis!!
            ) {
                val uri = TvContract.buildProgramsUriForChannel(program.channelId!!,
                    program.startTimeMillis!!,
                    program.endTimeMillis!!)

                ops.add(ContentProviderOperation
                    .newDelete(uri)
                    .build())

            }
            if (ops.size > 100
            ) {
                applyBatch(ops)
                ops.clear()
            }
        }
        // Compare the new programs with old programs one by one and update/delete the old one
        // or insert new program if there is no matching program in the database.
        val lastProgramEnd: Long = if (oldPrograms.isEmpty()){
            Long.MIN_VALUE
        }
        else{
            oldPrograms[oldPrograms.size-1].endTimeMillis!!
        }


        for (newProgram in newPrograms) {
            if (lastProgramEnd < newProgram.startTimeMillis!!){
                ops.add(
                    ContentProviderOperation.newInsert(TvContract.Programs.CONTENT_URI)
                        .withValues(newProgram.toContentValues())
                        .build()
                )
                if (ops.size > 100
                ) {
                    applyBatch(ops)
                    ops.clear()
                }
            }

            // Throttle the batch operation not to cause TransactionTooLargeException.
        }
    }



    private fun applyBatch(ops: ArrayList<ContentProviderOperation>){
        try {
            contentResolver.applyBatch(TvContract.AUTHORITY, ops)
        } catch (e: RemoteException) {
            Log.e(
                "TAG",
                "Failed to insert programs.",
                e
            )

            return
        } catch (e: OperationApplicationException) {
            Log.e(
                "TAG",
                "Failed to insert programs.",
                e
            )

            return
        }
        catch (e: IllegalArgumentException){
            /*Log.e(
                "TAG",
                "Failed to insert programs.",
                e
            )*/
        }


    }
    private fun shouldUpdateProgramMetadata(oldProgram: Program, newProgram: Program): Boolean {
        // NOTE: Here, we update the old program if it has the same title and overlaps with the
        // new program. The test logic is just an example and you can modify this. E.g. check
        // whether the both programs have the same program ID if your EPG supports any ID for
        // the programs.
        return (oldProgram.title.equals(newProgram.title)
                && oldProgram.startTimeMillis!! <= newProgram.endTimeMillis!!
                && newProgram.startTimeMillis!! <= oldProgram.endTimeMillis!!)
    }

}