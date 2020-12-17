package github.sachin2dehury.nitrmail.repository

import android.app.Application
import github.sachin2dehury.nitrmail.api.calls.MailApi
import github.sachin2dehury.nitrmail.api.data.entities.Mail
import github.sachin2dehury.nitrmail.api.data.local.MailDao
import github.sachin2dehury.nitrmail.others.Resource
import github.sachin2dehury.nitrmail.others.checkForInternetConnection
import github.sachin2dehury.nitrmail.others.networkBoundResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val mailApi: MailApi,
    private val mailDao: MailDao,
    private val context: Application
) {

    private suspend fun insertMail(mail: Mail) {
        mailDao.insertMail(mail)
    }

    suspend fun insertMails(mails: List<Mail>) {
        mails.forEach { insertMail(it) }
    }

    fun getAllMails(): Flow<Resource<List<Mail>>> {
        return networkBoundResource(
            query = {
                mailDao.getAllMails()
            },
            fetch = {
                mailApi.getMails()
            },
            saveFetchResult = { response ->
                response.body()?.let {

                }
            },
            shouldFetch = {
                checkForInternetConnection(context)
            }
        )
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        try {
            val response = mailApi.getMails()
            if (response.isSuccessful && response.code() == 200) {
                Resource.success(response.body()?.mails)
            } else {
                Resource.error(response.message() ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to the servers. Check your internet connection", null)
        }
    }

}