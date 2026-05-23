package com.bacbpl.iptv.jetStram.data.repositories

import com.bacbpl.iptv.jetStram.data.models.GenreResponse
import com.bacbpl.iptv.jetStram.data.network.ApiService
import retrofit2.HttpException
import java.io.IOException

class GenreRepository(
    private val apiService: ApiService
) {
    suspend fun getGenres(): Result<GenreResponse> {
        return try {
            val response = apiService.getGenres()
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