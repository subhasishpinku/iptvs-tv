// data/repositories/GenreChannelRepository.kt
package com.bacbpl.iptv.jetStram.data.repositories

import com.bacbpl.iptv.jetStram.data.models.GenreChannelResponse
import com.bacbpl.iptv.jetStram.data.network.ApiService
import retrofit2.HttpException
import java.io.IOException

class GenreChannelRepository(
    private val apiService: ApiService
) {
    suspend fun getChannelsByGenre(genreId: Int): Result<GenreChannelResponse> {
        return try {
            val response = apiService.getChannelsByGenre(genreId)
            if (response.status) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}