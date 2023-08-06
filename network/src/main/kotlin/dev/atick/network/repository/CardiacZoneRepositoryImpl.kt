package dev.atick.network.repository

import com.orhanobut.logger.Logger
import dev.atick.network.api.CardiacZoneApi
import dev.atick.network.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CardiacZoneRepositoryImpl @Inject constructor(
    private val cardiacZoneAPi: CardiacZoneApi
) : CardiacZoneRepository {

    private val abnormalEcgList = mutableListOf<Ecg>()
    private val _abnormalEcg = MutableStateFlow<List<Ecg>>(listOf())
    override val abnormalEcg: StateFlow<List<Ecg>>
        get() = _abnormalEcg.asStateFlow()

    private val _error = MutableStateFlow<Exception?>(null)
    override val error: StateFlow<Exception?>
        get() = _error.asStateFlow()

    override suspend fun login(request: LoginRequest): LoginResponse? {
        return try {
            cardiacZoneAPi.login(request)
        } catch (e: Exception) {
            Logger.e("LOGIN ATTEMPT UNSUCCESSFUL! ${e.message}")
            _error.value = e
            null
        }
    }

    override suspend fun pushEcg(request: EcgRequest): PushEcgResponse? {
        return try {
            val response = cardiacZoneAPi.pushEcg(request)
            response?.ecg?.let { ecg ->
                if (ecg.vBeats.isNotEmpty() || ecg.sBeats.isNotEmpty() || ecg.af == 1) {
                    abnormalEcgList.add(0, ecg)
                    _abnormalEcg.value = abnormalEcgList.toList()
                }
            }
            response
        } catch (e: Exception) {
            Logger.e("ECG DATA NOT SENT! ${e.message}")
            _error.value = e
            null
        }
    }

    override suspend fun pushAudio(patientId: String, file: File): Boolean {
        return try {
            val audio = MultipartBody.Part.createFormData(
                name = "file",
                filename = file.name,
                body = file.asRequestBody("audio/*".toMediaTypeOrNull()),
            )
            val response = cardiacZoneAPi.pushAudio(patientId, audio)
            response.success
        } catch (e: Exception) {
            Logger.e("AUDIO NOT SENT! ${e.message}")
            _error.value = e
            false
        }
    }

    override suspend fun connectDoctor(connectDoctorRequest: ConnectDoctorRequest): Boolean {
        return try {
            val response = cardiacZoneAPi.connectDoctor(connectDoctorRequest)
            return response?.success ?: false
        } catch (e: Exception) {
            Logger.e("ERROR ADDING DOCTOR! ${e.message}")
            _error.value = e
            false
        }
    }
}