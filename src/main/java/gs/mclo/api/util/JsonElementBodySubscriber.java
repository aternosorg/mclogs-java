package gs.mclo.api.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public final class JsonElementBodySubscriber implements HttpResponse.BodySubscriber<JsonElement> {
    private final CompletableFuture<JsonElement> result = new CompletableFuture<>();
    private final PipedOutputStream outputStream = new PipedOutputStream();
    private final JsonReader reader;

    private @Nullable Flow.Subscription subscription;

    public JsonElementBodySubscriber(Gson gson) {
        try {
            reader = gson.newJsonReader(new InputStreamReader(new PipedInputStream(outputStream), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public CompletionStage<JsonElement> getBody() {
        return result;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(List<ByteBuffer> items) {
        try {
            for (ByteBuffer byteBuffer : items) {
                byte[] bytes = new byte[byteBuffer.remaining()];
                byteBuffer.get(bytes);
                outputStream.write(bytes);
            }
        } catch (Exception e) {
            result.completeExceptionally(e);
            if (subscription != null) {
                subscription.cancel();
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        result.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        result.complete(JsonParser.parseReader(reader));
    }
}
