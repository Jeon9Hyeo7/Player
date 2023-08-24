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
        val connectManager = ConnectManager(serverUrl, macAddress, token)
        mRepository = TvRepository(context = context)
        val allChannels = mRepository.getChannelsMap()
        allChannels.forEach {
            val list = it.value
            list.forEach {channel->
                val programs = connectManager.getAllProgramsForChannel(channel = channel, 0)
                if (programs != null){
                    if (programs.isNotEmpty()){
                        updatePrograms(channel, programs = programs)
                    }
                }
            }
        }
    }

    private fun updatePrograms(channel: Channel, programs: List<Program>){
        val oldPrograms = TvRepository.getPrograms(contentResolver,
            channelUri = channel.getUri())!!
        val newPrograms = programs
        val fetchedProgramsCount = newPrograms.size
        val firstNewProgram = newPrograms[0]
        var oldProgramsIndex = 0
        var newProgramsIndex = 0
        // Skip the past programs. They will be automatically removed by the system.
        // Skip the past programs. They will be automatically removed by the system.
        for (program in oldPrograms) {
            if (program.endTimeMillis!! < System.currentTimeMillis()
                || program.endTimeMillis!! < firstNewProgram.startTimeMillis!!
            ) {
                oldProgramsIndex++
            } else {
                break
            }
        }
        // Compare the new programs with old programs one by one and update/delete the old one
        // or insert new program if there is no matching program in the database.
        val ops = ArrayList<ContentProviderOperation>()
        while (newProgramsIndex < fetchedProgramsCount) {
            val oldProgram =
                if (oldProgramsIndex < oldPrograms.size) oldPrograms[oldProgramsIndex]
                else null
            val newProgram = newPrograms[newProgramsIndex]
            var addNewProgram = false
            if (oldProgram != null) {
                if (oldProgram.equals(newProgram)) {
                    // Exact match. No need to update. Move on to the next programs.
                    oldProgramsIndex++
                    newProgramsIndex++
                } else if (shouldUpdateProgramMetadata(oldProgram, newProgram)) {
                    // Partial match. Update the old program with the new one.
                    // NOTE: Use 'update' in this case instead of 'insert' and 'delete'. There
                    // could be application specific settings which belong to the old program.
                    ops.add(
                        ContentProviderOperation.newUpdate(
                            TvContract.buildProgramUri(oldProgram.id!!)
                        )
                            .withValues(newProgram.toContentValues())
                            .build()
                    )

                    oldProgramsIndex++
                    newProgramsIndex++
                } else if (oldProgram.endTimeMillis!!
                    < newProgram.endTimeMillis!!
                ) {
                    // No match. Remove the old program first to see if the next program in
                    // {@code oldPrograms} partially matches the new program.
                    ops.add(
                        ContentProviderOperation.newDelete(
                            TvContract.buildProgramUri(oldProgram.id!!)
                        )
                            .build()
                    )

                    oldProgramsIndex++
                } else {
                    // No match. The new program does not match any of the old programs. Insert
                    // it as a new program.
                    addNewProgram = true
                    newProgramsIndex++

                }
            } else {
                // No old programs. Just insert new programs.
                addNewProgram = true
                newProgramsIndex++
            }
            if (addNewProgram) {
                ops.add(
                    ContentProviderOperation.newInsert(TvContract.Programs.CONTENT_URI)
                        .withValues(newProgram.toContentValues())
                        .build()
                )

            }
            // Throttle the batch operation not to cause TransactionTooLargeException.
            if (ops.size > 100
                || newProgramsIndex >= fetchedProgramsCount
            ) {
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

                }

                ops.clear()
            }
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