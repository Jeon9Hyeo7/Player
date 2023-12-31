package com.phoenix.phoenixplayer2

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import java.io.IOException

@Suppress("DEPRECATION")
class DefaultDataSource(context: Context, baseDataSource: DataSource?) :
    DataSource {
    class Factory @JvmOverloads constructor(
        context: Context,
        baseDataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
    ) :
        DataSource.Factory {
        private val context: Context
        private val baseDataSourceFactory: DataSource.Factory
        private var transferListener: TransferListener? = null


        fun setTransferListener(transferListener: TransferListener?): Factory {
            this.transferListener = transferListener
            return this
        }

        override fun createDataSource(): DefaultDataSource {
            val dataSource = DefaultDataSource(context, baseDataSourceFactory.createDataSource())
            if (transferListener != null) {
                dataSource.addTransferListener(transferListener!!)
            }
            return dataSource
        }
        /**
         * Creates an instance.
         *
         * @param context A context.
         * @param baseDataSourceFactory The [DataSource.Factory] to be used to create base [     ] for [DefaultDataSource] instances. The base [     ] is normally an [HttpDataSource], and is responsible for fetching data
         * over HTTP and HTTPS, as well as any other URI schemes not otherwise supported by [     ].
         */
        /**
         * Creates an instance.
         *
         * @param context A context.
         */
        init {
            this.context = context.applicationContext
            this.baseDataSourceFactory = baseDataSourceFactory
        }
    }

    private val context: Context
    private val transferListeners: MutableList<TransferListener>
    private val baseDataSource: DataSource

    // Lazily initialized.
    private var fileDataSource: DataSource? = null
    private var assetDataSource: DataSource? = null
    private var contentDataSource: DataSource? = null
    private var rtmpDataSource: DataSource? = null
    private var udpDataSource: DataSource? = null
    private var dataSchemeDataSource: DataSource? = null
    private var rawResourceDataSource: DataSource? = null
    private var dataSource: DataSource? = null

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param allowCrossProtocolRedirects Whether to allow cross-protocol redirects.
     */
    constructor(context: Context, allowCrossProtocolRedirects: Boolean) : this(
        context,  /* userAgent= */
        null,
        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
        allowCrossProtocolRedirects
    ) {
    }

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param userAgent The user agent that will be used when requesting remote data, or `null`
     * to use the default user agent of the underlying platform.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
     * to HTTPS and vice versa) are enabled when fetching remote data.
     */
    constructor(
        context: Context, userAgent: String?, allowCrossProtocolRedirects: Boolean
    ) : this(
        context,
        userAgent,
        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
        allowCrossProtocolRedirects
    ) {
    }

    /**
     * Constructs a new instance, optionally configured to follow cross-protocol redirects.
     *
     * @param context A context.
     * @param userAgent The user agent that will be used when requesting remote data, or `null`
     * to use the default user agent of the underlying platform.
     * @param connectTimeoutMillis The connection timeout that should be used when requesting remote
     * data, in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param readTimeoutMillis The read timeout that should be used when requesting remote data, in
     * milliseconds. A timeout of zero is interpreted as an infinite timeout.
     * @param allowCrossProtocolRedirects Whether cross-protocol redirects (i.e. redirects from HTTP
     * to HTTPS and vice versa) are enabled when fetching remote data.
     */
    constructor(
        context: Context,
        userAgent: String?,
        connectTimeoutMillis: Int,
        readTimeoutMillis: Int,
        allowCrossProtocolRedirects: Boolean
    ) : this(
        context,
        DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setConnectTimeoutMs(connectTimeoutMillis)
            .setReadTimeoutMs(readTimeoutMillis)
            .setAllowCrossProtocolRedirects(allowCrossProtocolRedirects)
            .createDataSource()
    ) {
    }

    override fun addTransferListener(transferListener: TransferListener) {
        Assertions.checkNotNull(transferListener)
        baseDataSource.addTransferListener(transferListener)
        transferListeners.add(transferListener)
        maybeAddListenerToDataSource(fileDataSource, transferListener)
        maybeAddListenerToDataSource(assetDataSource, transferListener)
        maybeAddListenerToDataSource(contentDataSource, transferListener)
        maybeAddListenerToDataSource(rtmpDataSource, transferListener)
        maybeAddListenerToDataSource(udpDataSource, transferListener)
        maybeAddListenerToDataSource(dataSchemeDataSource, transferListener)
        maybeAddListenerToDataSource(rawResourceDataSource, transferListener)
    }

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        Assertions.checkState(dataSource == null)
        // Choose the correct source for the scheme.
        val scheme = dataSpec.uri.scheme
        dataSource = if (Util.isLocalFileUri(dataSpec.uri)) {
            val uriPath = dataSpec.uri.path
            if (uriPath != null && uriPath.startsWith("/android_asset/")) {
                getAssetDataSource()
            } else {
                getFileDataSource()
            }
        } else if (SCHEME_ASSET == scheme) {
            getAssetDataSource()
        } else if (SCHEME_CONTENT == scheme) {
            getContentDataSource()
        } else if (SCHEME_RTMP == scheme) {
            getRtmpDataSource()
        } else if (SCHEME_UDP == scheme) {
            getUdpDataSource()
        } else if (SCHEME_DATA == scheme) {
            getDataSchemeDataSource()
        } else if (SCHEME_RAW == scheme || SCHEME_ANDROID_RESOURCE == scheme) {
            getRawResourceDataSource()
        } else {
            baseDataSource
        }
        // Open the source and return.
        return dataSource!!.open(dataSpec)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return Assertions.checkNotNull(dataSource).read(buffer, offset, length)
    }

    override fun getUri(): Uri? {
        return if (dataSource == null) null else dataSource!!.uri
    }

    override fun getResponseHeaders(): Map<String, List<String>> {
        return if (dataSource == null) emptyMap() else dataSource!!.responseHeaders
    }

    @Throws(IOException::class)
    override fun close() {
        if (dataSource != null) {
            try {
                dataSource!!.close()
            } finally {
                dataSource = null
            }
        }
    }

    private fun getUdpDataSource(): DataSource {
        if (udpDataSource == null) {
            udpDataSource = UdpDataSource()
            addListenersToDataSource(udpDataSource)
        }
        return udpDataSource as DataSource
    }

    private fun getFileDataSource(): DataSource {
        if (fileDataSource == null) {
            fileDataSource = FileDataSource()
            addListenersToDataSource(fileDataSource)
        }
        return fileDataSource as DataSource
    }

    private fun getAssetDataSource(): DataSource {
        if (assetDataSource == null) {
            assetDataSource = AssetDataSource(context)
            addListenersToDataSource(assetDataSource)
        }
        return assetDataSource as DataSource
    }

    private fun getContentDataSource(): DataSource {
        if (contentDataSource == null) {
            contentDataSource = ContentDataSource(context)
            addListenersToDataSource(contentDataSource)
        }
        return contentDataSource as DataSource
    }

    private fun getRtmpDataSource(): DataSource? {
        if (rtmpDataSource == null) {
            try {
                val clazz = Class.forName("com.google.android.exoplayer2.ext.rtmp.RtmpDataSource")
                rtmpDataSource = clazz.getConstructor().newInstance() as DataSource
                addListenersToDataSource(rtmpDataSource)
            } catch (e: ClassNotFoundException) {
                // Expected if the app was built without the RTMP extension.
                Log.w(TAG, "Attempting to play RTMP stream without depending on the RTMP extension")
            } catch (e: Exception) {
                // The RTMP extension is present, but instantiation failed.
                throw RuntimeException("Error instantiating RTMP extension", e)
            }
            if (rtmpDataSource == null) {
                rtmpDataSource = baseDataSource
            }
        }
        return rtmpDataSource
    }

    private fun getDataSchemeDataSource(): DataSource {
        if (dataSchemeDataSource == null) {
            dataSchemeDataSource = DataSchemeDataSource()
            addListenersToDataSource(dataSchemeDataSource)
        }
        return dataSchemeDataSource as DataSource
    }

    private fun getRawResourceDataSource(): DataSource {
        if (rawResourceDataSource == null) {
            rawResourceDataSource = RawResourceDataSource(context)
            addListenersToDataSource(rawResourceDataSource)
        }
        return rawResourceDataSource as DataSource
    }

    private fun addListenersToDataSource(dataSource: DataSource?) {
        for (i in transferListeners.indices) {
            dataSource!!.addTransferListener(transferListeners[i])
        }
    }

    private fun maybeAddListenerToDataSource(
        dataSource: DataSource?, listener: TransferListener
    ) {
        dataSource?.addTransferListener(listener)
    }

    companion object {
        private const val TAG = "DefaultDataSource"
        private const val SCHEME_ASSET = "asset"
        private const val SCHEME_CONTENT = "content"
        private const val SCHEME_RTMP = "rtmp"
        private const val SCHEME_UDP = "udp"
        private const val SCHEME_DATA = DataSchemeDataSource.SCHEME_DATA
        private const val SCHEME_RAW = RawResourceDataSource.RAW_RESOURCE_SCHEME
        private const val SCHEME_ANDROID_RESOURCE = ContentResolver.SCHEME_ANDROID_RESOURCE
    }

    /**
     * Constructs a new instance that delegates to a provided [DataSource] for URI schemes other
     * than file, asset and content.
     *
     * @param context A context.
     * @param baseDataSource A [DataSource] to use for URI schemes other than file, asset and
     * content. This [DataSource] should normally support at least http(s).
     */
    init {
        this.context = context.applicationContext
        this.baseDataSource = Assertions.checkNotNull(baseDataSource)
        transferListeners = ArrayList()
    }
}