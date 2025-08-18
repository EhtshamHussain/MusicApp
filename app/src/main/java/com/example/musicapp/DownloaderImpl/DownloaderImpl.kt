package com.example.musicapp.DownloaderImpl

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Response
import java.io.IOException
import org.schabi.newpipe.extractor.downloader.Request as NewPipeRequest



class DownloaderImpl private constructor() : Downloader() {
    companion object {
        private val instance: DownloaderImpl by lazy { DownloaderImpl() }
        fun init(client: OkHttpClient?): DownloaderImpl {
            return instance
        }
    }

    private val client = OkHttpClient()

    override fun execute(request: NewPipeRequest): Response {
        val builder = Request.Builder()
            .url(request.url())
            .method(request.httpMethod(), request.dataToSend()?.toRequestBody())

        // Add headers
        request.headers().forEach { (key, values) ->
            values.forEach { value -> builder.addHeader(key, value) }
        }

        val okHttpRequest = builder.build()
        val response = client.newCall(okHttpRequest).execute()

        if (!response.isSuccessful) {
            throw IOException("Response was not successful: ${response.code}")
        }

        val headers = mutableMapOf<String, List<String>>()
        response.headers.toMultimap().forEach { (key, values) ->
            headers[key] = values
        }

        return Response(
            response.code,
            response.message,
            headers,
            response.body?.string(),
            response.request.url.toString()
        )
    }
}