package com.vivo.vivorajonboarding.network;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    private final RequestBody delegate;
    private final UploadCallback callback;
    private final String fileName;
    private final int iconResource;
    private final int fileIndex;
    private final int totalFiles;

    public interface UploadCallback {
        void onProgressUpdate(int fileIndex, int totalFiles, String fileName, int iconResource, int progress);
    }

    public ProgressRequestBody(RequestBody delegate, String fileName, int iconResource, int fileIndex, int totalFiles, UploadCallback callback) {
        this.delegate = delegate;
        this.callback = callback;
        this.fileName = fileName;
        this.iconResource = iconResource;
        this.fileIndex = fileIndex;
        this.totalFiles = totalFiles;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(new CountingSink(sink));
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;
        private final Handler handler = new Handler(Looper.getMainLooper());

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;

            try {
                long contentLength = contentLength();
                int progress = (int) (100 * bytesWritten / contentLength);

                handler.post(() -> callback.onProgressUpdate(
                        fileIndex,
                        totalFiles,
                        fileName,
                        iconResource,
                        progress
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}