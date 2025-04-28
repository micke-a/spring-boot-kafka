package me.mikael.springbootkafka.tracing;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TracingSupport {
    private final Tracer tracer;

    public void withNewTrace(String traceId, String spanId, Map<String,String> metadata, Runnable runnable) {
        TraceContext tc = tracer.traceContextBuilder()
                .traceId(traceId)
                .spanId(spanId)
                .sampled(false)
                .build();

        try (var sc = tracer.currentTraceContext().newScope(tc)) {
            try(var baggageInScope = this.tracer.createBaggageInScope(tc,"baggage-1", "value-1")) {
                runnable.run();
            }
        }
    }

    public void withNewTrace(Headers headers, Runnable runnable) {
        TraceContext tc = tracer.traceContextBuilder()
                .traceId(traceId)
                .spanId(spanId)
                .sampled(false)
                .build();

        try (var sc = tracer.currentTraceContext().newScope(tc)) {
            try(var baggageInScope = this.tracer.createBaggageInScope(tc,"baggage-1", "value-1")) {
                runnable.run();
            }
        }
    }
}
